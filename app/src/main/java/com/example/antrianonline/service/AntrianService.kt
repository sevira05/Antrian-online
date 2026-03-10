package com.example.antrianonline.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.example.antrianonline.data.api.RetrofitClient
import com.example.antrianonline.data.repository.AntrianRepository
import com.example.antrianonline.data.repository.Result
import com.example.antrianonline.ui.home.HomeActivity
import com.example.antrianonline.utils.NotificationHelper
import com.example.antrianonline.utils.SessionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AntrianService : Service() {

    companion object {
        const val FOREGROUND_CHANNEL_ID = "antrian_service_channel"
        const val FOREGROUND_NOTIF_ID   = 9999
        const val CHECK_INTERVAL_MS     = 15_000L // cek tiap 15 detik

        fun start(context: Context) {
            val intent = Intent(context, AntrianService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, AntrianService::class.java))
        }
    }

    private lateinit var session: SessionManager
    private lateinit var repo: AntrianRepository
    private lateinit var notifHelper: NotificationHelper
    private val handler = Handler(Looper.getMainLooper())
    private val scope   = CoroutineScope(Dispatchers.IO + Job())

    private var lastStatus: String?    = null
    private var myNoAntrian: String?   = null
    private var selectedLoketId: Int   = 1

    private val checkRunnable = object : Runnable {
        override fun run() {
            checkAntrian()
            handler.postDelayed(this, CHECK_INTERVAL_MS)
        }
    }

    override fun onCreate() {
        super.onCreate()
        session     = SessionManager(this)
        repo        = AntrianRepository(RetrofitClient.getApi(session))
        notifHelper = NotificationHelper(this)
        createForegroundChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(FOREGROUND_NOTIF_ID, buildForegroundNotification())
        handler.post(checkRunnable)
        return START_STICKY // restart otomatis jika dibunuh sistem
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(checkRunnable)
    }

    private fun checkAntrian() {
        if (!session.isLoggedIn()) {
            stopSelf()
            return
        }

        scope.launch {
            // Cari antrian aktif milik user
            when (val result = repo.getAntrianSaya()) {
                is Result.Success -> {
                    val aktif = result.data.firstOrNull {
                        it.status == "menunggu" || it.status == "dipanggil"
                    }
                    if (aktif != null) {
                        selectedLoketId = aktif.loket?.idLoket ?: 1
                        myNoAntrian     = aktif.noAntrian
                        if (lastStatus == null) lastStatus = aktif.status
                        checkMonitor()
                    } else {
                        // Tidak ada antrian aktif, reset
                        myNoAntrian = null
                        lastStatus  = null
                    }
                }
                else -> {}
            }
        }
    }

    private fun checkMonitor() {
        scope.launch {
            when (val result = repo.getMonitor(selectedLoketId)) {
                is Result.Success -> {
                    val noAntrian = myNoAntrian ?: return@launch
                    val list      = result.data.daftarAntrian
                    val myAntrian = list.firstOrNull { it.noAntrian == noAntrian }

                    myAntrian?.let {
                        val newStatus = it.status
                        if (newStatus != lastStatus) {
                            when (newStatus) {
                                "dipanggil" -> notifHelper.showNotification(
                                    "🔔 Giliran Anda!",
                                    "Nomor $noAntrian dipanggil! Segera menuju loket.",
                                    isUrgent = true
                                )
                                "dilayani" -> notifHelper.showNotification(
                                    "✅ Sedang Dilayani",
                                    "Nomor $noAntrian sedang dilayani."
                                )
                                "selesai" -> notifHelper.showNotification(
                                    "🎉 Antrian Selesai",
                                    "Antrian $noAntrian telah selesai dilayani."
                                )
                            }
                            lastStatus = newStatus
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun createForegroundChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                FOREGROUND_CHANNEL_ID,
                "Antrian Berjalan",
                NotificationManager.IMPORTANCE_LOW // low agar tidak berisik
            ).apply {
                description = "Memantau status antrian Anda"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(): Notification {
        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
            .setContentTitle("Antrian Online")
            .setContentText("Memantau antrian Anda...")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}