package com.example.antrianonline.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.antrianonline.R
import com.example.antrianonline.databinding.ActivityHomeBinding
import com.example.antrianonline.utils.WorkManagerHelper
import com.example.antrianonline.ui.antrian.AmbilAntrianFragment
import com.example.antrianonline.ui.monitor.MonitorFragment
import com.example.antrianonline.ui.notifikasi.NotifikasiFragment
import com.example.antrianonline.ui.profil.ProfilFragment
import com.example.antrianonline.utils.SessionManager

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var session: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        session = SessionManager(this)

        val nama = session.getNama() ?: session.getUsername() ?: "Pengguna"
        binding.tvWelcome.text = "Halo, $nama! 👋"

        // Start background service untuk pantau antrian
        WorkManagerHelper.startPeriodicCheck(this)

        loadFragment(AmbilAntrianFragment())

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_ambil      -> { loadFragment(AmbilAntrianFragment()); true }
                R.id.nav_monitor    -> { loadFragment(MonitorFragment());       true }
                R.id.nav_notifikasi -> { loadFragment(NotifikasiFragment());    true }
                R.id.nav_profil     -> { loadFragment(ProfilFragment());        true }
                else                -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}