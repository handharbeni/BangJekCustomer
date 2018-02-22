package com.bangjeck.stucture;

public class HistoryData {
    String kode_transaksi;
    String status;
    String jenis_transaksi;
    String nama_kurir;
    String lokasi_ambil;
    String keterangan_ambil;
    String lokasi_kirim;
    String keterangan_kirim;
    String jarak;
    String biaya_jarak;
    String item;
    String biaya_item;
    String biaya_dua_kurir;
    String total;
    String tanggal;

    public HistoryData(String[] data){
        kode_transaksi      = data[0];
        status              = data[1];
        jenis_transaksi     = data[2];
        nama_kurir          = data[3];
        lokasi_ambil        = data[4];
        keterangan_ambil    = data[5];
        lokasi_kirim        = data[6];
        keterangan_kirim    = data[7];
        jarak               = data[8];
        biaya_jarak         = data[9];
        item                = data[10];
        biaya_item          = data[11];
        biaya_dua_kurir     = data[12];
        total               = data[13];
        tanggal             = data[14];
    }

    public String getKodeTransaksi(){
        return kode_transaksi;
    }

    public String getStatus(){
        return status;
    }

    public String getJenisTransaksi(){
        return jenis_transaksi;
    }

    public String getNamaKurir(){
        return nama_kurir;
    }

    public String getLokasiAmbil(){
        return lokasi_ambil;
    }

    public String getKeteranganAmbil(){
        return keterangan_ambil;
    }

    public String getLokasiKirim(){
        return lokasi_kirim;
    }

    public String getKeteranganKirim(){
        return keterangan_kirim;
    }

    public String getJarak(){
        return jarak;
    }

    public String getBiayaJarak(){
        return biaya_jarak;
    }

    public String getItem(){
        return item;
    }

    public String getBiayaItem(){
        return biaya_item;
    }

    public String getBiayaDuaKurir(){
        return biaya_dua_kurir;
    }

    public String getTotal(){
        return total;
    }

    public String getTanggal(){
        return tanggal;
    }
}