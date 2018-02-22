package com.bangjeck.page;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bangjeck.R;
import com.bangjeck.setting.BangJeckSetting;
import com.bangjeck.stucture.ChatData;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Chat extends AppCompatActivity{

    private static final String TAG = "MainActivity";
    ChatAdapter chatAdapter;
    LinearLayoutManager mLayoutManager;
    List<ChatData> fixNumberList;
    RecyclerView recyclerView;
    EditText input;
    ImageView send;
    ImageView back;
    ImageView help;
    ImageView down;
    ProgressBar progress;

    int startList   = 0;
    int endList     = 20;
    String token    = "";
    String token2   = "";
    String nama     = "Pelanggan";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        input   = (EditText) findViewById(R.id.input);
        send    = (ImageView)findViewById(R.id.ok);
        down    = (ImageView)findViewById(R.id.down);
        back        = (ImageView) findViewById(R.id.back);
        help        = (ImageView) findViewById(R.id.help);

        progress    = (ProgressBar) findViewById(R.id.progress);

        if (getIntent().getExtras() != null) {
            for (String key : getIntent().getExtras().keySet()) {
                Object value = getIntent().getExtras().get(key);
                Log.d(TAG, "Key: " + key + " Value: " + value);
            }
        }

        token2    = FirebaseInstanceId.getInstance().getToken();
        if(token2.length()>0){
            updateToken(token2);
        }

        HistoryOnline historyOnline = new HistoryOnline(this);
        historyOnline.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"historyOnline");

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postToServer();
            }
        });
        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                down.setVisibility(View.GONE);
                mLayoutManager.scrollToPosition(fixNumberList.size()-1);
            }
        });

        setBack();
        setHelp();
        ulang();
    }
    private class HistoryOnline extends AsyncTask<String,Void,Integer> {

        List<ChatData> historyDatas;
        Context context;
        HistoryOnline(Context context){
            this.context        = context;
        }
        String test;
        protected Integer doInBackground(String...urls){
            int count = 0;
            historyDatas            = new ArrayList<>();
            try{
                String json             = request();
                test = json;
                JSONObject jsonObject   = new JSONObject(json);
                JSONArray jsonArray     = jsonObject.optJSONArray("json_chat");
                token                   = jsonObject.optString("to_user");
                nama                    = jsonObject.optString("nama");
                count                   = jsonArray.length();
                if(count>0){
                    historyDatas.clear();
                    for(int i = 0; i<count; i++) {
                        jsonObject      = jsonArray.getJSONObject(i);
                        String[] data   = new String[]{
                                jsonObject.optString("nama"),
                                jsonObject.optString("konten"),
                                jsonObject.optString("tanggal")
                        };
                        historyDatas.add(new ChatData(data));
                    }
                }
            }catch (Exception ex){
                test = ex.toString();
            }
            return count;
        }
        protected void onPostExecute(Integer result){
            if(result>0){
                initialization(historyDatas);
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
                        .add("kode", getIntent().getExtras().getString("kode"))
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(BangJeckSetting.base_url+"get_chat2.html")
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
    void updateToken(String token){
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
                    .add("token",token)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(BangJeckSetting.base_url+"update_token2.html")
                    .post(formBody)
                    .build();

            Response response = client.newCall(request).execute();
            String json = response.body().string();
            System.out.println("Update token : "+json);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    void initialization(List<ChatData> dataNumberList){
        fixNumberList   = dataNumberList;
        chatAdapter     = new ChatAdapter(this);
        recyclerView    = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager  = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(chatAdapter);

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
                            chatAdapter.notifyDataSetChanged();
                        }
                    }
                },1000);
            }
        });
        afterLoad = true;
        firstScroll = true;
        progress.setVisibility(View.GONE);
    }
    void ulang(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(afterLoad){
                    if(BangJeckSetting.status==1){
                        String[] data   = new String[]{
                                BangJeckSetting.dari,
                                BangJeckSetting.pesan,
                                BangJeckSetting.tanggal
                        };
                        fixNumberList.add(new ChatData(data));
                        chatAdapter.notifyDataSetChanged();
                        BangJeckSetting.status = 0;
                        down.setVisibility(View.VISIBLE);
                        mLayoutManager.scrollToPosition(fixNumberList.size()-1);
                    }
                    if(firstScroll||afterpost){
                        firstScroll = false;
                        afterpost = false;
                        mLayoutManager.scrollToPosition(fixNumberList.size()-1);
                    }
                }
                ulang();
            }
        }, 1000);
    }
    boolean afterLoad = false;
    boolean firstScroll = false;
    boolean afterpost = false;
    private class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

        Context context;

        class MyViewHolder extends RecyclerView.ViewHolder {

            TextView nama;
            TextView konten;
            TextView tanggal;
            TextView layer;

            MyViewHolder(View view) {
                super(view);
                nama    = view.findViewById(R.id.nama);
                konten  = view.findViewById(R.id.konten);
                tanggal = view.findViewById(R.id.tanggal);
                layer   = view.findViewById(R.id.layer);
            }
        }

        ChatAdapter(Context context){
            this.context = context;
        }

        @Override
        public ChatAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_adapter, parent, false);
            return new ChatAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ChatAdapter.MyViewHolder holder, int position) {
            ChatData chatData = fixNumberList.get(position);

            holder.nama.setText(chatData.getNama());
            holder.konten.setText(chatData.getKonten());
            holder.tanggal.setText(chatData.getTanggal());

            if (chatData.getNama().equals(nama)) {
                holder.layer.setVisibility(View.VISIBLE);
            }else{
                holder.layer.setVisibility(View.GONE);
            }
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
    void postToServer(){
        if(afterLoad) {
            DateFormat df = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
            String tanggal = df.format(Calendar.getInstance().getTime());

            String[] data = new String[]{
                    nama,
                    input.getText().toString(),
                    tanggal
            };
            fixNumberList.add(new ChatData(data));
        }
        PostServer postServer = new PostServer(this);
        postServer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"PostServer");
    }
    private class PostServer extends AsyncTask<String,Void,String> {

        Context context;
        PostServer(Context context){
            this.context        = context;
        }

        protected String doInBackground(String...urls){
            String json = "";
            try{
                json = request();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return json;
        }
        protected void onPostExecute(String result){
            PostToBangJeck postToBangJeck = new PostToBangJeck(context,input.getText().toString());
            postToBangJeck.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"postToBangJeck");
            afterpost = true;
            input.setText("");
            input.requestFocus();
            progress.setVisibility(View.GONE);
        }
        String request(){
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            String json = "";
            try{
                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                httpClient.addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        Request.Builder requestBuilder = original.newBuilder()
                                .addHeader("Authorization","key=AAAA5kWPQ7A:APA91bETHdgZhSlkVdQvX6usCmXnFoqWkXrtzsHcHDf92tU0cP8NDrgFRdrb_2p1IdsjUdg6txY9YpGUzNgqmOjtc8_4-8wOFdi5kheNwCOiPKQ9y7xF1CF1OnG3loSCGNZei_mmmuk0JnW3C2kxE_m9CeyXiTlRNg")
                                .addHeader("Content-Type","application/json");

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                });
                OkHttpClient client = httpClient.build();

                MediaType JSON  = MediaType.parse("application/json; charset=utf-8");

                String inputan      = input.getText().toString();
                DateFormat df = new SimpleDateFormat("yyyy-MM-d HH:mm:ss");
                String tanggal = df.format(Calendar.getInstance().getTime());

                json    = "{\"to\":\""+token+"\",\"notification\":{\"body\":\""+tanggal+"=>"+nama+"=>"+inputan+"\"},\"priority\":\"high\"}";
                RequestBody body = RequestBody.create(JSON, json);

                Request request = new Request.Builder()
                        .url("https://fcm.googleapis.com/fcm/send")
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                json = response.body().string();
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return json;
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
        i.putExtra("url",BangJeckSetting.base_url+"help_chat.html");
        startActivity(i);
    }
    private class PostToBangJeck extends AsyncTask<String,Void,Integer> {

        List<ChatData> historyDatas;
        Context context;
        String isi_chat = "";

        PostToBangJeck(Context context,String isi_chat){
            this.context        = context;
            this.isi_chat       = isi_chat;
        }

        protected Integer doInBackground(String...urls){
            request();
            return 0;
        }
        protected void onPostExecute(Integer result){
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
                        .add("kode", getIntent().getExtras().getString("kode"))
                        .add("penulis","1")
                        .add("isi_chat",isi_chat)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(BangJeckSetting.base_url+"tambah_chat2.html")
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