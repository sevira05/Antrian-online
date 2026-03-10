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
import com.example.antrianonline.databinding.ActivityLoginBinding
import com.example.antrianonline.ui.home.HomeActivity
import com.example.antrianonline.utils.SessionManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var session: SessionManager
    private lateinit var repo: AntrianRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)
        repo    = AntrianRepository(RetrofitClient.getApi(session))

        if (session.isLoggedIn()) {
            goHome()
            return
        }

        binding.btnLogin.setOnClickListener { doLogin() }
        binding.tvDaftar.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun doLogin() {
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username dan password wajib diisi", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)
        lifecycleScope.launch {
            when (val result = repo.login(username, password)) {
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
                    goHome()
                }
                is Result.Error -> {
                    setLoading(false)
                    Toast.makeText(this@LoginActivity, result.message, Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    }

    private fun goHome() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled     = !loading
    }
}