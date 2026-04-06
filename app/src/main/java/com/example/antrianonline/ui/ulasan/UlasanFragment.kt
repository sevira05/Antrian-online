package com.example.antrianonline.ui.ulasan

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.antrianonline.data.model.Ulasan
import com.example.antrianonline.databinding.FragmentUlasanBinding
import java.text.SimpleDateFormat
import java.util.*

class UlasanFragment : Fragment() {

    private var _binding: FragmentUlasanBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: UlasanAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUlasanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter = UlasanAdapter()

        binding.rvUlasan.layoutManager = LinearLayoutManager(requireContext())
        binding.rvUlasan.adapter = adapter

        // 🔥 tombol kirim
        binding.btnKirim.setOnClickListener {
            kirimUlasan()
        }
    }

    private fun kirimUlasan() {
        val nama = binding.etNama.text.toString().trim()
        val ratingText = binding.etRating.text.toString().trim()
        val komentar = binding.etKomentar.text.toString().trim()

        // ✅ VALIDASI
        if (nama.isEmpty() || ratingText.isEmpty() || komentar.isEmpty()) {
            Toast.makeText(requireContext(), "Harap isi semua data!", Toast.LENGTH_SHORT).show()
            return
        }

        val rating = ratingText.toInt()

        if (rating < 1 || rating > 5) {
            Toast.makeText(requireContext(), "Rating harus 1 - 5", Toast.LENGTH_SHORT).show()
            return
        }

        // ✅ TANGGAL
        val tanggal = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            .format(Date())

        // 🔥 BUAT DATA ULASAN
        val ulasan = Ulasan(
            id = System.currentTimeMillis().toInt(),
            nama = nama,
            rating = rating,
            komentar = komentar,
            tanggal = tanggal
        )

        // 🔥 MASUK KE LIST (REAL-TIME)
        adapter.addUlasan(ulasan)

        // 🔥 CLEAR INPUT
        binding.etNama.text.clear()
        binding.etRating.text.clear()
        binding.etKomentar.text.clear()

        Toast.makeText(requireContext(), "Ulasan berhasil dikirim", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}