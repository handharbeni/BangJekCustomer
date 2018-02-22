package com.bangjeck.stucture;

public class ChatData {

    String nama;
    String konten;
    String tanggal;

    public ChatData(String[] data){
        this.nama       = data[0];
        this.konten     = data[1];
        this.tanggal    = data[2];
    }

    public String getNama(){
        return nama;
    }
    public String getKonten(){
        return konten;
    }
    public String getTanggal(){
        return tanggal;
    }
}
