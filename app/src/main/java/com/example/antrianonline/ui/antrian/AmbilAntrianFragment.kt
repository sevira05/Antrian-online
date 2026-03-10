package com.example.antrianonline.ui.antrian

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.antrianonline.data.api.RetrofitClient
import com.example.antrianonline.data.model.Loket
import com.example.antrianonline.data.repository.AntrianRepository
import com.example.antrianonline.data.repository.Result
import com.example.antrianonline.databinding.FragmentAmbilAntrianBinding
import com.example.antrianonline.utils.WorkManagerHelper
import com.example.antrianonline.utils.SessionManager
import kotlinx.coroutines.launch

class AmbilAntrianFragment : Fragment() {

    private var _binding: FragmentAmbilAntrianBinding? = null
    private val binding get() = _binding!!

    private lateinit var repo: AntrianRepository
    private lateinit var adapter: LoketAdapter
    private var selectedLoket: Loket? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAmbilAntrianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session = SessionManager(requireContext())
        repo = AntrianRepository(RetrofitClient.getApi(session))

        adapter = LoketAdapter { loket ->
            selectedLoket = loket
            binding.btnAmbil.isEnabled = (loket.statusLoket == "BUKA")
            binding.tvSelectedLoket.text = "Dipilih: ${loket.nama} (Sisa: ${loket.sisaKuota})"
        }

        binding.rvLoket.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLoket.adapter       = adapter

        binding.swipeRefresh.setOnRefreshListener { loadLoket() }
        binding.btnAmbil.setOnClickListener { konfirmasiAmbil() }

        loadLoket()
    }

    private fun loadLoket() {
        binding.swipeRefresh.isRefreshing = true
        lifecycleScope.launch {
            when (val result = repo.getLoketList()) {
                is Result.Success -> {
                    adapter.submitList(result.data)
                    binding.swipeRefresh.isRefreshing = false
                }
                is Result.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun konfirmasiAmbil() {
        val loket = selectedLoket ?: return
        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi Antrian")
            .setMessage("Ambil nomor antrian untuk:\n\n${loket.nama}\nPengelola: ${loket.pengelola}\nSisa kuota: ${loket.sisaKuota}")
            .setPositiveButton("Ambil") { _, _ -> ambilAntrian(loket) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun ambilAntrian(loket: Loket) {
        binding.btnAmbil.isEnabled     = false
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            when (val result = repo.ambilAntrian(loket.idLoket)) {
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val data = result.data

                    // Start background service pantau antrian
                    WorkManagerHelper.startPeriodicCheck(requireContext())

                    // Tampilkan dialog tiket
                    TiketDialog(
                        noAntrian  = data.antrian.noAntrian,
                        namaLoket  = loket.nama,
                        posisi     = data.posisi,
                        estimasi   = data.estimasiMenit,
                        tanggalJam = data.antrian.tanggalJam,
                    ).show(parentFragmentManager, "tiket")

                    loadLoket()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnAmbil.isEnabled     = true
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}