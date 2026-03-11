package com.example.antrianonline.ui.ulasan

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.antrianonline.data.api.RetrofitClient
import com.example.antrianonline.data.model.UlasanRequest
import com.example.antrianonline.databinding.ActivityKirimUlasanBinding
import com.example.antrianonline.utils.SessionManager
import kotlinx.coroutines.launch

class KirimUlasanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKirimUlasanBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityKirimUlasanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        val apiService = RetrofitClient.getApi(session)

        val idLoket = intent.getIntExtra("id_loket", 0)
        val idRiwayat = intent.getIntExtra("id_riwayat", 0)

        binding.btnKirim.setOnClickListener {

            val rating = binding.ratingInput.rating.toInt()
            val komentar = binding.edtKomentar.text.toString()

            if (rating == 0) {
                Toast.makeText(this, "Berikan rating terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = UlasanRequest(
                idLoket = idLoket,
                idRiwayat = idRiwayat,
                rating = rating,
                komentar = komentar
            )

            lifecycleScope.launch {

                try {

                    val response = apiService.kirimUlasan(request)

                    if (response.isSuccessful && response.body()?.success == true) {

                        Toast.makeText(
                            this@KirimUlasanActivity,
                            "Ulasan berhasil dikirim",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {

                        Toast.makeText(
                            this@KirimUlasanActivity,
                            response.body()?.message ?: "Gagal mengirim ulasan",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                } catch (e: Exception) {

                    Toast.makeText(
                        this@KirimUlasanActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()

                }

            }

        }
    }
}