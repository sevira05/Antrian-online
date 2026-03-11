package com.example.antrianonline.ui.ulasan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.antrianonline.databinding.ActivityUlasanBinding

class UlasanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUlasanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUlasanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loket: String = intent.getStringExtra("loket") ?: ""

        binding.tvLoket.text = "Ulasan untuk loket: $loket"

        binding.recyclerUlasan.layoutManager =
            LinearLayoutManager(this)
    }
}