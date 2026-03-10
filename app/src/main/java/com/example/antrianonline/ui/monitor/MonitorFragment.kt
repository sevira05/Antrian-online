package com.example.antrianonline.ui.monitor

import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.antrianonline.data.api.RetrofitClient
import com.example.antrianonline.data.repository.AntrianRepository
import com.example.antrianonline.data.repository.Result
import com.example.antrianonline.databinding.FragmentMonitorBinding
import com.example.antrianonline.utils.NotificationHelper
import com.example.antrianonline.utils.SessionManager
import kotlinx.coroutines.launch

class MonitorFragment : Fragment() {

    private var _binding: FragmentMonitorBinding? = null
    private val binding get() = _binding!!

    private lateinit var repo: AntrianRepository
    private lateinit var adapter: MonitorAdapter
    private lateinit var notifHelper: NotificationHelper
    private val handler = Handler(Looper.getMainLooper())
    private var selectedLoketId = 1
    private var lastStatus: String? = null
    private var myNoAntrian: String? = null
    private var countDownTimer: CountDownTimer? = null

    // Simpan estimasi terakhir agar tidak reset setiap refresh
    private var lastEstimasiMenit: Int = -1

    private val refreshRunnable = object : Runnable {
        override fun run() {
            loadMonitor()
            handler.postDelayed(this, 10_000)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMonitorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session = SessionManager(requireContext())
        repo = AntrianRepository(RetrofitClient.getApi(session))
        notifHelper = NotificationHelper(requireContext())

        adapter = MonitorAdapter()
        binding.rvAntrian.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAntrian.adapter = adapter

        loadAntrianSaya()
        binding.swipeRefresh.setOnRefreshListener { loadMonitor() }
    }

    private fun loadAntrianSaya() {
        lifecycleScope.launch {
            when (val result = repo.getAntrianSaya()) {
                is Result.Success -> {
                    val aktif = result.data.firstOrNull {
                        it.status == "menunggu" || it.status == "dipanggil"
                    }
                    if (aktif != null) {
                        selectedLoketId = aktif.loket?.idLoket ?: 1
                        myNoAntrian     = aktif.noAntrian
                        lastStatus      = aktif.status
                        binding.tvNomorSaya.text = aktif.noAntrian ?: "-"
                    } else {
                        binding.tvNomorSaya.text  = "-"
                        binding.tvPosisi.text     = "-"
                        binding.tvEstimasi.text   = "-"
                        binding.tvCountdown.text  = "⏱ Belum ada antrian aktif"
                    }
                    loadMonitor()
                }
                is Result.Error -> loadMonitor()
                else -> {}
            }
        }
    }

    private fun startCountdown(estimasiMenit: Int) {
        // Hanya restart countdown jika estimasi BERUBAH (beda posisi)
        if (estimasiMenit == lastEstimasiMenit) return
        lastEstimasiMenit = estimasiMenit

        countDownTimer?.cancel()

        if (estimasiMenit <= 0) {
            binding.tvCountdown.text = "🔔 Sebentar lagi giliran Anda!"
            return
        }

        val totalMs = estimasiMenit * 60 * 1000L
        countDownTimer = object : CountDownTimer(totalMs, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (_binding == null) return
                val menit = millisUntilFinished / 1000 / 60
                val detik = millisUntilFinished / 1000 % 60
                binding.tvCountdown.text = "⏱ Estimasi tunggu: %02d:%02d".format(menit, detik)
            }
            override fun onFinish() {
                if (_binding == null) return
                binding.tvCountdown.text = "🔔 Sebentar lagi giliran Anda!"
            }
        }.start()
    }

    private fun loadMonitor() {
        binding.swipeRefresh.isRefreshing = true
        lifecycleScope.launch {
            when (val result = repo.getMonitor(selectedLoketId)) {
                is Result.Success -> {
                    val data = result.data
                    binding.tvSedangDilayani.text = "Sedang Dilayani: ${data.sedangDilayani}"
                    binding.tvTotalMenunggu.text  = "Menunggu: ${data.totalMenunggu} orang"
                    adapter.submitList(data.daftarAntrian)
                    binding.swipeRefresh.isRefreshing = false

                    myNoAntrian?.let { noAntrian ->
                        val list    = data.daftarAntrian
                        val myIndex = list.indexOfFirst { it.noAntrian == noAntrian }

                        if (myIndex >= 0) {
                            // Posisi 1 = giliran berikutnya, estimasi = posisi * 5 menit
                            // Posisi 1 → 0 menit (hampir dipanggil)
                            // Posisi 2 → 5 menit, dst
                            val estimasi = if (myIndex == 0) 0 else myIndex * 5
                            binding.tvPosisi.text   = "${myIndex + 1}"
                            binding.tvEstimasi.text = if (estimasi == 0) "< 1" else "$estimasi"
                            startCountdown(estimasi)
                        }

                        // Cek perubahan status
                        val myAntrian = list.firstOrNull { it.noAntrian == noAntrian }
                        myAntrian?.let {
                            val newStatus = it.status
                            if (newStatus != lastStatus) {
                                when (newStatus) {
                                    "dipanggil" -> {
                                        notifHelper.showNotification(
                                            "🔔 Giliran Anda!",
                                            "Nomor $noAntrian dipanggil! Segera menuju loket.",
                                            isUrgent = true
                                        )
                                        countDownTimer?.cancel()
                                        lastEstimasiMenit = -1
                                        binding.tvCountdown.text = "🔔 Giliran Anda sekarang!"
                                    }
                                    "dilayani" -> notifHelper.showNotification(
                                        "✅ Sedang Dilayani",
                                        "Nomor $noAntrian sedang dilayani."
                                    )
                                    "selesai" -> {
                                        notifHelper.showNotification(
                                            "🎉 Selesai",
                                            "Antrian $noAntrian telah selesai."
                                        )
                                        countDownTimer?.cancel()
                                        lastEstimasiMenit = -1
                                        binding.tvCountdown.text = "✅ Antrian selesai"
                                    }
                                }
                                lastStatus = newStatus
                            }
                        }
                    }
                }
                is Result.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(refreshRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(refreshRunnable)
        countDownTimer?.cancel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}