package com.example.antrianonline.ui.profil

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.antrianonline.databinding.ActivityEditProfileBinding
import com.example.antrianonline.utils.SessionManager

class EditProfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        loadData()

        binding.btnSimpan.setOnClickListener {
            updateProfil()
        }
    }

    private fun loadData() {

        binding.etNama.setText(session.getNama())
        binding.etUsername.setText(session.getUsername())
        binding.etEmail.setText(session.getEmail())
        binding.etNoHp.setText(session.getNoHp())

    }

    private fun updateProfil() {

        val nama = binding.etNama.text.toString()
        val username = binding.etUsername.text.toString()
        val email = binding.etEmail.text.toString()
        val nohp = binding.etNoHp.text.toString()

        if (nama.isEmpty() || username.isEmpty()) {
            Toast.makeText(this, "Data belum lengkap", Toast.LENGTH_SHORT).show()
            return
        }

        session.updateProfil(nama, username, email, nohp)

        Toast.makeText(this, "Profil berhasil diperbarui", Toast.LENGTH_SHORT).show()

        finish()
    }
}