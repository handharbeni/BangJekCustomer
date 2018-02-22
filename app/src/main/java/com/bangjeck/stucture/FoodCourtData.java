package com.bangjeck.stucture;

public class FoodCourtData {
    String nama;
    String harga;
    String foto;
    String id_merchant;

    public FoodCourtData(String[] data){
        this.nama   = data[0];
        this.harga  = data[1];
        this.foto   = data[2];
        this.id_merchant    = data[3];
    }

    public String getNama(){
        return nama;
    }
    public String getHarga(){
        return harga;
    }
    public  String getFoto(){
        return foto;
    }
    public String getIDMerchant(){
        return id_merchant;
    }
}
