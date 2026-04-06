package com.example.antrianonline.ui.profil

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.antrianonline.databinding.ActivityGantiPasswordBinding

class GantiPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGantiPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGantiPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUpdatePassword.setOnClickListener {
            gantiPassword()
        }
    }

    private fun gantiPassword() {

        val lama = binding.etPasswordLama.text.toString()
        val baru = binding.etPasswordBaru.text.toString()
        val konfirmasi = binding.etKonfirmasiPassword.text.toString()

        if (lama.isEmpty() || baru.isEmpty() || konfirmasi.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (baru != konfirmasi) {
            Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_SHORT).show()

        finish()
    }
}