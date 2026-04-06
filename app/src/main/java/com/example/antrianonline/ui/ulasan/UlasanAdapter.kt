package com.example.antrianonline.ui.ulasan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.antrianonline.data.model.Ulasan
import com.example.antrianonline.databinding.ItemUlasanBinding

class UlasanAdapter :
    RecyclerView.Adapter<UlasanAdapter.VH>() {

    private val list = mutableListOf<Ulasan>()

    inner class VH(val b: ItemUlasanBinding) :
        RecyclerView.ViewHolder(b.root) {

        fun bind(data: Ulasan) {
            b.tvNama.text = data.nama
            b.tvKomentar.text = data.komentar
            b.tvTanggal.text = data.tanggal
            b.tvRating.text = "⭐ ${data.rating}/5"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            ItemUlasanBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(list[position])
    }

    // 🔥 TAMBAHAN: fungsi tambah data
    fun addUlasan(data: Ulasan) {
        list.add(0, data) // masuk paling atas
        notifyItemInserted(0)
    }
}