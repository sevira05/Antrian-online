package com.example.antrianonline.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker.Result
import com.example.antrianonline.data.api.RetrofitClient
import com.example.antrianonline.data.repository.AntrianRepository
import com.example.antrianonline.utils.NotificationHelper
import com.example.antrianonline.utils.SessionManager

class AntrianWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val session = SessionManager(applicationContext)

        // kalau belum login → stop
        if (!session.isLoggedIn()) return Result.success()

        val repo = AntrianRepository(
            RetrofitClient.getApi(session),
            session
        )

        val notifHelper = NotificationHelper(applicationContext)

        return try {

            // ================= AMBIL ANTRIAN SAYA =================
            val antrianResult = repo.getAntrianSaya()

            if (antrianResult !is com.example.antrianonline.data.repository.Result.Success) {
                return Result.success()
            }

            val list = antrianResult.data.data

            val aktif = list.firstOrNull {
                it.status == "menunggu" || it.status == "dipanggil"
            } ?: return Result.success()

            val loketId   = aktif.loket?.idLoket ?: 1
            val noAntrian = aktif.noAntrian

            val lastStatus = session.getLastAntrianStatus()

            // ================= MONITOR =================
            val monitorResult = repo.getMonitor(loketId)

            if (monitorResult !is com.example.antrianonline.data.repository.Result.Success) {
                return Result.success()
            }

            val sedang = monitorResult.data.data

            val newStatus = when {
                sedang?.noAntrian == noAntrian -> sedang.status
                aktif.status != lastStatus     -> aktif.status
                else                           -> return Result.success()
            }

            // ================= HANDLE PERUBAHAN STATUS =================
            if (newStatus != lastStatus) {

                when (newStatus) {

                    "dipanggil" -> notifHelper.showNotification(
                        "🔔 Giliran Anda!",
                        "Nomor $noAntrian dipanggil, segera ke loket!",
                        true
                    )

                    "dilayani" -> notifHelper.showNotification(
                        "✅ Sedang Dilayani",
                        "Nomor $noAntrian sedang dilayani"
                    )

                    "selesai" -> notifHelper.showNotification(
                        "🎉 Selesai",
                        "Antrian $noAntrian telah selesai"
                    )
                }

                // SIMPAN STATUS TERBARU
                session.saveLastAntrianStatus(newStatus)
            }

            Result.success()

        } catch (e: Exception) {
            Result.success()
        }
    }
}