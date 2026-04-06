package com.example.antrianonline.ui.antrian

import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import com.example.antrianonline.databinding.DialogTiketBinding

class TiketDialog : DialogFragment() {

    private var _binding: DialogTiketBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(no: String, loket: String, posisi: Int, estimasi: Int, tanggal: String)
                = TiketDialog().apply {
            arguments = Bundle().apply {
                putString("no", no)
                putString("loket", loket)
                putInt("posisi", posisi)
                putInt("estimasi", estimasi)
                putString("tanggal", tanggal)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogTiketBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tvNoAntrian.text = arguments?.getString("no")
        binding.tvNamaLoket.text = arguments?.getString("loket")
        binding.tvPosisi.text = "Ke-${arguments?.getInt("posisi")}"
        binding.tvEstimasi.text = "${arguments?.getInt("estimasi")} menit"
        binding.tvTanggalJam.text = arguments?.getString("tanggal")

        binding.btnTutup.setOnClickListener { dismiss() }
    }
}