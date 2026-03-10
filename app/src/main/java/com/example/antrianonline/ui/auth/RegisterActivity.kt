package com.example.antrianonline.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.antrianonline.data.api.RetrofitClient
import com.example.antrianonline.data.repository.AntrianRepository
import com.example.antrianonline.data.repository.Result
import com.example.antrianonline.databinding.ActivityRegisterBinding
import com.example.antrianonline.ui.home.HomeActivity
import com.example.antrianonline.utils.SessionManager
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var session: SessionManager
    private lateinit var repo: AntrianRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        repo    = AntrianRepository(RetrofitClient.getApi(session))

        binding.btnDaftar.setOnClickListener { doRegister() }
        binding.tvLogin.setOnClickListener   { finish() }
    }

    private fun doRegister() {
        val nama     = binding.etNama.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val email    = binding.etEmail.text.toString().trim()
        val noHp     = binding.etNoHp.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirm  = binding.etPasswordConfirmation.text.toString()

        if (nama.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }
        if (password != confirm) {
            Toast.makeText(this, "Konfirmasi password tidak cocok", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            when (val result = repo.register(nama, username, email, noHp, password, confirm)) {
                is Result.Success -> {
                    val user = result.data.user
                    session.saveSession(
                        token    = result.data.token,
                        userId   = user.idUser,
                        username = user.username,
                        nama     = user.namaLengkap,
                        email    = user.email,
                        noHp     = user.noHp
                    )
                    startActivity(Intent(this@RegisterActivity, HomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    })
                }
                is Result.Error -> {
                    setLoading(false)
                    Toast.makeText(this@RegisterActivity, result.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnDaftar.isEnabled    = !loading
    }
}