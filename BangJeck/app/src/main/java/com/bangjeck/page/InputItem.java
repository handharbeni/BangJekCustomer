package com.bangjeck.page;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bangjeck.R;
import com.bangjeck.setting.BangJeckSetting;
import com.bangjeck.stucture.ItemData;

import java.util.ArrayList;
import java.util.List;

public class InputItem extends BangJeckSetting {

    int count = 0;
    int startList   = 0;
    int endList     = 20;

    Button submit;
    FrameLayout loadpage;
    ImageView add;
    ImageView back;
    ImageView help;
    ItemAdapter itemAdapter;
    LinearLayoutManager mLayoutManager;
    List<ItemData> fixNumberList;
    RecyclerView recyclerView;

    TextView mulai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_item);

        add     = (ImageView)findViewById(R.id.add);
        back    = (ImageView) findViewById(R.id.back);
        help    = (ImageView) findViewById(R.id.help);
        submit  = (Button) findViewById(R.id.submit);
        mulai   = (TextView) findViewById(R.id.mulai);
        mulai.setVisibility(View.VISIBLE);

        getItem();
        getAdd();
        getBack();
        getHelp();
        getSubmit();
    }
    void getAdd(){
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertAdd();
            }
        });
    }
    void alertAdd(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.add_item, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditText editText = dialogView.findViewById(R.id.item_name);
                fixNumberList.add(new ItemData(editText.getText().toString()));
                itemAdapter.notifyDataSetChanged();
                if(fixNumberList.size()>0){
                    mulai.setVisibility(View.GONE);
                }
            }
        });
        dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }
    void getItem(){
        List<ItemData> itemDatas = new ArrayList<>();
        itemDatas.clear();
        if(count>0) {
            for (int i = 0; i < count; i++) {
                String data = "fsa";
                itemDatas.add(new ItemData(data));
            }
        }
        initialization(itemDatas);
    }
    void initialization(List<ItemData> dataNumberList){
        fixNumberList   = dataNumberList;
        itemAdapter  = new ItemAdapter(this);
        recyclerView    = (RecyclerView)findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager  = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(itemAdapter);

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
                            itemAdapter.notifyDataSetChanged();
                        }
                    }
                },1000);
            }
        });
    }
    private class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

        Context context;

        class MyViewHolder extends RecyclerView.ViewHolder {

            ImageView cancel;
            TextView item_name;

            MyViewHolder(View view) {
                super(view);
                cancel      = view.findViewById(R.id.cancel);
                item_name   = view.findViewById(R.id.item_name);
            }
        }

        ItemAdapter(Context context){
            this.context = context;
        }

        @Override
        public ItemAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter, parent, false);
            return new ItemAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ItemAdapter.MyViewHolder holder, int position) {
            final ItemData itemData = fixNumberList.get(position);
            holder.item_name.setText(itemData.getDescription());
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goToCancel(holder.getAdapterPosition(),itemData.getDescription());
                }
            });
        }

        @Override
        public int getItemCount() {
            return fixNumberList.size();
        }
    }
    void goToCancel(final int position, String item_name){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Membatalkan item "+item_name+"?");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fixNumberList.remove(position);
                itemAdapter.notifyDataSetChanged();
                if(fixNumberList.size()<=0){
                    mulai.setVisibility(View.VISIBLE);
                }
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
                .setMessage("Yakin ingin kembali ke halaman peta? Item yang telah diinputkan akan hilang.")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        finish();
                    }
                }).create().show();
    }
    void getBack(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBack();
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
        i.putExtra("url",base_url+"help_item.html");
        startActivity(i);
    }
    void getSubmit(){
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if(fixNumberList.size()>0){
                        String jenis    = "";
                        try{
                            jenis   = getIntent().getExtras().getString("jenis");
                        }catch (Exception ex){
                            System.out.println(ex.toString());
                        }

                        if(jenis.length()>0){
                            if(jenis.equals("Kurir Kargo")){
                                goToDetailPesan(0);
                            }else{
                                goToSubmit();
                            }
                        }else{
                            goToSubmit();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"Anda belum memasukkan item.",Toast.LENGTH_LONG).show();
                    }
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(),"Anda belum memasukkan item.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    void goToSubmit(){
        CharSequence colors[] = new CharSequence[] {"<= 50.000", "<= 100.000", "<= 150.000", "<= 200.000","> 200.000"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Estimasi Harga");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToDetailPesan(which);
            }
        });
        builder.show();
    }
    void goToDetailPesan(int estimasi){
        try{
            Intent i = new Intent(getApplication().getApplicationContext(),Pemesanan.class);
            i.putExtra("jenis",getIntent().getExtras().getString("jenis"));
            i.putExtra("lokasi_ambil",getIntent().getExtras().getString("lokasi_ambil"));
            i.putExtra("keterangan_ambil",getIntent().getExtras().getString("keterangan_ambil"));
            i.putExtra("lokasi_kirim",getIntent().getExtras().getString("lokasi_kirim"));
            i.putExtra("keterangan_kirim",getIntent().getExtras().getString("keterangan_kirim"));
            i.putExtra("jarak",getIntent().getExtras().getString("jarak"));
            i.putExtra("biaya_jarak",getIntent().getExtras().getString("biaya_jarak"));

            String item = "";
            if(fixNumberList.size()>0){
                for (int j = 0; j < fixNumberList.size(); j++) {
                    item += fixNumberList.get(j).getDescription()+"\n";
                }
            }

            int biaya_item  = 50000+((estimasi+1)-1)*50000;
            String biaya    = Integer.toString(biaya_item);

            if(getIntent().getExtras().getString("jenis").equals("Kurir Kargo")){
                biaya   = "0";
            }

            i.putExtra("item",item);
            i.putExtra("biaya_item",biaya);
            i.putExtra("biaya_dua_kurir","0");
            i.putExtra("total",getIntent().getExtras().getString("total"));

            i.putExtra("lat",getIntent().getExtras().getString("lat"));
            i.putExtra("lon",getIntent().getExtras().getString("lon"));
            startActivity(i);
        }catch (Exception ex){
            Toast.makeText(this, "Terjadi Kesalahan.", Toast.LENGTH_LONG).show();
        }
    }
}