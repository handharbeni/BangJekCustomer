package com.bangjeck.page;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
import com.bangjeck.stucture.FoodCourtData;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TiketPoster extends BangJeckSetting{

    int startList   = 0;
    int endList     = 10;

    EditText cari;
    ImageView tombol_cari;
    FrameLayout loadpage;
    FoodCourtAdapter foodCourtAdapter;
    LinearLayoutManager mLayoutManager;
    List<FoodCourtData> fixNumberList;
    RecyclerView recyclerView;
    ImageView back;
    ImageView help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tiket_poster);

        loadpage    = (FrameLayout)findViewById(R.id.loadpage);
        loadpage.setVisibility(View.VISIBLE);
        back        = (ImageView) findViewById(R.id.back);
        help        = (ImageView) findViewById(R.id.help);
        cari        = (EditText) findViewById(R.id.cari);
        tombol_cari = (ImageView) findViewById(R.id.tombol_cari);

        setBack();
        setHelp();
        setCari();
        setTombolCari();

        getDataOnline();
    }
    void getDataOnline(){
        FoodCourtOnline foodCourtOnline = new FoodCourtOnline(this);
        foodCourtOnline.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"foodCourtOnline");
    }
    void setCari(){
        cari.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    tombol_cari.performClick();
                    return true;
                }
                return false;
            }
        });
    }
    void setTombolCari(){
        tombol_cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDataOnline();
            }
        });
    }
    private class FoodCourtOnline extends AsyncTask<String,Void,Integer> {

        List<FoodCourtData> foodCourtDatas;
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
                                jsonObject.optString("id_merchant")
                        };
                        foodCourtDatas.add(new FoodCourtData(data));
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
                        .add("cari", cari.getText().toString())
                        .add("category","4")
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(base_url+"cari.html")
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
    void initialization(List<FoodCourtData> dataNumberList){
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
                        }
                    }
                },1000);
            }
        });
    }
    private class FoodCourtAdapter extends RecyclerView.Adapter<FoodCourtAdapter.MyViewHolder> {

        Context context;

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView nama;
            TextView harga;
            WebView browser;
            ProgressBar progressBar;
            Button pesan;

            boolean ssl = false;
            boolean errorPolicy;

            MyViewHolder(View view) {
                super(view);
                nama    = view.findViewById(R.id.nama);
                harga   = view.findViewById(R.id.harga);
                pesan   = view.findViewById(R.id.pesan);

                progressBar = (ProgressBar)view.findViewById(R.id.progress);
                browser     = (WebView)view.findViewById(R.id.browser);

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
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tiket_poster_adapter, parent, false);
            return new FoodCourtAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final FoodCourtAdapter.MyViewHolder holder, int position) {
            final FoodCourtData foodCourtData = fixNumberList.get(position);
            holder.nama.setText(foodCourtData.getNama());
            holder.harga.setText(foodCourtData.getHarga());
            if(position>=startList&&position<=endList) {
                holder.browser.loadUrl(base_url+"gambar/"+foodCourtData.getFoto()+".html");
            }

            holder.pesan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToMerchantList(foodCourtData.getIDMerchant());
                }
            });
        }

        @Override
        public int getItemCount() {
            return fixNumberList.size();
        }
    }
    void goToMerchantList(String id_merchant){
        Intent i = new Intent(this,TiketList.class);
        i.putExtra("id_merchant",id_merchant);
        startActivity(i);
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
    void setBack(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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
}