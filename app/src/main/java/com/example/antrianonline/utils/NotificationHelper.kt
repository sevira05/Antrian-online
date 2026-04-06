package com.example.antrianonline.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.antrianonline.R
import com.example.antrianonline.ui.home.HomeActivity

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID_URGENT  = "antrian_urgent"
        const val CHANNEL_ID_NORMAL  = "antrian_normal"

        const val CHANNEL_NAME_URGENT = "Antrian Dipanggil"
        const val CHANNEL_NAME_NORMAL = "Status Antrian"

        const val NOTIF_ID_URGENT = 2001
        const val NOTIF_ID_NORMAL = 2002
    }

    init {
        createChannel()
    }

    // ================= CHANNEL =================
    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                    as NotificationManager

            // 🔴 URGENT (HEADS-UP TANPA SUARA)
            val urgentChannel = NotificationChannel(
                CHANNEL_ID_URGENT,
                CHANNEL_NAME_URGENT,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi saat nomor dipanggil"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 500, 200, 500)
                setSound(null, null)
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
            }

            // 🔵 NORMAL
            val normalChannel = NotificationChannel(
                CHANNEL_ID_NORMAL,
                CHANNEL_NAME_NORMAL,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Update status antrian"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
                setSound(null, null)
            }

            manager.createNotificationChannel(urgentChannel)
            manager.createNotificationChannel(normalChannel)
        }
    }

    // ================= SHOW NOTIFICATION =================
    fun showNotification(
        judul: String,
        pesan: String,
        isUrgent: Boolean = false
    ) {

        // ✅ SIMPAN KE SESSION (INI YANG DITAMBAHKAN)
        val session = SessionManager(context)
        session.saveNotif(judul, pesan)

        // ✅ Permission Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) return
        }

        // ✅ Intent ke Home → Monitor
        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("nav_to", "monitor")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = if (isUrgent) CHANNEL_ID_URGENT else CHANNEL_ID_NORMAL
        val notifId   = if (isUrgent) NOTIF_ID_URGENT else NOTIF_ID_NORMAL

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_notification) // pastikan drawable ada
            .setContentTitle(judul)
            .setContentText(pesan)
            .setStyle(NotificationCompat.BigTextStyle().bigText(pesan))
            .setPriority(
                if (isUrgent) NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_DEFAULT
            )
            .setCategory(
                if (isUrgent) NotificationCompat.CATEGORY_CALL
                else NotificationCompat.CATEGORY_STATUS
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setFullScreenIntent(pendingIntent, isUrgent)
            .build()

        NotificationManagerCompat.from(context).notify(notifId, notification)

        // 🔥 GETAR SAJA (TANPA SUARA)
        if (isUrgent) vibrate()
    }

    // ================= VIBRATE =================
    private fun vibrate() {
        val pattern = longArrayOf(0, 500, 200, 500)

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vm.defaultVibrator.vibrate(
                    VibrationEffect.createWaveform(pattern, -1)
                )
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                v.vibrate(pattern, -1)
            }
        } catch (_: Exception) {}
    }
}