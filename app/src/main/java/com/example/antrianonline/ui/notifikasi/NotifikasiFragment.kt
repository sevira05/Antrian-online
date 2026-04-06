package com.example.antrianonline.ui.notifikasi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.antrianonline.data.model.Notifikasi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.antrianonline.databinding.FragmentNotifikasiBinding
import com.example.antrianonline.utils.SessionManager

class NotifikasiFragment : Fragment() {

    private var _binding: FragmentNotifikasiBinding? = null
    private val binding get() = _binding!!

    private lateinit var session: SessionManager
    private lateinit var adapter: NotifikasiAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotifikasiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        session = SessionManager(requireContext())

        adapter = NotifikasiAdapter()
        binding.rvNotifikasi.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifikasi.adapter = adapter

        binding.swipeRefresh.setOnRefreshListener {
            loadNotif()
        }

        binding.tvReadAll.setOnClickListener {
            session.clearNotif()
            loadNotif()
        }

        loadNotif()
    }

    private fun loadNotif() {
        val data = session.getNotif()

        val list: List<Notifikasi> = data.mapIndexed { index: Int, triple: Triple<String, String, String> ->

            Notifikasi(
                idNotif = index,
                judul = triple.first,
                pesan = triple.second,
                createdAt = triple.third,
                isRead = false
            )
        }

        adapter.submitList(list)
        binding.swipeRefresh.isRefreshing = false

        binding.tvReadAll.text =
            if (list.isNotEmpty()) "Hapus Semua" else "Tidak ada notifikasi"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}