package com.example.antrianonline.ui.antrian

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.antrianonline.R
import com.example.antrianonline.databinding.DialogTiketBinding

class TiketDialog(
    private val noAntrian: String,
    private val namaLoket: String,
    private val posisi: Int,
    private val estimasi: Int,
    private val tanggalJam: String,
) : DialogFragment() {

    private var _binding: DialogTiketBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogTiketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvNoAntrian.text    = noAntrian
        binding.tvNamaLoket.text    = namaLoket
        binding.tvPosisi.text       = "Posisi: $posisi"
        binding.tvEstimasi.text     = "Estimasi tunggu: ~$estimasi menit"
        binding.tvTanggalJam.text   = tanggalJam
        binding.tvInfo.text         = "⚠️ Anda akan mendapat notifikasi 30 menit sebelum giliran dipanggil."

        binding.btnTutup.setOnClickListener { dismiss() }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
