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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bangjeck.R;
import com.bangjeck.setting.BangJeckSetting;
import com.bangjeck.stucture.BasketData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TiketList extends BangJeckSetting{

    int startList   = 0;
    int endList     = 10;

    FrameLayout loadpage;
    FoodCourtAdapter foodCourtAdapter;
    LinearLayoutManager mLayoutManager;
    List<BasketData> fixNumberList;
    RecyclerView recyclerView;
    ImageView back;
    ImageView help;

    String id_merchant;
    TextView merchant_name;
    EditText merchant_address;

    TextView jumlah;
    TextView total;

    int jumlah_item = 0;
    int total_bayar = 0;

    Button lanjut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tiket_list);

        loadpage    = (FrameLayout)findViewById(R.id.loadpage);
        loadpage.setVisibility(View.VISIBLE);
        back        = (ImageView) findViewById(R.id.back);
        help        = (ImageView) findViewById(R.id.help);
        merchant_name       = (TextView)findViewById(R.id.merchant_name);
        merchant_address    = (EditText)findViewById(R.id.merchant_address);

        jumlah      = (TextView)findViewById(R.id.jumlah);
        total       = (TextView)findViewById(R.id.total);

        lanjut      = (Button)findViewById(R.id.lanjut);

        try{
            id_merchant = getIntent().getExtras().getString("id_merchant");
        }catch (Exception ex){
            System.out.println(ex.toString());
        }

        setBack();
        setHelp();
        setLanjut();

        getDataOnline();
    }
    void getDataOnline(){
        FoodCourtOnline foodCourtOnline = new FoodCourtOnline(this);
        foodCourtOnline.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"foodCourtOnline");
    }
    private class FoodCourtOnline extends AsyncTask<String,Void,Integer> {

        String name;
        String address;
        List<BasketData> foodCourtDatas;
        Context context;
        FoodCourtOnline(Context context){
            this.context        = context;
        }
        String test;
        protected Integer doInBackground(String...urls){
            int count = 0;
            try{
                foodCourtDatas            = new ArrayList<>();
                String json             = request();
                test = json;
                JSONObject jsonObject   = new JSONObject(json);
                JSONArray jsonArray     = jsonObject.optJSONArray("json_menu");
                count                   = jsonArray.length();
                if(count>0){
                    foodCourtDatas.clear();
                    for(int i = 0; i<count; i++) {
                        jsonObject      = jsonArray.getJSONObject(i);
                        String[] data   = new String[]{
                                jsonObject.optString("merchant_menu"),
                                jsonObject.optString("price"),
                                jsonObject.optString("photo"),
                                jsonObject.optString("id_menu")
                        };
                        foodCourtDatas.add(new BasketData(data));
                        if(i==0){
                            name    = jsonObject.optString("name");
                            address = jsonObject.optString("address");
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
                initialization(foodCourtDatas);
                loadpage.setVisibility(View.GONE);

                merchant_name.setText(name);
                merchant_address.setText(address);
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
                        .add("id_merchant",id_merchant)
                        .add("category","4")
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(base_url+"merchant_list.html")
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
    void initialization(List<BasketData> dataNumberList){
        fixNumberList   = dataNumberList;
        foodCourtAdapter  = new FoodCourtAdapter(this);
        recyclerView    = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager  = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(foodCourtAdapter);

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
                            foodCourtAdapter.notifyDataSetChanged();
                            merchant_address.requestFocus();
                        }
                    }
                },1000);
            }
        });
    }
    private class FoodCourtAdapter extends RecyclerView.Adapter<FoodCourtAdapter.MyViewHolder> {

        Context context;

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView up;
            ImageView down;

            TextView nama;
            TextView harga;
            WebView browser;
            ProgressBar progressBar;

            boolean ssl = false;
            boolean errorPolicy;

            MyViewHolder(View view) {
                super(view);
                nama    = view.findViewById(R.id.nama);
                harga   = view.findViewById(R.id.harga);

                progressBar = view.findViewById(R.id.progress);
                browser     = view.findViewById(R.id.browser);

                up      = view.findViewById(R.id.up);
                down    = view.findViewById(R.id.down);

                browser.setWebChromeClient(new WebChromeClient(){
                    public void onProgressChanged(WebView view, int newProgress) {
                        progressBar.setProgress(newProgress);
                        if(newProgress>=100&&!errorPolicy){
                            progressBar.setVisibility(View.GONE);
                            browser.setVisibility(View.VISIBLE);
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
                            browser.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }

        FoodCourtAdapter(Context context){
            this.context = context;
        }

        @Override
        public FoodCourtAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tiket_adapter, parent, false);
            return new FoodCourtAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final FoodCourtAdapter.MyViewHolder holder, int position) {
            final BasketData foodCourtData = fixNumberList.get(position);
            holder.nama.setText(foodCourtData.getNama());
            holder.harga.setText(foodCourtData.getHarga()+" x "+Integer.toString(foodCourtData.getJumlah()));
            if(position>=startList&&position<=endList) {
                holder.browser.loadUrl(base_url + "gambar/" + foodCourtData.getFoto() + ".html");
            }

            holder.up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int jml = foodCourtData.getJumlah();
                    jml++;
                    fixNumberList.get(holder.getAdapterPosition()).setJumlah(jml);
                    holder.harga.setText(foodCourtData.getHarga()+" x "+Integer.toString(foodCourtData.getJumlah()));
                    countJumlah();
                }
            });
            holder.down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int jml = foodCourtData.getJumlah();
                    jml--;
                    if(jml<=0){
                        jml = 0;
                    }
                    fixNumberList.get(holder.getAdapterPosition()).setJumlah(jml);
                    holder.harga.setText(foodCourtData.getHarga()+" x "+Integer.toString(foodCourtData.getJumlah()));
                    countJumlah();
                }
            });
        }

        @Override
        public int getItemCount() {
            return fixNumberList.size();
        }
    }
    void countJumlah(){
        if(fixNumberList.size()>0){
            jumlah_item = 0;
            total_bayar = 0;
            for (int i = 0; i < fixNumberList.size(); i++) {
                jumlah_item += fixNumberList.get(i).getJumlah();

                int harga   = 0;
                try{
                    String temp = fixNumberList.get(i).getHarga();
                    temp        = temp.replace(".","");
                    temp        = temp.replace(" ","");
                    temp        = temp.replace("Rp","");
                    harga       = Integer.parseInt(temp);
                }catch (Exception ex){
                    harga   = 0;
                }
                total_bayar += (harga*fixNumberList.get(i).getJumlah());
            }
        }
        jumlah.setText(Integer.toString(jumlah_item));
        total.setText("Rp. "+Integer.toString(total_bayar));
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
    @Override
    public void onBackPressed() {
        goToBack();
    }
    void goToBack(){
        new AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Yakin ingin kembali ke halaman menu? Item yang telah diinputkan akan hilang.")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).create().show();
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
        i.putExtra("url",base_url+"help_ticket.html");
        startActivity(i);
    }
    void setLanjut(){
        lanjut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(jumlah.getText().toString())>0){
                    goToFoodCourt();
                }else{
                    Toast.makeText(getApplicationContext(),"Anda belum memesan apapun.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    void goToFoodCourt(){
        Intent i = new Intent(this,Tiket.class);
        i.putExtra("lokasi_ambil",merchant_address.getText().toString());
        i.putExtra("keterangan_ambil",merchant_name.getText().toString());

        String item = "-";
        int biaya   = 0;
        if(fixNumberList.size()>0){
            item    = "";
            for (int j = 0; j < fixNumberList.size(); j++) {
                item    += Integer.toString(fixNumberList.get(j).getJumlah())+" x "+fixNumberList.get(j).getNama()+"\n";

                int harga   = 0;
                try{
                    String temp = fixNumberList.get(j).getHarga();
                    temp        = temp.replace(".","");
                    temp        = temp.replace(" ","");
                    temp        = temp.replace("Rp","");
                    harga       = Integer.parseInt(temp);
                }catch (Exception ex){
                    harga   = 0;
                }
                biaya += (harga*fixNumberList.get(j).getJumlah());
            }
        }

        if(biaya>200000){
        }else if(biaya>150000){
            biaya   = 200000;
        }else if(biaya>100000){
            biaya   = 150000;
        }else if(biaya>50000){
            biaya   = 100000;
        }else{
            biaya   = 50000;
        }

        i.putExtra("item",item);
        i.putExtra("biaya_item",Integer.toString(biaya));
        startActivity(i);
    }
}