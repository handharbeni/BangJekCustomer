package com.bangjeck.page;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bangjeck.R;
import com.bangjeck.setting.BangJeckSetting;
import com.bangjeck.stucture.HistoryData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class History extends BangJeckSetting{

    int startList   = 0;
    int endList     = 20;

    FrameLayout loadpage;
    HistoryAdapter historyAdapter;
    LinearLayoutManager mLayoutManager;
    List<HistoryData> fixNumberList;
    RecyclerView recyclerView;
    ImageView back;
    ImageView help;
//    FloatingActionButton refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        show_notif = false;

        loadpage    = (FrameLayout)findViewById(R.id.loadpage);
        loadpage.setVisibility(View.VISIBLE);
        back        = (ImageView) findViewById(R.id.back);
        help        = (ImageView) findViewById(R.id.help);
//        refresh     = (FloatingActionButton)findViewById(R.id.refresh);

        setBack();
        setHelp();
//        setRefresh();

        HistoryOnline historyOnline = new HistoryOnline(this);
        historyOnline.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"historyOnline");
    }

    private class HistoryOnline extends AsyncTask<String,Void,Integer> {

        List<HistoryData> historyDatas;
        Context context;
        HistoryOnline(Context context){
            this.context        = context;
        }
        String test;
        protected Integer doInBackground(String...urls){
            int count = 0;
            try{
                historyDatas            = new ArrayList<>();
                String json             = request();
                test = json;
                JSONObject jsonObject   = new JSONObject(json);
                JSONArray jsonArray     = jsonObject.optJSONArray("json_history");
                count                   = jsonArray.length();
                if(count>0){
                    historyDatas.clear();
                    for(int i = 0; i<count; i++) {
                        jsonObject      = jsonArray.getJSONObject(i);
                        String[] data   = new String[]{
                                jsonObject.optString("kode_transaksi"),
                                jsonObject.optString("status"),
                                jsonObject.optString("jenis_transaksi"),
                                jsonObject.optString("nama_kurir"),
                                jsonObject.optString("lokasi_ambil"),
                                jsonObject.optString("keterangan_ambil"),
                                jsonObject.optString("lokasi_kirim"),
                                jsonObject.optString("keterangan_kirim"),
                                jsonObject.optString("jarak"),
                                jsonObject.optString("biaya_jarak"),
                                jsonObject.optString("item"),
                                jsonObject.optString("biaya_item"),
                                jsonObject.optString("biaya_dua_kurir"),
                                jsonObject.optString("total"),
                                jsonObject.optString("tanggal")
                        };
                        historyDatas.add(new HistoryData(data));
                        if(i==0){
                            kode_transaksi  = jsonObject.optString("kode_transaksi");
                            if(jsonObject.optString("status").equals("Belum Diambil")){
                                kode_status = "1";
                            }else if(jsonObject.optString("status").equals("Diproses Kurir")){
                                kode_status = "2";
                            }else if(jsonObject.optString("status").equals("Selesai")){
                                kode_status = "3";
                            }else if(jsonObject.optString("status").equals("Batal")){
                                kode_status = "4";
                            }else if(jsonObject.optString("status").equals("Bermasalah")){
                                kode_status = "5";
                            }else{
                                kode_status = "0";
                            }
                        }
                    }
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return count;
        }
        protected void onPostExecute(Integer result){
            if(result>0){
                initialization(historyDatas);
                loadpage.setVisibility(View.GONE);
            }else{
                Toast.makeText(context,"0 result.",Toast.LENGTH_LONG).show();
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

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(base_url+"history.html")
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
    void initialization(List<HistoryData> dataNumberList){
        fixNumberList   = dataNumberList;
        historyAdapter  = new HistoryAdapter(this);
        recyclerView    = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager  = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(historyAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView,final int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(newState==0){
                            startList   = mLayoutManager.findFirstVisibleItemPosition();
                            endList     = mLayoutManager.findLastVisibleItemPosition();
                            historyAdapter.notifyDataSetChanged();
                        }
                    }
                },1000);
            }
        });
    }
    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

        Context context;

        class MyViewHolder extends RecyclerView.ViewHolder {

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

            Button cancel;
            Button chat;

            MyViewHolder(View view) {
                super(view);
                icon_image          = view.findViewById(R.id.icon_image);
                kode_transaksi      = view.findViewById(R.id.kode_transaksi);
                status_transaksi    = view.findViewById(R.id.status_transaksi);
                jenis_transaksi     = view.findViewById(R.id.jenis_transaksi);

                nama_kurir          = view.findViewById(R.id.nama_kurir);
                lokasi_ambil        = view.findViewById(R.id.lokasi_ambil);
                keterangan_ambil    = view.findViewById(R.id.keterangan_ambil);
                lokasi_kirim        = view.findViewById(R.id.lokasi_kirim);
                keterangan_kirim    = view.findViewById(R.id.keterangan_kirim);
                jarak               = view.findViewById(R.id.jarak);
                item                = view.findViewById(R.id.item);
                biaya_jarak         = view.findViewById(R.id.biaya_jarak);
                biaya_item          = view.findViewById(R.id.biaya_item);
                biaya_dua_kurir     = view.findViewById(R.id.biaya_dua_kurir);
                total               = view.findViewById(R.id.total);
                tanggal             = view.findViewById(R.id.tanggal);

                cancel              = view.findViewById(R.id.batal);
                chat                = view.findViewById(R.id.chat);
            }
        }

        HistoryAdapter(Context context){
            this.context = context;
        }

        @Override
        public HistoryAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_adapter, parent, false);
            return new HistoryAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final HistoryAdapter.MyViewHolder holder, int position) {
            final HistoryData historyData = fixNumberList.get(position);
            if(historyData.getJenisTransaksi().equals("Ojek Argo")){
                holder.icon_image.setImageResource(R.drawable.ojekargo);
            }else if(historyData.getJenisTransaksi().equals("Pesan Makanan")){
                holder.icon_image.setImageResource(R.drawable.pesanmakanan);
            }else if(historyData.getJenisTransaksi().equals("Belanja")){
                holder.icon_image.setImageResource(R.drawable.belanja);
            }else if(historyData.getJenisTransaksi().equals("Kurir Kargo")){
                holder.icon_image.setImageResource(R.drawable.kurir);
            }else if(historyData.getJenisTransaksi().equals("Food Court")){
                holder.icon_image.setImageResource(R.drawable.foodcourt);
            }else if(historyData.getJenisTransaksi().equals("Tiket")){
                holder.icon_image.setImageResource(R.drawable.tiketnonton);
            }else{
                holder.icon_image.setImageResource(R.drawable.logobangjeck);
            }

            holder.kode_transaksi.setText(historyData.getKodeTransaksi());
            holder.status_transaksi.setText(historyData.getStatus());
            holder.jenis_transaksi.setText(historyData.getJenisTransaksi());

            holder.nama_kurir.setText(historyData.getNamaKurir());
            holder.lokasi_ambil.setText(historyData.getLokasiAmbil());
            holder.keterangan_ambil.setText(historyData.getKeteranganAmbil());
            holder.lokasi_kirim.setText(historyData.getLokasiKirim());
            holder.keterangan_kirim.setText(historyData.getKeteranganKirim());
            holder.jarak.setText(historyData.getJarak());
            holder.item.setText(historyData.getItem());
            holder.biaya_jarak.setText(historyData.getBiayaJarak());
            holder.biaya_item.setText(historyData.getBiayaItem());
            holder.biaya_dua_kurir.setText(historyData.getBiayaDuaKurir());
            holder.total.setText(historyData.getTotal());
            holder.tanggal.setText(historyData.getTanggal());

            if(historyData.getStatus().equals("Belum Diambil")){
                holder.cancel.setVisibility(View.VISIBLE);
            }else{
                holder.cancel.setVisibility(View.GONE);
                if(historyData.getStatus().equals("Diproses Kurir")){
                    holder.chat.setVisibility(View.VISIBLE);
                }else{
                    holder.chat.setVisibility(View.GONE);
                }
            }
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    peringatan(holder.getAdapterPosition());
                }
            });
            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToChat(historyData.getKodeTransaksi());
                }
            });
        }

        @Override
        public int getItemCount() {
            return fixNumberList.size();
        }
    }
    private static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        interface ClickListener {
            void onClick(View view, int position);
            void onLongClick(View view, int position);
        }

        private GestureDetector gestureDetector;
        private RecyclerTouchListener.ClickListener clickListener;

        RecyclerTouchListener(Context context, final RecyclerView recyclerView, final RecyclerTouchListener.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
    void peringatan(int position){
        final HistoryData historyData = fixNumberList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Membatalkan pesanan "+historyData.getKodeTransaksi()+"? Kami menyarankan Anda untuk menekan refresh untuk mengetahui status dari proses pemesanan.");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goToBatalPesan(historyData.getKodeTransaksi());
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
    private class BatalPesan extends AsyncTask<String,Void,String> {

        Context context;
        String kode;
        String json="";

        BatalPesan(Context context,String kode){
            this.context    = context;
            this.kode       = kode;
        }
        protected String doInBackground(String...urls){
            json    = request();
            return json;
        }
        protected void onPostExecute(String result){
            if(result.equals("1")){
                Toast.makeText(context,"Berhasil membatalkan pemesanan "+kode,Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(context,"Terjadi kesalahan.",Toast.LENGTH_LONG).show();
            }
        }
        String request(){
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            try{
                SharedPreferences preferences = getSharedPreferences("com.bangjeck", Context.MODE_PRIVATE);
                String user     = preferences.getString("old_phone","");
                String pass     = preferences.getString("old_password","");

                RequestBody formBody = new FormBody.Builder()
                        .add("user", user)
                        .add("password", pass)
                        .add("kode_transaksi", kode)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(base_url+"batal_pesan.html")
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
    void goToBatalPesan(String kode){
        BatalPesan batalPesan = new BatalPesan(this,kode);
        batalPesan.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"batalPesan");
    }
    void setBack(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBack();
            }
        });
    }
    void setHelp(){
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToHelp();
            }
        });
    }
    void goToHelp(){
        Intent i = new Intent(this,BangJeckBrowser.class);
        i.putExtra("url",base_url+"help_history.html");
        startActivity(i);
    }
//    void setRefresh(){
//        refresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                show_notif = false;
//                loadpage.setVisibility(View.VISIBLE);
//                HistoryOnline historyOnline = new HistoryOnline(getApplicationContext());
//                historyOnline.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"historyOnline");
//            }
//        });
//    }
    void goToBack(){
        try{
            int jenis   = getIntent().getExtras().getInt("jenis");
            if(jenis==1){
                goToMainMenu();
            }else{
                finish();
            }
        }catch (Exception ex){
            finish();
        }
    }
    void goToMainMenu(){
        Intent i = new Intent(this,MainMenu.class);
        startActivity(i);
        finish();
    }
    @Override
    public void onBackPressed() {
        goToBack();
    }
    void goToChat(String kode){
        Intent i = new Intent(this,Chat.class);
        i.putExtra("kode",kode);
        startActivity(i);
    }
}