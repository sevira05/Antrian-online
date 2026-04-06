package com.example.antrianonline.ui.antrian

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.antrianonline.data.model.Loket
import com.example.antrianonline.databinding.FragmentAmbilAntrianBinding
import com.example.antrianonline.utils.NotificationHelper
import com.example.antrianonline.utils.SessionManager
import java.text.SimpleDateFormat
import java.util.*

class AmbilAntrianFragment : Fragment() {

    private var _binding: FragmentAmbilAntrianBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: LoketAdapter
    private lateinit var session: SessionManager
    private lateinit var notifHelper: NotificationHelper

    private var selectedLoket: Loket? = null
    private var currentList = mutableListOf<Loket>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAmbilAntrianBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        session = SessionManager(requireContext())
        notifHelper = NotificationHelper(requireContext())

        adapter = LoketAdapter {
            selectedLoket = it
            binding.btnAmbil.isEnabled = true
            binding.tvSelectedLoket.text =
                "✓ ${it.nama} (Sisa: ${it.maxAntrian - it.totalDiambil})"
        }

        binding.rvLoket.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLoket.adapter = adapter

        binding.btnAmbil.setOnClickListener { konfirmasiAmbil() }

        loadData()
    }

    private fun loadData() {
        currentList = mutableListOf(
            Loket(1,"Layanan Umum","-", "Petugas A",20,"08:00","16:00",0,0,"BUKA"),
            Loket(2,"Keuangan","-", "Petugas B",20,"08:00","16:00",0,0,"BUKA"),
            Loket(3,"Kesehatan","-", "Petugas C",20,"08:00","16:00",0,0,"BUKA"),
            Loket(4,"Administrasi","-", "Petugas D",20,"08:00","16:00",0,0,"BUKA")
        )

        adapter.submitList(currentList.toList())
    }

    private fun konfirmasiAmbil() {
        val loket = selectedLoket ?: return

        AlertDialog.Builder(requireContext())
            .setTitle("Konfirmasi")
            .setMessage("Ambil antrian di ${loket.nama}?")
            .setPositiveButton("Ambil") { _, _ -> ambilAntrian(loket) }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun ambilAntrian(loket: Loket) {

        val current = session.getNomorLoket(loket.idLoket) + 1
        session.saveNomorLoket(loket.idLoket, current)

        val noAntrian = "A" + String.format("%03d", current)

        val tanggal = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            .format(Date())

        session.saveTiket(
            no = noAntrian,
            loket = loket.nama,
            posisi = current,
            estimasi = current * 5,
            tanggal = tanggal
        )

        val namaUser = session.getNama() ?: "User"

        val pesan = "Halo $namaUser,\n\nNomor antrian Anda $noAntrian untuk layanan ${loket.nama} telah berhasil dibuat pada $tanggal.\n\n" +
                "Saat ini Anda berada di posisi ke-$current dengan estimasi waktu tunggu sekitar ${current * 5} menit.\n\n" +
                "Silakan menunggu dan pantau menu Monitor agar tidak terlewat."

        notifHelper.showNotification("🎟️ Antrian Berhasil Diambil", pesan, false)

        TiketDialog.newInstance(noAntrian, loket.nama, current, current * 5, tanggal)
            .show(parentFragmentManager, "tiket")

        Toast.makeText(requireContext(), "Nomor Antrian: $noAntrian", Toast.LENGTH_SHORT).show()

        selectedLoket = null
        binding.btnAmbil.isEnabled = false
        binding.tvSelectedLoket.text = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}