package com.example.antrianonline.ui.notifikasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.antrianonline.data.api.RetrofitClient
import com.example.antrianonline.data.repository.AntrianRepository
import com.example.antrianonline.data.repository.Result
import com.example.antrianonline.databinding.FragmentNotifikasiBinding
import com.example.antrianonline.utils.SessionManager
import kotlinx.coroutines.launch

class NotifikasiFragment : Fragment() {

    private var _binding: FragmentNotifikasiBinding? = null
    private val binding get() = _binding!!

    private lateinit var repo: AntrianRepository
    private lateinit var adapter: NotifikasiAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotifikasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val session = SessionManager(requireContext())
        repo = AntrianRepository(RetrofitClient.getApi(session))

        adapter = NotifikasiAdapter()
        binding.rvNotifikasi.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifikasi.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener { loadNotifikasi() }
        binding.tvReadAll.setOnClickListener { readAll() }

        loadNotifikasi()
    }

    private fun loadNotifikasi() {
        binding.swipeRefresh.isRefreshing = true
        lifecycleScope.launch {
            when (val result = repo.getNotifikasi()) {
                is Result.Success -> {
                    binding.swipeRefresh.isRefreshing = false
                    adapter.submitList(result.data.data)
                    val unread = result.data.unreadCount
                    binding.tvReadAll.text = if (unread > 0) "Tandai Semua ($unread)" else "Semua Dibaca ✓"
                }
                is Result.Error -> {
                    binding.swipeRefresh.isRefreshing = false
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    private fun readAll() {
        lifecycleScope.launch {
            repo.markReadAll()
            loadNotifikasi()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}