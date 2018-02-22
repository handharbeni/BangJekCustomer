package com.bangjeck.page;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bangjeck.R;
import com.bangjeck.setting.BangJeckSetting;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SignIn extends BangJeckSetting {

    Button register;
    Button signin;
    EditText password;
    EditText phone;
    FrameLayout loadpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);

        password    = (EditText)findViewById(R.id.password);
        phone       = (EditText)findViewById(R.id.phone);
        signin      = (Button)findViewById(R.id.signin);
        register    = (Button)findViewById(R.id.register);
        loadpage    = (FrameLayout)findViewById(R.id.loadpage);

        getSignIn();
        getregister();
    }
    void getSignIn(){
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCheckSignIn();
            }
        });
    }
    void getregister(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToregister();
            }
        });
    }
    void goToCheckSignIn(){
        loadpage.setVisibility(View.VISIBLE);
        CheckSignIn checkSignIn = new CheckSignIn(this);
        checkSignIn.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"SignIn");
    }
    private class CheckSignIn extends AsyncTask<String,Void,Integer> {
        Context context;
        boolean have_session = false;
        String data = "";

        CheckSignIn(Context context) {
            this.context    = context;
        }
        protected Integer doInBackground(String...urls){
            try{
                String result   = request();
                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject.getString("found").equals("1")){
                    have_session    = true;
                    data            = jsonObject.getString("data");
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return 0;
        }
        protected void onPostExecute(Integer result){
            loadpage.setVisibility(View.GONE);
            if(have_session){
                SharedPreferences preferences = getSharedPreferences("com.bangjeck", Context.MODE_PRIVATE);
                preferences.edit().putString("session",data).apply();
                preferences.edit().putString("old_phone",phone.getText().toString()).apply();
                preferences.edit().putString("old_password",password.getText().toString()).apply();
                Toast.makeText(context,"Selamat datang.",Toast.LENGTH_LONG).show();
                finish();
            }else{
                Toast.makeText(context,"User atau Password salah.",Toast.LENGTH_LONG).show();
            }
        }
        String request(){
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            String json = "";
            try{
                String user = phone.getText().toString();
                String pass = password.getText().toString();

                RequestBody formBody = new FormBody.Builder()
                        .add("user", user)
                        .add("password", pass)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(base_url+"process_user_login.html")
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
    void goToregister(){
        Intent i = new Intent(this,Register.class);
        startActivity(i);
    }
}