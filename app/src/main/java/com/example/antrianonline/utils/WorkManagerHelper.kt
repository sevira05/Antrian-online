package com.example.antrianonline.utils

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.antrianonline.worker.AntrianWorker
import java.util.concurrent.TimeUnit

object WorkManagerHelper {

    private const val WORK_NAME = "antrian_check_work"

    fun startPeriodicCheck(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Minimal 15 menit (batas minimum WorkManager)
        val workRequest = PeriodicWorkRequestBuilder<AntrianWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // tidak restart kalau sudah jalan
            workRequest
        )
    }

    fun stop(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}