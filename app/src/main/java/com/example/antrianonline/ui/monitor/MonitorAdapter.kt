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
            b.tvNoAntrian.text = antrian.noAntrian
            b.tvNamaUser.text  = antrian.loket?.nama ?: ""
            b.tvWaktu.text     = antrian.tanggalJam

            val (statusText, textColor) = when (antrian.status) {
                "dilayani"  -> Pair("DILAYANI",  "#22C55E")
                "dipanggil" -> Pair("DIPANGGIL", "#C47C0A")
                "menunggu"  -> Pair("MENUNGGU",  "#1A4DB8")
                "selesai"   -> Pair("SELESAI",   "#7A8FA8")
                "batal"     -> Pair("BATAL",     "#E24B4A")
                else        -> Pair(antrian.status.uppercase(), "#7A8FA8")
            }
            b.tvStatus.text = statusText
            b.tvStatus.setTextColor(Color.parseColor(textColor))
            b.tvStatusMonitor.text = ""
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        VH(ItemAntrianMonitorBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class DiffCb : DiffUtil.ItemCallback<Antrian>() {
        override fun areItemsTheSame(a: Antrian, b: Antrian) = a.idRiwayat == b.idRiwayat
        override fun areContentsTheSame(a: Antrian, b: Antrian) = a == b
    }
}