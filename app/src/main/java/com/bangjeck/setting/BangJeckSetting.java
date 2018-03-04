package com.bangjeck.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;

public class BangJeckSetting extends AppCompatActivity{
    public static String base_url  = "http://31.220.53.148/bangjeck/";
//    public static String base_url  = "http://192.168.1.2/bangjeck/";
    public static boolean show_history  = false;
    public static boolean show_notif    = false;

    public static String kode_transaksi = "";
    public static String kode_status    = "";

    public static String dari       = "";
    public static String pesan      = "";
    public static String tanggal    = "";
    public static int status        = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MultiDex.install(this);
    }
}