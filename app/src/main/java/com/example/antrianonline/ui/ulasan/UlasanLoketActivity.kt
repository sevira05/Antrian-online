package com.example.antrianonline.ui.ulasan

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.antrianonline.databinding.ActivityUlasanLoketBinding

class UlasanLoketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUlasanLoketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUlasanLoketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUmum.setOnClickListener {
            bukaUlasan("umum")
        }

        binding.btnKeuangan.setOnClickListener {
            bukaUlasan("keuangan")
        }

        binding.btnKesehatan.setOnClickListener {
            bukaUlasan("kesehatan")
        }

        binding.btnAdministrasi.setOnClickListener {
            bukaUlasan("administrasi")
        }
    }

    private fun bukaUlasan(loket: String) {

        val intent = Intent(this, UlasanActivity::class.java)
        intent.putExtra("loket", loket)
        startActivity(intent)

    }

}