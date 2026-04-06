package com.example.antrianonline.ui.antrian

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.*
import com.example.antrianonline.data.model.Loket
import com.example.antrianonline.databinding.ItemLoketBinding

class LoketAdapter(
    private val onSelect: (Loket) -> Unit
) : ListAdapter<Loket, LoketAdapter.VH>(DiffCb()) {

    private var selectedPos = -1

    private val iconMap = mapOf(
        "umum" to "🏛️",
        "keuangan" to "💰",
        "kesehatan" to "🏥",
        "administrasi" to "📋"
    )

    inner class VH(val b: ItemLoketBinding) : RecyclerView.ViewHolder(b.root) {

        fun bind(loket: Loket, isSelected: Boolean) {

            val key = loket.nama.lowercase()
            b.tvIcon.text = iconMap.entries
                .firstOrNull { key.contains(it.key) }?.value ?: "🏢"

            b.tvNamaLoket.text = loket.nama
            b.tvPengelola.text = "Pengelola: ${loket.pengelola}"
            b.tvJam.text = "${loket.jamBuka} - ${loket.jamTutup}"
            b.tvSisaKuota.text = "${loket.totalDiambil}/${loket.maxAntrian}"

            val persen = if (loket.maxAntrian > 0)
                (loket.totalDiambil * 100) / loket.maxAntrian else 0

            b.progressKuota.progress = persen

            val color = when {
                persen >= 80 -> "#EF4444"
                persen >= 50 -> "#F59E0B"
                else -> "#22C55E"
            }

            b.progressKuota.progressTintList =
                ColorStateList.valueOf(Color.parseColor(color))

            b.tvStatus.text = loket.statusLoket

            val statusColor = when (loket.statusLoket) {
                "BUKA" -> "#22C55E"
                "PENUH" -> "#EF4444"
                else -> "#94A3B8"
            }

            b.tvStatus.setTextColor(Color.parseColor(statusColor))

            b.cardLoket.strokeWidth = if (isSelected) 3 else 0

            val isActive = loket.statusLoket == "BUKA"
            b.root.alpha = if (isActive) 1f else 0.5f

            b.root.setOnClickListener {
                if (!isActive) return@setOnClickListener

                val old = selectedPos
                selectedPos = adapterPosition

                notifyItemChanged(old)
                notifyItemChanged(selectedPos)

                onSelect(loket)
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