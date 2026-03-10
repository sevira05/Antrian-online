package com.example.antrianonline.ui.antrian

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.antrianonline.data.model.Loket
import com.example.antrianonline.databinding.ItemLoketBinding

class LoketAdapter(
    private val onSelect: (Loket) -> Unit
) : ListAdapter<Loket, LoketAdapter.VH>(DiffCb()) {

    private var selectedPos = -1

    inner class VH(val b: ItemLoketBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(loket: Loket, isSelected: Boolean) {
            b.tvNamaLoket.text      = loket.nama
            b.tvPengelola.text      = "Pengelola: ${loket.pengelola}"
            b.tvJam.text            = "${loket.jamBuka} - ${loket.jamTutup}"
            b.tvSisaKuota.text      = "${loket.totalDiambil}/${loket.maxAntrian} antrian"
            b.progressKuota.max     = loket.maxAntrian
            b.progressKuota.progress = loket.totalDiambil

            val statusColor = when (loket.statusLoket) {
                "BUKA"       -> Color.parseColor("#22c55e")
                "PENUH"      -> Color.parseColor("#ef4444")
                "TUTUP"      -> Color.parseColor("#94a3b8")
                "BELUM_BUKA" -> Color.parseColor("#f59e0b")
                else         -> Color.GRAY
            }
            b.tvStatus.text      = loket.statusLoket.replace("_", " ")
            b.tvStatus.setTextColor(statusColor)

            b.root.isSelected = isSelected
            b.root.cardElevation = if (isSelected) 8f else 2f

            b.root.setOnClickListener {
                if (loket.statusLoket == "BUKA") {
                    val old = selectedPos
                    selectedPos = adapterPosition
                    notifyItemChanged(old)
                    notifyItemChanged(selectedPos)
                    onSelect(loket)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemLoketBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), position == selectedPos)
    }

    class DiffCb : DiffUtil.ItemCallback<Loket>() {
        override fun areItemsTheSame(a: Loket, b: Loket) = a.idLoket == b.idLoket
        override fun areContentsTheSame(a: Loket, b: Loket) = a == b
    }
}
