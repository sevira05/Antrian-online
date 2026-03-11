package com.example.antrianonline.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// ── User ─────────────────────────────────────────────────────
@Parcelize
data class User(
    @SerializedName("id_user") val idUser: Int,
    @SerializedName("nama_lengkap") val namaLengkap: String,
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("no_hp") val noHp: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("created_at") val createdAt: String?
) : Parcelable


// ── Loket ────────────────────────────────────────────────────
@Parcelize
data class Loket(
    @SerializedName("id_loket") val idLoket: Int,
    @SerializedName("nama") val nama: String,
    @SerializedName("deskripsi") val deskripsi: String?,
    @SerializedName("pengelola") val pengelola: String,
    @SerializedName("max_antrian") val maxAntrian: Int,
    @SerializedName("jam_buka") val jamBuka: String,
    @SerializedName("jam_tutup") val jamTutup: String,
    @SerializedName("total_diambil") val totalDiambil: Int = 0,
    @SerializedName("sisa_kuota") val sisaKuota: Int = 0,
    @SerializedName("status_loket") val statusLoket: String = "BUKA"
) : Parcelable


// ── Antrian ──────────────────────────────────────────────────
@Parcelize
data class Antrian(
    @SerializedName("id_riwayat") val idRiwayat: Int,
    @SerializedName("no_antrian") val noAntrian: String,
    @SerializedName("tanggal_jam") val tanggalJam: String,
    @SerializedName("status") val status: String,
    @SerializedName("tanggal_panggil") val tanggalPanggil: String?,
    @SerializedName("keterangan") val keterangan: String?,
    @SerializedName("loket") val loket: Loket?,
    @SerializedName("posisi") val posisi: Int = 0,
    @SerializedName("estimasi_menit") val estimasiMenit: Int = 0
) : Parcelable


// ── Monitor ──────────────────────────────────────────────────
data class MonitorData(
    @SerializedName("sedang_dilayani") val sedangDilayani: String,
    @SerializedName("total_menunggu") val totalMenunggu: Int,
    @SerializedName("daftar_antrian") val daftarAntrian: List<Antrian>
)


// ── Notifikasi ───────────────────────────────────────────────
data class Notifikasi(
    @SerializedName("id_notif") val idNotif: Int,
    @SerializedName("judul") val judul: String,
    @SerializedName("pesan") val pesan: String,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("created_at") val createdAt: String
)


// ── Wrapper API ──────────────────────────────────────────────
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: T?
)


// ── Login Response ───────────────────────────────────────────
data class LoginResponse(
    @SerializedName("user") val user: User,
    @SerializedName("token") val token: String
)


// ── Ambil Antrian Response ───────────────────────────────────
data class AntrianResponse(
    @SerializedName("antrian") val antrian: Antrian,
    @SerializedName("posisi") val posisi: Int,
    @SerializedName("estimasi_menit") val estimasiMenit: Int
)


// ── Notifikasi Response ──────────────────────────────────────
data class NotifListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("unread_count") val unreadCount: Int,
    @SerializedName("data") val data: List<Notifikasi>
)


// ── Monitor Response ─────────────────────────────────────────
data class MonitorResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: MonitorData
)



// ============================================================
// ULASAN LOKET
// ============================================================


// ── Request Kirim Ulasan ─────────────────────────────────────
data class UlasanRequest(
    @SerializedName("id_loket") val idLoket: Int,
    @SerializedName("id_riwayat") val idRiwayat: Int,
    @SerializedName("rating") val rating: Int,
    @SerializedName("komentar") val komentar: String?
)


// ── Item Ulasan ──────────────────────────────────────────────
data class UlasanItem(
    @SerializedName("id_ulasan") val idUlasan: Int,
    @SerializedName("rating") val rating: Int,
    @SerializedName("komentar") val komentar: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("nama_user") val namaUser: String
)


// ── Statistik Rating ─────────────────────────────────────────
data class RatingStats(
    @SerializedName("rata_rata") val rataRata: Float,
    @SerializedName("total") val total: Int,
    @SerializedName("bintang_5") val bintang5: Int,
    @SerializedName("bintang_4") val bintang4: Int,
    @SerializedName("bintang_3") val bintang3: Int,
    @SerializedName("bintang_2") val bintang2: Int,
    @SerializedName("bintang_1") val bintang1: Int
)


// ── Response Ambil Ulasan ────────────────────────────────────
data class UlasanResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("stats") val stats: RatingStats?,
    @SerializedName("data") val data: List<UlasanItem>
)


// ── Response Kirim Ulasan ────────────────────────────────────
data class KirimUlasanResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)