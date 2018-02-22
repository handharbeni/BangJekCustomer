package com.bangjeck.page;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bangjeck.R;
import com.bangjeck.setting.BangJeckSetting;

public class PermissionPage extends BangJeckSetting{

    ImageView help;
    TextView value_internet;
    TextView value_storage;
    TextView value_location;
    TextView value_network;
    TextView value_call;
    TextView value_phoneinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permissionpage);

        help            = (ImageView)findViewById(R.id.help);
        value_internet  = (TextView)findViewById(R.id.value_internet);
        value_storage   = (TextView)findViewById(R.id.value_storage);
        value_location  = (TextView)findViewById(R.id.value_location);
        value_network   = (TextView)findViewById(R.id.value_network);
        value_call      = (TextView)findViewById(R.id.value_call);
        value_phoneinfo = (TextView)findViewById(R.id.value_phoneinfo);

        checkPermission();
        getPermission();
        getHelp();
    }
    void checkPermission(){
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                value_internet.setText(getResources().getString(R.string.value_disable));
                value_internet.setTextColor(getResources().getColor(R.color.colorDisable));
            }else{
                value_internet.setText(getResources().getString(R.string.value_enable));
                value_internet.setTextColor(getResources().getColor(R.color.colorEnable));
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                value_storage.setText(getResources().getString(R.string.value_disable));
                value_storage.setTextColor(getResources().getColor(R.color.colorDisable));
            }else{
                value_storage.setText(getResources().getString(R.string.value_enable));
                value_storage.setTextColor(getResources().getColor(R.color.colorEnable));
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                value_location.setText(getResources().getString(R.string.value_disable));
                value_location.setTextColor(getResources().getColor(R.color.colorDisable));
            }else{
                value_location.setText(getResources().getString(R.string.value_enable));
                value_location.setTextColor(getResources().getColor(R.color.colorEnable));
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                value_network.setText(getResources().getString(R.string.value_disable));
                value_network.setTextColor(getResources().getColor(R.color.colorDisable));
            }else{
                value_network.setText(getResources().getString(R.string.value_enable));
                value_network.setTextColor(getResources().getColor(R.color.colorEnable));
            }
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                value_call.setText(getResources().getString(R.string.value_disable));
                value_call.setTextColor(getResources().getColor(R.color.colorDisable));
            }else{
                value_call.setText(getResources().getString(R.string.value_enable));
                value_call.setTextColor(getResources().getColor(R.color.colorEnable));
            }
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                value_phoneinfo.setText(getResources().getString(R.string.value_disable));
                value_phoneinfo.setTextColor(getResources().getColor(R.color.colorDisable));
            }else{
                value_phoneinfo.setText(getResources().getString(R.string.value_enable));
                value_phoneinfo.setTextColor(getResources().getColor(R.color.colorEnable));
            }
            checkAll();
        }else{
            goToMainMenu();
        }
    }
    void getPermission(){
        value_internet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.INTERNET}, 1);
                    }
                }
            }
        });
        value_storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                    }
                }
            }
        });
        value_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
                    }
                }
            }
        });
        value_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_NETWORK_STATE}, 4);
                    }
                }
            }
        });
        value_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 5);
                    }
                }
            }
        });
        value_phoneinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, 6);
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    value_internet.setText(getResources().getString(R.string.value_enable));
                    value_internet.setTextColor(getResources().getColor(R.color.colorEnable));
                }else{
                    value_internet.setText(getResources().getString(R.string.value_disable));
                    value_internet.setTextColor(getResources().getColor(R.color.colorDisable));
                }
                break;
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    value_storage.setText(getResources().getString(R.string.value_enable));
                    value_storage.setTextColor(getResources().getColor(R.color.colorEnable));
                } else {
                    value_storage.setText(getResources().getString(R.string.value_disable));
                    value_storage.setTextColor(getResources().getColor(R.color.colorDisable));
                }
                break;
            case 3:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    value_location.setText(getResources().getString(R.string.value_enable));
                    value_location.setTextColor(getResources().getColor(R.color.colorEnable));
                } else {
                    value_location.setText(getResources().getString(R.string.value_disable));
                    value_location.setTextColor(getResources().getColor(R.color.colorDisable));
                }
                break;
            case 4:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    value_network.setText(getResources().getString(R.string.value_enable));
                    value_network.setTextColor(getResources().getColor(R.color.colorEnable));
                } else {
                    value_network.setText(getResources().getString(R.string.value_disable));
                    value_network.setTextColor(getResources().getColor(R.color.colorDisable));
                }
                break;
            case 5:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    value_call.setText(getResources().getString(R.string.value_enable));
                    value_call.setTextColor(getResources().getColor(R.color.colorEnable));
                } else {
                    value_call.setText(getResources().getString(R.string.value_disable));
                    value_call.setTextColor(getResources().getColor(R.color.colorDisable));
                }
                break;
            case 6:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    value_phoneinfo.setText(getResources().getString(R.string.value_enable));
                    value_phoneinfo.setTextColor(getResources().getColor(R.color.colorEnable));
                } else {
                    value_phoneinfo.setText(getResources().getString(R.string.value_disable));
                    value_phoneinfo.setTextColor(getResources().getColor(R.color.colorDisable));
                }
                break;
        }
        checkAll();
    }
    void checkAll(){
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            int all_premission  = 0;
            if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                all_premission++;
            }
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                all_premission++;
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                all_premission++;
            }
            if (checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED) {
                all_premission++;
            }
            if (checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                all_premission++;
            }
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                all_premission++;
            }

            if(all_premission==6){
                goToMainMenu();
            }
        }
    }
    void goToMainMenu(){
        Intent i = new Intent(this,MainMenu.class);
        startActivity(i);
        finish();
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
        i.putExtra("url",base_url+"help_permission.html");
        startActivity(i);
    }
}
