package com.example.antrianonline.ui.notifikasi

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.antrianonline.R
import com.example.antrianonline.data.model.Notifikasi
import com.example.antrianonline.databinding.ItemNotifikasiBinding

class NotifikasiAdapter :
    ListAdapter<Notifikasi, NotifikasiAdapter.VH>(DiffCb()) {

    inner class VH(val b: ItemNotifikasiBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(notif: Notifikasi) {

            // ================= TEXT =================
            b.tvJudul.text = notif.judul
            b.tvPesan.text = notif.pesan
            b.tvWaktu.text = notif.createdAt

            // ================= ICON =================
            if (notif.judul.contains("Dipanggil", true)) {
                b.imgIcon.setImageResource(R.drawable.ic_notification)
            } else {
                b.imgIcon.setImageResource(R.drawable.ic_notification)
            }

            // ================= UNREAD STYLE =================
            if (!notif.isRead) {
                b.viewUnread.visibility = android.view.View.VISIBLE
                b.cardNotif.setCardBackgroundColor(Color.parseColor("#F8FAFF"))
            } else {
                b.viewUnread.visibility = android.view.View.GONE
                b.cardNotif.setCardBackgroundColor(Color.WHITE)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemNotifikasiBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCb : DiffUtil.ItemCallback<Notifikasi>() {
        override fun areItemsTheSame(a: Notifikasi, b: Notifikasi): Boolean {
            return a.idNotif == b.idNotif
        }

        override fun areContentsTheSame(a: Notifikasi, b: Notifikasi): Boolean {
            return a == b
        }
    }
}