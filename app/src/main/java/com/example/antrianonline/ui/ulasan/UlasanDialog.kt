package com.example.antrianonline.ui.ulasan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import com.example.antrianonline.data.api.RetrofitClient
import com.example.antrianonline.data.model.UlasanRequest
import com.example.antrianonline.databinding.DialogUlasanBinding
import com.example.antrianonline.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.*

class UlasanDialog : BottomSheetDialogFragment() {

    private var _binding: DialogUlasanBinding? = null
    private val binding get() = _binding!!

    companion object {

        private const val ARG_LOKET_ID = "loket_id"
        private const val ARG_RIWAYAT_ID = "riwayat_id"
        private const val ARG_NAMA_LOKET = "nama_loket"

        fun newInstance(idLoket: Int, idRiwayat: Int, namaLoket: String): UlasanDialog {

            return UlasanDialog().apply {

                arguments = Bundle().apply {

                    putInt(ARG_LOKET_ID, idLoket)
                    putInt(ARG_RIWAYAT_ID, idRiwayat)
                    putString(ARG_NAMA_LOKET, namaLoket)

                }

            }

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DialogUlasanBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val idLoket = arguments?.getInt(ARG_LOKET_ID) ?: return
        val idRiwayat = arguments?.getInt(ARG_RIWAYAT_ID) ?: return
        val namaLoket = arguments?.getString(ARG_NAMA_LOKET) ?: ""

        isCancelable = false

        binding.tvNamaLoket.text = namaLoket
        binding.ratingBar.rating = 5f

        updateLabelRating(5)

        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ ->
            updateLabelRating(rating.toInt())
        }

        binding.btnKirim.setOnClickListener {

            val rating = binding.ratingBar.rating.toInt()
            val komentar = binding.etKomentar.text.toString().trim().ifEmpty { null }

            kirimUlasan(idLoket, idRiwayat, rating, komentar)

        }

        binding.tvLewati.setOnClickListener {

            dismiss()

        }

    }

    private fun updateLabelRating(rating: Int) {

        binding.tvRatingLabel.text = when (rating) {

            5 -> "Luar Biasa! ⭐⭐⭐⭐⭐"
            4 -> "Sangat Baik ⭐⭐⭐⭐"
            3 -> "Cukup Baik ⭐⭐⭐"
            2 -> "Kurang Baik ⭐⭐"
            else -> "Mengecewakan ⭐"

        }

    }

    private fun kirimUlasan(
        idLoket: Int,
        idRiwayat: Int,
        rating: Int,
        komentar: String?
    ) {

        binding.progressBar.isVisible = true
        binding.btnKirim.isEnabled = false
        binding.tvLewati.isEnabled = false

        val session = SessionManager(requireContext())
        val api = RetrofitClient.getApi(session)

        CoroutineScope(Dispatchers.IO).launch {

            try {

                val request = UlasanRequest(
                    idLoket = idLoket,
                    idRiwayat = idRiwayat,
                    rating = rating,
                    komentar = komentar
                )

                val response = api.kirimUlasan(request)

                withContext(Dispatchers.Main) {

                    binding.progressBar.isVisible = false

                    if (response.isSuccessful && response.body()?.success == true) {

                        tampilkanSukses()

                    } else {

                        val msg = response.body()?.message ?: "Gagal mengirim ulasan"
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

                        binding.btnKirim.isEnabled = true
                        binding.tvLewati.isEnabled = true

                    }

                }

            } catch (e: Exception) {

                withContext(Dispatchers.Main) {

                    binding.progressBar.isVisible = false
                    binding.btnKirim.isEnabled = true
                    binding.tvLewati.isEnabled = true

                    Toast.makeText(
                        requireContext(),
                        "Koneksi bermasalah, coba lagi",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }

        }

    }

    private fun tampilkanSukses() {

        binding.layoutForm.isVisible = false
        binding.layoutSukses.isVisible = true

        binding.root.postDelayed({

            dismiss()

        }, 2000)

    }

    override fun onDestroyView() {

        super.onDestroyView()
        _binding = null

    }

}