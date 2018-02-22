package com.bangjeck.page;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bangjeck.R;
import com.bangjeck.setting.BangJeckSetting;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Pemesanan extends BangJeckSetting {

    Button batal;
    Button lanjut;

    FrameLayout loadpage;
    ImageView back;
    ImageView help;
    ImageView icon_image;
    TextView kode_transaksi;
    TextView status_transaksi;
    TextView jenis_transaksi;

    TextView nama_kurir;
    TextView lokasi_ambil;
    TextView keterangan_ambil;
    TextView lokasi_kirim;
    TextView keterangan_kirim;
    TextView jarak;
    TextView item;
    TextView biaya_jarak;
    TextView biaya_item;
    TextView biaya_dua_kurir;
    TextView total;
    TextView tanggal;

    String send_jenis_transaksi;
    String send_lokasi_ambil;
    String send_keterangan_ambil;
    String send_lokasi_kirim;
    String send_keterangan_kirim;
    String send_jarak;
    String send_biaya_jarak;
    String send_item;
    String send_biaya_item;
    String send_biaya_dua_kurir;
    String lat;
    String lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pemesanan);

        loadpage            = (FrameLayout) findViewById(R.id.loadpage);
        batal               = (Button) findViewById(R.id.batal);
        lanjut              = (Button) findViewById(R.id.lanjut);

        icon_image          = (ImageView) findViewById(R.id.icon_image);
        kode_transaksi      = (TextView) findViewById(R.id.kode_transaksi);
        status_transaksi    = (TextView) findViewById(R.id.status_transaksi);
        jenis_transaksi     = (TextView) findViewById(R.id.jenis_transaksi);

        nama_kurir          = (TextView) findViewById(R.id.nama_kurir);
        lokasi_ambil        = (TextView) findViewById(R.id.lokasi_ambil);
        keterangan_ambil    = (TextView) findViewById(R.id.keterangan_ambil);
        lokasi_kirim        = (TextView) findViewById(R.id.lokasi_kirim);
        keterangan_kirim    = (TextView) findViewById(R.id.keterangan_kirim);
        jarak               = (TextView) findViewById(R.id.jarak);
        item                = (TextView) findViewById(R.id.item);
        biaya_jarak         = (TextView) findViewById(R.id.biaya_jarak);
        biaya_item          = (TextView) findViewById(R.id.biaya_item);
        biaya_dua_kurir     = (TextView) findViewById(R.id.biaya_dua_kurir);
        total               = (TextView) findViewById(R.id.total);
        tanggal             = (TextView) findViewById(R.id.tanggal);
        back                = (ImageView) findViewById(R.id.back);
        help                = (ImageView) findViewById(R.id.help);

        getDataDetail();
        getBatal();
        getLanjut();
        getBack();
        getHelp();
    }
    void getDataDetail(){
        try{
            Bundle data     = getIntent().getExtras();
            String jenis    = data.getString("jenis");
            if(jenis.equals("Ojek Argo")){
                icon_image.setImageResource(R.drawable.ojekargo);
            }else if(jenis.equals("Pesan Makanan")){
                icon_image.setImageResource(R.drawable.pesanmakanan);
            }else if(jenis.equals("Belanja")){
                icon_image.setImageResource(R.drawable.belanja);
            }else if(jenis.equals("Kurir Kargo")){
                icon_image.setImageResource(R.drawable.kurir);
            }else if(jenis.equals("Food Court")){
                icon_image.setImageResource(R.drawable.foodcourt);
            }else if(jenis.equals("Tiket")){
                icon_image.setImageResource(R.drawable.tiketnonton);
            }

            kode_transaksi.setText("-");
            status_transaksi.setText(getResources().getString(R.string.unconfirmed));
            jenis_transaksi.setText(jenis);

            String data_lokasi_ambil        = data.getString("lokasi_ambil");
            String data_keterangan_ambil    = data.getString("keterangan_ambil");
            if(data_keterangan_ambil.length()<=0){
                data_keterangan_ambil       = "-";
            }
            String data_lokasi_kirim        = data.getString("lokasi_kirim");
            String data_keterangan_kirim    = data.getString("keterangan_kirim");
            if(data_keterangan_kirim.length()<=0){
                data_keterangan_kirim       = "-";
            }
            String data_jarak               = data.getString("jarak");
            String data_biaya_jarak         = data.getString("biaya_jarak");
            String data_item                = data.getString("item");
            String data_biaya_item          = data.getString("biaya_item");
            String data_biaya_dua_kurir     = data.getString("biaya_dua_kurir");
            String data_total               = data.getString("total");

            nama_kurir.setText("-");
            lokasi_ambil.setText(data_lokasi_ambil);
            keterangan_ambil.setText(data_keterangan_ambil);
            lokasi_kirim.setText(data_lokasi_kirim);
            keterangan_kirim.setText(data_keterangan_kirim);
            jarak.setText(data_jarak+" Km");
            item.setText(data_item);
            biaya_jarak.setText(data_biaya_jarak);
            biaya_item.setText(data_biaya_item);
            biaya_dua_kurir.setText(data_biaya_dua_kurir);
            total.setText(data_total);
            tanggal.setVisibility(View.GONE);

            send_jenis_transaksi    = jenis;
            send_lokasi_ambil       = data_lokasi_ambil;
            send_keterangan_ambil   = data_keterangan_ambil;
            send_lokasi_kirim       = data_lokasi_kirim;
            send_keterangan_kirim   = data_keterangan_kirim;
            send_jarak              = data_jarak;
            send_biaya_jarak        = data_biaya_jarak;
            send_item               = data_item;
            send_biaya_item         = data_biaya_item;
            send_biaya_dua_kurir    = data_biaya_dua_kurir;

            lat = data.getString("lat");
            lon = data.getString("lon");
        }catch (Exception ex){
            Toast.makeText(this,"Terjadi kesalahan.",Toast.LENGTH_LONG).show();
        }
    }
    void getBatal(){
        batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBatal();
            }
        });
    }
    void getLanjut(){
        lanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLanjut();
            }
        });
    }
    void goToBatal(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Yakin mau membatalkan transaksi?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    void goToInsertTransaksi(){
        loadpage.setVisibility(View.VISIBLE);
        InsertTransaksi insertTransaksi = new InsertTransaksi(this);
        insertTransaksi.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"insertTransaksi");
    }
    void goToLanjut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Apakah pengisian data sudah benar dan setuju dengan biaya pemesanan?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goToInsertTransaksi();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    void gotToMainMenu(){
        show_history    = true;
        Intent i = new Intent(this,MainMenu.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(i);
    }
    private class InsertTransaksi extends AsyncTask<String,Void,Integer> {
        Context context;
        String data = "";

        InsertTransaksi(Context context) {
            this.context    = context;
        }
        protected Integer doInBackground(String...urls){
            try{
                data   = request();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return 0;
        }
        protected void onPostExecute(Integer result){
            loadpage.setVisibility(View.GONE);
            if(data.equals("1")){
                gotToMainMenu();
            }else{
                Toast.makeText(context,"Terjadi kesalahan."+data,Toast.LENGTH_LONG).show();
            }
        }
        String request(){
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            String json = "";
            try{
                SharedPreferences preferences = getSharedPreferences("com.bangjeck", Context.MODE_PRIVATE);
                String user     = preferences.getString("old_phone","");
                String pass     = preferences.getString("old_password","");

                RequestBody formBody = new FormBody.Builder()
                        .add("user", user)
                        .add("password", pass)
                        .add("jenis_transaksi", send_jenis_transaksi)
                        .add("lokasi_ambil", send_lokasi_ambil)
                        .add("keterangan_ambil", send_keterangan_ambil)
                        .add("lokasi_kirim", send_lokasi_kirim)
                        .add("keterangan_kirim", send_keterangan_kirim)
                        .add("jarak", send_jarak)
                        .add("biaya_jarak", send_biaya_jarak)
                        .add("item", send_item)
                        .add("biaya_item", send_biaya_item)
                        .add("biaya_dua_kurir", send_biaya_dua_kurir)
                        .add("lat", lat)
                        .add("lon", lon)
                        .build();

                String url = base_url+"transaksi_from_app.html";

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();
                json = response.body().string();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return json;
        }
    }
    void getBack(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    void getHelp(){
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHelp();
            }
        });
    }
    void goToHelp(){
        Intent i = new Intent(this,BangJeckBrowser.class);
        i.putExtra("url",base_url+"help_pemesanan.html");
        startActivity(i);
    }
}