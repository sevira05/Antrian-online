package com.example.antrianonline.ui.monitor

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.example.antrianonline.databinding.FragmentMonitorBinding
import com.example.antrianonline.utils.SessionManager

class MonitorFragment : Fragment() {

    private var _binding: FragmentMonitorBinding? = null
    private val binding get() = _binding!!

    private lateinit var session: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMonitorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        session = SessionManager(requireContext())

        binding.tvNomorSaya.text = session.getNoAntrian()
        binding.tvSedangDilayani.text = "Sedang Dilayani: ${session.getNoAntrian()}"
        binding.tvPosisi.text = session.getPosisi().toString()
        binding.tvEstimasi.text = "${session.getEstimasi()} menit"
        binding.tvCountdown.text = "⏱ Menunggu giliran..."
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}