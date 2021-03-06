package com.bangjeck.page;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bangjeck.R;
import com.bangjeck.library.GPSTracker;
import com.bangjeck.setting.BangJeckSetting;

import java.nio.charset.StandardCharsets;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FoodCourt extends BangJeckSetting {

    boolean ambil_status = false;
    boolean kirim_status = false;
    boolean ssl = false;
    boolean errorPolicy;
    boolean cari = false;
    boolean loading = true;
    boolean typing = false;
    double lat  = -3.348397;
    double lon  = 114.618929;
    int biaya_fix   = 0;
    int jarak_fix   = 0;

    Button submit;
    EditText keterangan_ambil;
    EditText keterangan_kirim;
    EditText lokasi_ambil;
    EditText lokasi_kirim;
    ImageView back;
    ImageView help;
    ImageView shownote1;
    ImageView shownote2;
    ProgressBar progressBar;
    String awal;
    String akhir;
    TextView jarak;
    TextView biaya_jarak;
    WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.food_court);

        submit              = (Button) findViewById(R.id.submit);
        biaya_jarak         = (TextView) findViewById(R.id.biaya_jarak);
        jarak               = (TextView) findViewById(R.id.jarak);
        lokasi_ambil        = (EditText) findViewById(R.id.lokasi_ambil);
        lokasi_kirim        = (EditText) findViewById(R.id.lokasi_kirim);
        keterangan_ambil    = (EditText) findViewById(R.id.keterangan_ambil);
        keterangan_kirim    = (EditText) findViewById(R.id.keterangan_kirim);
        progressBar         = (ProgressBar)findViewById(R.id.progress);
        shownote1           = (ImageView) findViewById(R.id.shownote1);
        shownote2           = (ImageView) findViewById(R.id.shownote2);
        back                = (ImageView) findViewById(R.id.back);
        help                = (ImageView) findViewById(R.id.help);
        progressBar.setVisibility(View.VISIBLE);

        try{
            lokasi_ambil.setText(getIntent().getExtras().getString("lokasi_ambil"));
            keterangan_ambil.setText(getIntent().getExtras().getString("keterangan_ambil"));
        }catch (Exception ex){
            System.out.println(ex.toString());
        }

        String def = getDefault();
        getBrowser(def,def);
        getShowNote();
        getWatcher();
        getSubmit();
        getBack();
        getHelp();
        ulang();
    }
    String getDefault(){
        try{
            GPSTracker gps = new GPSTracker(this);
            if(gps.canGetLocation()){
                lat  = gps.getLatitude();
                lon  = gps.getLongitude();
                GetAddress getAddress = new GetAddress(this);
                getAddress.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"getAddress");
            }else{
                alertGPS();
            }
        }catch (Exception ex){
            alertGPS();
        }
        return Double.toString(lat)+","+Double.toString(lon);
    }
    void alertGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Tidak mendapatkan posisi GPS. Nyalakan permission GPS pada pengaturan.");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    void getJarak(){
        jarak.setText("Loading...");
        CalculateJarak calculateJarak = new CalculateJarak(this);
        calculateJarak.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"calculateJarak");
    }
    void getShowNote(){
        shownote1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ambil_status    = !ambil_status;
                if(ambil_status){
                    keterangan_ambil.setVisibility(View.VISIBLE);
                }else{
                    keterangan_ambil.setVisibility(View.GONE);
                }
            }
        });
        shownote2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kirim_status    = !kirim_status;
                if(kirim_status){
                    keterangan_kirim.setVisibility(View.VISIBLE);
                }else{
                    keterangan_kirim.setVisibility(View.GONE);
                }
            }
        });
    }
    void getSubmit(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPemesanan();
            }
        });
    }
    void getBrowser(String start, String end){
        awal    = encode(start);
        akhir   = encode(end);

        awal    = awal.replace("+","-");
        awal    = awal.replace("/","_");
        akhir   = akhir.replace("+","-");
        akhir   = akhir.replace("/","_");

        String url = base_url+"maps/"+awal+"/"+akhir+".html";

        browser = (WebView)findViewById(R.id.browser);
        browser.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if(newProgress>=100&&!errorPolicy){
                    progressBar.setVisibility(View.GONE);
                    loading = false;
                    getJarak();
                }
            }
        });
        browser.setWebViewClient(new WebViewClient(){
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Handle the error
                errorPolicy = true;
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(!ssl&&errorPolicy){
                    Toast.makeText(getApplicationContext(),"Terjadi kesalahan koneksi.",Toast.LENGTH_SHORT).show();
                    browser.setVisibility(View.GONE);
                }
            }
        });
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setBuiltInZoomControls(false);
        browser.loadUrl(url);
    }
    void getWatcher(){
        lokasi_ambil.addTextChangedListener(tw1);
        lokasi_kirim.addTextChangedListener(tw1);
    }
    void goToDetailPesan(){
        Intent i = new Intent(getApplication().getApplicationContext(),Pemesanan.class);
        i.putExtra("jenis","Food Court");
        i.putExtra("lokasi_ambil",lokasi_ambil.getText().toString());
        i.putExtra("keterangan_ambil",keterangan_ambil.getText().toString());
        i.putExtra("lokasi_kirim",lokasi_kirim.getText().toString());
        i.putExtra("keterangan_kirim",keterangan_kirim.getText().toString());
        i.putExtra("jarak",Integer.toString(jarak_fix));
        i.putExtra("biaya_jarak",Integer.toString(biaya_fix));

        i.putExtra("item",getIntent().getExtras().getString("item"));
        i.putExtra("biaya_item",getIntent().getExtras().getString("biaya_item"));
        i.putExtra("biaya_dua_kurir","0");
        i.putExtra("total",Integer.toString(biaya_fix));

        i.putExtra("lat",Double.toString(lat));
        i.putExtra("lon",Double.toString(lon));
        startActivity(i);
    }
    void goToPemesanan(){
        if(jarak_fix>0&&biaya_fix>0){
            goToDetailPesan();
        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Warning");
            builder.setMessage("Lokasi ambil atau lokasi antar tidak valid. Isikan lokasi yang valid.");
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
    public String encode(String text){
        byte[] data = text.getBytes(StandardCharsets.UTF_8);
        return Base64.encodeToString(data, Base64.DEFAULT);
    }
    TextWatcher tw1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            typing  = true;
            cari    = false;
            jarak_fix   = 0;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            cari    = true;
            jarak_fix   = 0;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            typing  = false;
            jarak_fix   = 0;
        }
    };
    void cariLokasi(){
        if(!typing&&!loading){
            String ambil    = lokasi_ambil.getText().toString();
            String kirim    = lokasi_kirim.getText().toString();
            if(ambil.length()>3&&kirim.length()>3) {
                loading = true;
                progressBar.setVisibility(View.VISIBLE);
                getBrowser(ambil, kirim);
            }
        }
    }
    void ulang(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(cari){
                    cari = false;
                    cariLokasi();
                }
                ulang();
            }
        },3000);
    }
    private class CalculateJarak extends AsyncTask<String,Void,Integer> {
        Context context;
        String data = "";

        CalculateJarak(Context context) {
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
            String string_jarak = "0";
            try{
                int jrk         = Integer.parseInt(data);
                String jrks     = Integer.toString(jrk)+" Km";
                string_jarak    = jrks;
                jarak.setText(jrks);
                jarak_fix       = jrk;
            }catch (Exception ex){
                jarak_fix       = 0;
                jarak.setText("0 Km");
            }

            if(!string_jarak.equals("0")){
                biaya_jarak.setText("Menghitung biaya...");
                CalculateBiaya calculateBiaya = new CalculateBiaya(context,string_jarak);
                calculateBiaya.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"calculateBiaya");
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
                        .build();

                awal    = encode(lokasi_ambil.getText().toString());
                akhir   = encode(lokasi_kirim.getText().toString());

                awal    = awal.replace("+","-");
                awal    = awal.replace("/","_");
                akhir   = akhir.replace("+","-");
                akhir   = akhir.replace("/","_");

                String url = base_url+"jarak_from_app/"+awal+"/"+akhir+".html";

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
    private class CalculateBiaya extends AsyncTask<String,Void,Integer> {
        Context context;
        String data = "";
        String jarak;

        CalculateBiaya(Context context,String jarak) {
            this.context    = context;
            this.jarak      = jarak;
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
            try{
                int jrk     = Integer.parseInt(data);
                String jrks = "Rp. "+Integer.toString(jrk);
                biaya_jarak.setText(jrks);
                biaya_fix   = jrk;
            }catch (Exception ex){
                biaya_fix   = 0;
                biaya_jarak.setText("Rp. 0");
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
                        .build();

                String url = base_url+"harga_perkm/"+jarak+"/"+Double.toString(lat)+"/"+Double.toString(lon)+".html";

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
    private class GetAddress extends AsyncTask<String,Void,Integer> {
        Context context;
        String data = "";

        GetAddress(Context context) {
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
            if(data.length()>0){
                lokasi_kirim.setText(data);
                cari    = true;
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
                        .build();

                String url = base_url+"get_address/"+Double.toString(lat)+"/"+Double.toString(lon)+".html";

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
        i.putExtra("url",base_url+"help_argo.html");
        startActivity(i);
    }
}