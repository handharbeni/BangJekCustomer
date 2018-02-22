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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bangjeck.R;
import com.bangjeck.setting.BangJeckSetting;

import org.json.JSONObject;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Profile extends BangJeckSetting {

    Button edit;
    Button signout;
    EditText name;
    EditText phone;
    EditText email;
    EditText password;
    EditText repassword;
    FrameLayout loadpage;
    ImageView back;
    ImageView help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        name        = (EditText) findViewById(R.id.name);
        phone       = (EditText) findViewById(R.id.phone);
        email       = (EditText) findViewById(R.id.email);
        password    = (EditText) findViewById(R.id.password);
        repassword  = (EditText) findViewById(R.id.repassword);
        loadpage    = (FrameLayout) findViewById(R.id.loadpage);
        back        = (ImageView) findViewById(R.id.back);
        help        = (ImageView) findViewById(R.id.help);

        email   = (EditText) findViewById(R.id.email);
        signout = (Button) findViewById(R.id.signout);
        edit    = (Button) findViewById(R.id.edit);

        SharedPreferences preferences = getSharedPreferences("com.bangjeck", Context.MODE_PRIVATE);
        String session = preferences.getString("session","");

        try{
            if(!session.equals("")){
                JSONObject jsonObject = new JSONObject(session);
                name.setText(jsonObject.getString("nama"));
                phone.setText(jsonObject.getString("notelp"));
                email.setText(jsonObject.getString("email"));
            }
        }catch (Exception ex){
            System.out.println(ex.toString());
        }

        setSignOut();
        setEdit();
        setBack();
        setHelp();

        if(session.equals("")){
            Intent i = new Intent(this,SignIn.class);
            startActivity(i);
            finish();
        }
    }
    void setEdit(){
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToUpdate();
            }
        });
    }
    void setSignOut(){
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertSignOut();
            }
        });
    }
    void alertSignOut(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Apakah Anda ingin mengganti akun?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences preferences = getSharedPreferences("com.bangjeck", Context.MODE_PRIVATE);
                preferences.edit().putString("session","").apply();
                preferences.edit().putString("old_phone","").apply();
                preferences.edit().putString("old_password","").apply();
                goToSignIn();
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
    void goToSignIn(){
        Intent i = new Intent(this,SignIn.class);
        startActivity(i);
        finish();
    }
    void goToUpdate(){
        if(!password.getText().toString().equals(repassword.getText().toString())){
            Toast.makeText(this,"Password dan Ulangi Password tidak sama.",Toast.LENGTH_LONG).show();
        }else{
            loadpage.setVisibility(View.VISIBLE);
            UpdateProfile updateProfile = new UpdateProfile(this);
            updateProfile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"update_profile");
        }
    }
    private class UpdateProfile extends AsyncTask<String,Void,Integer> {
        Context context;
        boolean have_session = false;

        UpdateProfile(Context context) {
            this.context    = context;
        }
        protected Integer doInBackground(String...urls){
            try{
                String result   = request();
                JSONObject jsonObject = new JSONObject(result);
                if(jsonObject.getString("found").equals("1")){
                    have_session    = true;
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
                preferences.edit().putString("session","").apply();
                preferences.edit().putString("old_phone","").apply();
                preferences.edit().putString("old_password","").apply();
                Toast.makeText(context,"Berhasil dirubah.",Toast.LENGTH_LONG).show();
                goToSignIn();
            }else{
                Toast.makeText(context,"Terjadi kesalahan.",Toast.LENGTH_LONG).show();
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
                String nama     = name.getText().toString();
                String notelp   = phone.getText().toString();
                String surel    = email.getText().toString();
                String pass_new = password.getText().toString();

                RequestBody formBody = new FormBody.Builder()
                        .add("user", user)
                        .add("password", pass)
                        .add("nama", nama)
                        .add("notelp", notelp)
                        .add("email", surel)
                        .add("password_new", pass_new)
                        .build();

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(base_url+"process_update_user.html")
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
        i.putExtra("url",base_url+"help_profile.html");
        startActivity(i);
    }
}