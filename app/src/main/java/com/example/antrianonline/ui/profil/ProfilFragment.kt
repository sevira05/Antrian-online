package com.example.antrianonline.ui.profil

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.antrianonline.databinding.FragmentProfilBinding
import com.example.antrianonline.utils.WorkManagerHelper
import com.example.antrianonline.ui.auth.LoginActivity
import com.example.antrianonline.utils.SessionManager

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!
    private lateinit var session: SessionManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        session = SessionManager(requireContext())

        loadProfil()

        binding.btnLogout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Keluar")
                .setMessage("Yakin ingin keluar dari akun?")
                .setPositiveButton("Keluar") { _, _ -> doLogout() }
                .setNegativeButton("Batal", null)
                .show()
        }

        binding.btnEditProfil.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfilActivity::class.java))
        }

        binding.btnGantiPassword.setOnClickListener {
            startActivity(Intent(requireContext(), GantiPasswordActivity::class.java))
        }
    }

    private fun loadProfil() {
        val nama     = session.getNama() ?: "User"
        val username = session.getUsername() ?: "-"
        val email    = session.getEmail() ?: "-"
        val noHp     = session.getNoHp() ?: "-"

        binding.tvNama.text     = nama
        binding.tvUsername.text = "@$username"
        binding.tvEmail.text    = email
        binding.tvNoHp.text     = noHp
        binding.tvAvatar.text   = nama.firstOrNull()?.uppercase() ?: "?"
        binding.tvStatus.text   = "Aktif ✓"
    }

    private fun doLogout() {
        // Stop background service sebelum logout
        WorkManagerHelper.stop(requireContext())
        session.clear()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}