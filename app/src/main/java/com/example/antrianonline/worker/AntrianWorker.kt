package com.example.antrianonline.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.antrianonline.data.api.RetrofitClient
import com.example.antrianonline.data.repository.AntrianRepository
import com.example.antrianonline.data.repository.Result
import com.example.antrianonline.utils.NotificationHelper
import com.example.antrianonline.utils.SessionManager

class AntrianWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val session = SessionManager(applicationContext)
        if (!session.isLoggedIn()) return Result.success()

        val repo        = AntrianRepository(RetrofitClient.getApi(session))
        val notifHelper = NotificationHelper(applicationContext)

        return try {
            // Ambil antrian aktif user
            val antrianResult = repo.getAntrianSaya()
            if (antrianResult !is com.example.antrianonline.data.repository.Result.Success) {
                return Result.success()
            }

            val aktif = antrianResult.data.firstOrNull {
                it.status == "menunggu" || it.status == "dipanggil"
            } ?: return Result.success()

            val loketId    = aktif.loket?.idLoket ?: 1
            val noAntrian  = aktif.noAntrian
            val lastStatus = session.getLastAntrianStatus()

            // Cek monitor
            val monitorResult = repo.getMonitor(loketId)
            if (monitorResult !is com.example.antrianonline.data.repository.Result.Success) {
                return Result.success()
            }

            val myAntrian = monitorResult.data.daftarAntrian
                .firstOrNull { it.noAntrian == noAntrian }

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
                    session.saveLastAntrianStatus(newStatus)
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}