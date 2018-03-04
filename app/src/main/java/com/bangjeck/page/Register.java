package com.bangjeck.page;

import android.content.Context;
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

public class Register extends BangJeckSetting {

    Button edit;
    Button signout;
    EditText name;
    EditText phone;
    EditText email;
    EditText password;
    EditText repassword;
    FrameLayout loadpage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        edit        = findViewById(R.id.edit);
        signout     = findViewById(R.id.signout);
        name        = findViewById(R.id.name);
        phone       = findViewById(R.id.phone);
        email       = findViewById(R.id.email);
        password    = findViewById(R.id.password);
        repassword  = findViewById(R.id.repassword);
        loadpage    = findViewById(R.id.loadpage);

        edit.setText("Daftar");

        signout.setVisibility(View.INVISIBLE);
        setRegister();
    }
    void setRegister(){
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegister();
            }
        });
    }
    void goToRegister(){
        String nama     = name.getText().toString();
        String notelp   = phone.getText().toString();
        String surel    = email.getText().toString();
        String pass     = password.getText().toString();
        String pass2    = repassword.getText().toString();

        if(nama.length()<=0){
            Toast.makeText(this,"Nama tidak boleh kosong.",Toast.LENGTH_LONG).show();
            name.requestFocus();
        }else{
            if(notelp.length()<11){
                Toast.makeText(this,"Nomor telfon tidak valid.",Toast.LENGTH_LONG).show();
                phone.requestFocus();
            }else{
                if(surel.length()<5){
                    Toast.makeText(this,"Email tidak valid.",Toast.LENGTH_LONG).show();
                    email.requestFocus();
                }else{
                    if(pass.length()<6){
                        Toast.makeText(this,"Password minimal 6 karakter.",Toast.LENGTH_LONG).show();
                        password.requestFocus();
                    }else{
                        if(!pass.equals(pass2)){
                            Toast.makeText(this,"Password dan Ulangi Password tidak sama.",Toast.LENGTH_LONG).show();
                            repassword.requestFocus();
                        }else{
                            loadpage.setVisibility(View.VISIBLE);
                            InsertUser insertUser = new InsertUser(this);
                            insertUser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"inserUser");
                        }
                    }
                }
            }
        }
    }
    private class InsertUser extends AsyncTask<String,Void,String> {
        Context context;
        boolean have_session = false;

        InsertUser(Context context) {
            this.context    = context;
        }
        protected String doInBackground(String...urls){
            String hasil = "";
            try{
                String result   = request();
                JSONObject jsonObject = new JSONObject(result);
                hasil = jsonObject.getString("found");
            }catch (Exception ex){
                ex.printStackTrace();
            }
            return hasil;
        }
        protected void onPostExecute(String result){
            loadpage.setVisibility(View.GONE);
            if(result.equals("1")){
                Toast.makeText(context,"Berhasil terdaftar.",Toast.LENGTH_LONG).show();
                finish();
            }else if(result.equals("2")){
                Toast.makeText(context,"Terjadi kesalahan.",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(context,"Nomor sudah terdaftar.",Toast.LENGTH_LONG).show();
            }
        }
        String request(){
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
            }

            String json = "";
            try{
                String nama     = name.getText().toString();
                String notelp   = phone.getText().toString();
                String surel    = email.getText().toString();
                String pass     = password.getText().toString();

                RequestBody formBody = new FormBody.Builder()
                        .add("nama", nama)
                        .add("notelp", notelp)
                        .add("email", surel)
                        .add("password", pass)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(base_url+"process_insert_user.html")
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();
                json = response.body().string();
            }catch (Exception ex){
                System.out.println(ex.toString());
            }
            return json;
        }
    }
}