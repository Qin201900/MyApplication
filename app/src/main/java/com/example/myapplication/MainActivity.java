package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.lang.annotation.Documented;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Runnable {
    TextView res;
    String TAG="main";
    EditText rmb;
    Button btnd,btne,btnw,btnconfig;
    float dollar_rate=0.3f;
    float euro_rate=0.4f;
    float won_rate=0.5f;
    Handler handler;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);

        dollar_rate = sharedPreferences.getFloat("dollar_rate",0.0f);
        euro_rate = sharedPreferences.getFloat("euro_rate",0.0f);
        won_rate = sharedPreferences.getFloat("won_rate",0.0f);

        rmb=findViewById(R.id.editText);
        res=findViewById(R.id.textView);
        btnd=findViewById(R.id.button);
        btne=findViewById(R.id.button2);
        btnw=findViewById(R.id.button3);
        btnconfig=findViewById(R.id.button4);
        //开启子线程
        Thread t=new Thread(this);
        //等待开始运行的代码
        t.start();

        handler = new Handler(){
            @Override
            public  void handleMessage(Message msg){
                if(msg.what==5){
                    float[] rates = (float[])msg.obj;

                    dollar_rate=rates[0];
                    euro_rate=rates[1];
                    won_rate=rates[2];
                    Log.i(TAG,"handleMessage:getMessage msg="+rates[0]);
                    //res.setText(str);

                }
            }
        };
    }
    public void doll(View view){
        String str=rmb.getText().toString();
        float a=Float.parseFloat(str);
        res.setText(a/dollar_rate+"");
    }
    public void eur(View view){
        String str=rmb.getText().toString();
        float a=Float.parseFloat(str);
        res.setText(a/euro_rate+"");
    }
    public void won(View view){
        String str=rmb.getText().toString();
        float a=Float.parseFloat(str);
        res.setText(a/won_rate+"");
    }

    @Override
    public void onClick(View view) {

        Intent config=new Intent(this,ConfigActivity.class);
        config.putExtra("dollar_rate_key",dollar_rate);
        config.putExtra("euro_rate_key",euro_rate);
        config.putExtra("won_rate_key",won_rate);

        //startActivity(config);
        startActivityForResult(config,1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==1 && resultCode==2){
            Bundle bundle = data.getExtras();
            dollar_rate=bundle.getFloat("key_dollar",0.1f);
            euro_rate=bundle.getFloat("key_euro",0.1f);
            won_rate=bundle.getFloat("key_won",0.1f);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void run(){
        /*for(int i=1;i<6;i++){
            Log.i(TAG,"run:i="+i);
            try{
                Thread.sleep(2000);

            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }*/
        float dol=0f,eur=0f,won=0f;

        try{
            String url="http://www.usd-cny.com/";
            Document doc= Jsoup.connect(url).get();
            Log.i("thread","run:"+doc.title());
            Elements tables=doc.getElementsByTag("table");
            Element table=tables.get(0);
            Elements tds=table.getElementsByTag("td");
            for(int i=0;i<tds.size();i+=5){
                Element td1=tds.get(i);
                Element td2=tds.get(i+1);

                String name=td1.text();
                String value=td2.text();


                Log.i("结果","run:  "+name + "==>"+value);


                if(name.equals("美元")){
                    dol=Float.parseFloat(value);
                    Log.i("得到的美元汇率","run---"+dol);
                }
                if(name.equals("欧元")){
                    eur=Float.parseFloat(value);
                }
                if(name.equals("韩币")){
                    won=Float.parseFloat(value);
                }
            }
        }catch (IOException e){

        }

        float rate[] = new float[3];
        rate[0]=dol;
        rate[1]=eur;
        rate[2]=won;

        //获取message对象，用于传递给主线程
        Message msg=handler.obtainMessage(5);
        msg.obj=rate;
        handler.sendMessage(msg);

    }

}
