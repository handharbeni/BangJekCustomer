package com.bangjeck.page;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bangjeck.R;
import com.bangjeck.setting.BangJeckSetting;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainMenu extends BangJeckSetting {

    boolean ssl = false;
    boolean errorPolicy;

    FrameLayout loadpage;
    LinearLayout argo;
    LinearLayout pesan_makanan;
    LinearLayout belanja;
    LinearLayout kurir_kargo;
    LinearLayout food_court;
    LinearLayout tiket_poster;
    LinearLayout cs;
    LinearLayout ads;
    LinearLayout berita;
    ProgressBar progressBar;
    TextView terms;
    LinearLayout account;
    LinearLayout history;
    WebView browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainmenu);

        argo        = (LinearLayout)findViewById(R.id.argo);
        pesan_makanan   = (LinearLayout)findViewById(R.id.pesan_makan);
        belanja     = (LinearLayout)findViewById(R.id.belanja);
        kurir_kargo = (LinearLayout)findViewById(R.id.kurir);
        food_court  = (LinearLayout)findViewById(R.id.food_court);
        tiket_poster= (LinearLayout)findViewById(R.id.tiket);
        cs          = (LinearLayout)findViewById(R.id.cs);
        ads         = (LinearLayout)findViewById(R.id.jeck_ads);
        berita      = (LinearLayout)findViewById(R.id.berita);
        account     = (LinearLayout)findViewById(R.id.account);
        history     = (LinearLayout)findViewById(R.id.history);
        loadpage    = (FrameLayout)findViewById(R.id.loadpage);
        terms       = (TextView)findViewById(R.id.terms);
        loadpage.setVisibility(View.VISIBLE);

        CheckMaintenance checkMaintenance = new CheckMaintenance(this);
        checkMaintenance.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"maintenance");

        ulang();
        if(getLocationMode()<2){
            displayPromptForEnablingGPS();
        }
    }
    int u   = 0;
    int c   = 0;
    void ulang(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(show_history){
                    show_history = false;
                    goToHistory();
                }
                u++;
                if(u>=10){
                    u   = 0;
                    c++;
                    if(!show_notif){
                        show_notif = true;
                        CheckNotifikasi checkNotifikasi = new CheckNotifikasi(getApplicationContext());
                        checkNotifikasi.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"checkNotifikasi");
                    }
                }
                if(c>=2){
                    c   = 0;
                    show_notif = false;
                }
                ulang();
            }
        }, 1000);
    }
    private class CheckMaintenance extends AsyncTask<String,Void,Integer> {
        Context context;
        boolean maintenance = false;

        CheckMaintenance(Context context) {
            this.context    = context;
        }
        protected Integer doInBackground(String...urls){
            try{
                if(!request().equals("0")){
                    maintenance = true;
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return 0;
        }
        protected void onPostExecute(Integer result){
            if(maintenance){
                String url  = base_url+"maintenance.html";
                goToBangJeckBrowser(url,true);
            }else{
                loadpage.setVisibility(View.GONE);
                getAccount();
                getHistory();
                getSlide();
                getArgo();
                getPesanMakanan();
                getBelanja();
                getKurirKargo();
                getFoodCourt();
                getTiketPoster();
                getAds();
                getBerita();
                getCS();
                getTerms();
            }
        }
        String request(){
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            String json = "";
            try{
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(base_url+"maintenance_status.html")
                        .build();

                Response response = client.newCall(request).execute();
                json = response.body().string();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return json;
        }
    }
    void getAccount(){
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToProfile();
            }
        });
    }
    void getArgo(){
        argo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToArgo();
            }
        });
    }
    void getPesanMakanan(){
        pesan_makanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPesanMakanan();
            }
        });
    }
    void getBelanja(){
        belanja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBelanja();
            }
        });
    }
    void getKurirKargo(){
        kurir_kargo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToKurirKargo();
            }
        });
    }
    void getFoodCourt(){
        food_court.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFoodCourt();
            }
        });
    }
    void getTiketPoster(){
        tiket_poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTiketPoster();
            }
        });
    }
    void getCS(){
        cs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertCallCS();
            }
        });
    }
    void getAds(){
        ads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAds();
            }
        });
    }
    void getHistory(){
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHistory();
            }
        });
    }
    void getSlide(){
        progressBar = (ProgressBar)findViewById(R.id.progress);
        String url = base_url+"slider.html";

        browser = (WebView)findViewById(R.id.browser);
        browser.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                if(newProgress>=100&&!errorPolicy){
                    progressBar.setVisibility(View.GONE);
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
        browser.clearCache(true);
        browser.clearHistory();
        browser.clearFormData();
        browser.getSettings().setJavaScriptEnabled(true);
        browser.getSettings().setBuiltInZoomControls(false);
        browser.loadUrl(url);
    }
    void goToArgo(){
        if(checkLogin()){
            stillIn(1);
        }else{
            Toast.makeText(this,"Anda belum login.",Toast.LENGTH_LONG).show();
            goToProfile();
        }
    }
    void goToPesanMakanan(){
        if(checkLogin()){
            stillIn(2);
        }else{
            Toast.makeText(this,"Anda belum login.",Toast.LENGTH_LONG).show();
            goToProfile();
        }
    }
    void goToBelanja(){
        if(checkLogin()){
            stillIn(3);
        }else{
            Toast.makeText(this,"Anda belum login.",Toast.LENGTH_LONG).show();
            goToProfile();
        }
    }
    void alertCallCS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Menggunakan fitur ini mungkin akan dikenakan biaya pulsa reguler. Tetap menelfon?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goToCS();
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
    void goToCS(){
        Intent i = new Intent(Intent.ACTION_CALL);
        String posted_by = "0877-0077-0012";
        String uri = "tel:" + posted_by.trim();
        i.setData(Uri.parse(uri));
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 5);
            }else{
                startActivity(i);
            }
        }else{
            startActivity(i);
        }
    }
    void goToKurirKargo(){
        if(checkLogin()){
            stillIn(4);
        }else{
            Toast.makeText(this,"Anda belum login.",Toast.LENGTH_LONG).show();
            goToProfile();
        }
    }
    void goToFoodCourt(){
        if(checkLogin()){
            stillIn(5);
        }else{
            Toast.makeText(this,"Anda belum login.",Toast.LENGTH_LONG).show();
            goToProfile();
        }
    }
    void goToTiketPoster(){
        if(checkLogin()){
            stillIn(6);
        }else{
            Toast.makeText(this,"Anda belum login.",Toast.LENGTH_LONG).show();
            goToProfile();
        }
    }
    void getTerms(){
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTerms();
            }
        });
    }
    void getBerita(){
        berita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBerita();
            }
        });
    }
    void goToBangJeckBrowser(String url,boolean finish_it){
        Intent i = new Intent(this,BangJeckBrowser.class);
        i.putExtra("url",url);
        startActivity(i);
        if(finish_it){
            finish();
        }
    }
    void goToHistory(){
        Intent i = new Intent(this,History.class);
        startActivity(i);
    }
    void goToProfile(){
        Intent i = new Intent(this,Profile.class);
        startActivity(i);
    }
    void goToAds(){
        Intent i = new Intent(this,BangJeckBrowser.class);
//        i.putExtra("url","http://goo.gl/eyq5cU");
        i.putExtra("url","http://bangjeck.com/");
        startActivity(i);
    }
    void goToBerita(){
        Intent i = new Intent(this,BangJeckBrowser.class);
        i.putExtra("url","http://banjarmasin.tribunnews.com/");
        startActivity(i);
    }
    void goToTerms(){
        Intent i = new Intent(this,BangJeckBrowser.class);
        i.putExtra("url",base_url+"privacy_policy.html");
        startActivity(i);
    }
    boolean checkLogin(){
        SharedPreferences preferences = getSharedPreferences("com.bangjeck", Context.MODE_PRIVATE);
        String user     = preferences.getString("old_phone","");
        String pass     = preferences.getString("old_password","");
        if(user.length()>0&&pass.length()>0){
            return true;
        }else{
            return false;
        }
    }
    void stillIn(int still){
        loadpage.setVisibility(View.VISIBLE);
        CheckStill checkStill = new CheckStill(this,still);
        checkStill.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"checkStill");
    }
    private class CheckStill extends AsyncTask<String,Void,Integer> {
        Context context;
        boolean dissallowed = false;
        int still = 0;

        CheckStill(Context context,int still) {
            this.context    = context;
            this.still      = still;
        }
        protected Integer doInBackground(String...urls){
            try{
                String hasil = request();
                if(hasil.equals("-1")){
                    dissallowed = true;
                }else if(!hasil.equals("0")){
                    dissallowed = true;
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return 0;
        }
        protected void onPostExecute(Integer result){
            if(dissallowed){
                showAlert(still);
            }else{
                loadpage.setVisibility(View.GONE);
                goTo(still);
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

                String url = base_url+"still_transaction.html";

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
    void showAlert(final int still){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Anda masih memiliki transaksi yang belum selesai.");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int j) {
                loadpage.setVisibility(View.GONE);
//                goTo(still);
                goToHistory();
            }
        });
//        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                loadpage.setVisibility(View.GONE);
//                goToHistory();
//            }
//        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    void goTo(int still){
        Intent i = null;
        switch (still){
            case 1:
                i = new Intent(this,Argo.class);
                break;
            case 2:
                i = new Intent(this,PesanMakanan.class);
                break;
            case 3:
                i = new Intent(this,Belanja.class);
                break;
            case 4:
                i = new Intent(this,KurirDanKargo.class);
                break;
            case 5:
                i = new Intent(this,FoodCourtList.class);
                break;
            case 6:
                i = new Intent(this,TiketPoster.class);
                break;
        }
        startActivity(i);
    }
    public void displayPromptForEnablingGPS() {
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        String message = "Kami membutuhkan GPS dalam keadaan menyala untuk performa lebih baik. Buka pengaturan GPS?";

        builder.setMessage(message)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                d.dismiss();
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }
    public int getLocationMode() {
        try{
            return Settings.Secure.getInt(this.getContentResolver(),Settings.Secure.LOCATION_MODE);
        }catch (Exception ex){
            return 0;
        }
    }
    void notifikasi(int jenis){
        String isi_text = "Anda mendapatkan notifikasi.";
        switch(jenis){
            case 1:
                isi_text    = "Anda sudah mendapatkan driver";
                break;
            case 2:
                isi_text    = "Kami mencari driver baru";
                break;
        }

        Intent notificationIntent = new Intent(getApplicationContext(), History.class);
        notificationIntent.putExtra("jenis",1);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainMenu.class);
        stackBuilder.addNextIntent(notificationIntent);

        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(1, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.logobangjeck)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logobangjeck))
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentTitle(getString(R.string.app_name))
                .setContentIntent(notificationPendingIntent)
                .setContentText(isi_text)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSound(Uri.parse("android.resource://com.bangjeck/" + R.raw.notif))
                .setPriority(NotificationManager.IMPORTANCE_HIGH);

        builder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, builder.build());
    }
    private class CheckNotifikasi extends AsyncTask<String,Void,Integer> {
        Context context;
        String status="0";

        CheckNotifikasi(Context context) {
            this.context    = context;
        }
        protected Integer doInBackground(String...urls){
            try{
                String s = request();
                if(s.equals("1")){
                    status = "1";
                }else if(s.equals("2")){
                    status = "2";
                }else{
                    status = s;
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return 0;
        }
        protected void onPostExecute(Integer result){
            if(status.equals("1")||status.equals("2")){
                if(status.equals("2")) {
                    kode_status = "1";
                }else{
                    kode_status = "2";
                }
                notifikasi(Integer.parseInt(status));
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
                        .add("kode", kode_transaksi)
                        .add("status", kode_status)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(base_url+"status_history.html")
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
}