package com.example.antrianonline.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// ── User ─────────────────────────────────────────────────────────────────
@Parcelize
data class User(
    @SerializedName("id_user")      val idUser: Int,
    @SerializedName("nama_lengkap") val namaLengkap: String,
    @SerializedName("username")     val username: String,
    @SerializedName("email")        val email: String,
    @SerializedName("no_hp")        val noHp: String?,
    @SerializedName("is_active")    val isActive: Boolean,
    @SerializedName("created_at")   val createdAt: String?,
) : Parcelable

// ── Loket ─────────────────────────────────────────────────────────────────
@Parcelize
data class Loket(
    @SerializedName("id_loket")     val idLoket: Int,
    @SerializedName("nama")         val nama: String,
    @SerializedName("deskripsi")    val deskripsi: String?,
    @SerializedName("pengelola")    val pengelola: String,
    @SerializedName("max_antrian")  val maxAntrian: Int,
    @SerializedName("jam_buka")     val jamBuka: String,
    @SerializedName("jam_tutup")    val jamTutup: String,
    @SerializedName("total_diambil") val totalDiambil: Int = 0,
    @SerializedName("sisa_kuota")   val sisaKuota: Int = 0,
    @SerializedName("status_loket") val statusLoket: String = "BUKA",
) : Parcelable

// ── Antrian ───────────────────────────────────────────────────────────────
@Parcelize
data class Antrian(
    @SerializedName("id_riwayat")       val idRiwayat: Int,
    @SerializedName("no_antrian")       val noAntrian: String,
    @SerializedName("tanggal_jam")      val tanggalJam: String,
    @SerializedName("status")           val status: String,
    @SerializedName("tanggal_panggil")  val tanggalPanggil: String?,
    @SerializedName("keterangan")       val keterangan: String?,
    @SerializedName("loket")            val loket: Loket?,
    @SerializedName("posisi")           val posisi: Int = 0,
    @SerializedName("estimasi_menit")   val estimasiMenit: Int = 0,
) : Parcelable

// ── Monitor ───────────────────────────────────────────────────────────────
data class MonitorData(
    @SerializedName("sedang_dilayani")  val sedangDilayani: String,
    @SerializedName("total_menunggu")  val totalMenunggu: Int,
    @SerializedName("daftar_antrian")  val daftarAntrian: List<Antrian>,
)

// ── Notifikasi ────────────────────────────────────────────────────────────
data class Notifikasi(
    @SerializedName("id_notif")    val idNotif: Int,
    @SerializedName("judul")       val judul: String,
    @SerializedName("pesan")       val pesan: String,
    @SerializedName("is_read")     val isRead: Boolean,
    @SerializedName("created_at")  val createdAt: String,
)

// ── API Response Wrappers ─────────────────────────────────────────────────
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data")    val data: T?,
)

data class LoginResponse(
    @SerializedName("user")  val user: User,
    @SerializedName("token") val token: String,
)

data class AntrianResponse(
    @SerializedName("antrian")         val antrian: Antrian,
    @SerializedName("posisi")          val posisi: Int,
    @SerializedName("estimasi_menit")  val estimasiMenit: Int,
)

data class NotifListResponse(
    @SerializedName("success")      val success: Boolean,
    @SerializedName("unread_count") val unreadCount: Int,
    @SerializedName("data")         val data: List<Notifikasi>,
)

data class MonitorResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data")    val data: MonitorData,
)
