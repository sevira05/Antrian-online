package com.example.antrianonline.ui.ulasan

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.antrianonline.data.model.Ulasan
import com.example.antrianonline.databinding.ItemUlasanBinding

class UlasanAdapter : RecyclerView.Adapter<UlasanAdapter.ViewHolder>() {

    private var list = listOf<Ulasan>()

    fun submitList(data: List<Ulasan>) {
        list = data
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemUlasanBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val binding = ItemUlasanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = list[position]

        holder.binding.tvNama.text = data.nama
        holder.binding.tvKomentar.text = data.komentar
        holder.binding.ratingBar.rating = data.rating.toFloat()

    }

    override fun getItemCount(): Int {
        return list.size
    }
}