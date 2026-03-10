package com.example.antrianonline.ui.monitor

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.antrianonline.data.model.Antrian
import com.example.antrianonline.databinding.ItemAntrianMonitorBinding

class MonitorAdapter : ListAdapter<Antrian, MonitorAdapter.VH>(DiffCb()) {

    inner class VH(val b: ItemAntrianMonitorBinding) : RecyclerView.ViewHolder(b.root) {
        fun bind(antrian: Antrian) {
            b.tvNoAntrian.text     = antrian.noAntrian
            b.tvNamaUser.text      = antrian.loket?.nama ?: "-"
            b.tvWaktu.text         = antrian.tanggalJam
            b.tvStatusMonitor.text = antrian.status.uppercase()

            val (bgColor, textColor) = when (antrian.status) {
                "dilayani"  -> Pair("#1A3D2B", "#4ADE80")
                "dipanggil" -> Pair("#3D2E1A", "#FCD34D")
                "menunggu"  -> Pair("#1A1F3D", "#818CF8")
                else        -> Pair("#1E1E2E", "#8B8FA8")
            }
            b.root.setCardBackgroundColor(Color.parseColor(bgColor))
            b.tvStatusMonitor.setTextColor(Color.parseColor(textColor))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemAntrianMonitorBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class DiffCb : DiffUtil.ItemCallback<Antrian>() {
        override fun areItemsTheSame(a: Antrian, b: Antrian) = a.idRiwayat == b.idRiwayat
        override fun areContentsTheSame(a: Antrian, b: Antrian) = a == b
    }
}