package com.example.antrianonline.ui.notifikasi

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.antrianonline.data.model.Notifikasi
import com.example.antrianonline.databinding.ItemNotifikasiBinding

class NotifikasiAdapter : ListAdapter<Notifikasi, NotifikasiAdapter.VH>(DiffCb()) {

    inner class VH(val b: ItemNotifikasiBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(notif: Notifikasi) {
            b.tvJudul.text = notif.judul
            b.tvPesan.text = notif.pesan
            b.tvWaktu.text = notif.createdAt

            if (!notif.isRead) {
                b.root.setCardBackgroundColor(Color.parseColor("#1A1535"))
                b.viewUnread.visibility = View.VISIBLE
            } else {
                b.root.setCardBackgroundColor(Color.parseColor("#13131F"))
                b.viewUnread.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemNotifikasiBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class DiffCb : DiffUtil.ItemCallback<Notifikasi>() {
        override fun areItemsTheSame(a: Notifikasi, b: Notifikasi) = a.idNotif == b.idNotif
        override fun areContentsTheSame(a: Notifikasi, b: Notifikasi) = a == b
    }
}