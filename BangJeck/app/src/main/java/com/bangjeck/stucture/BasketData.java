package com.bangjeck.stucture;

public class BasketData {
    String nama;
    String harga;
    String foto;
    String id_menu;
    int jumlah;

    public BasketData(String[] data){
        this.nama       = data[0];
        this.harga      = data[1];
        this.foto       = data[2];
        this.id_menu    = data[3];
        this.jumlah     = 0;
    }

    public String getNama(){
        return nama;
    }
    public String getHarga(){
        return harga;
    }
    public String getFoto(){
        return foto;
    }
    public String getMenu(){
        return id_menu;
    }
    public void setJumlah(int jumlah){
        this.jumlah = jumlah;
    }
    public int getJumlah(){
        return jumlah;
    }
}
