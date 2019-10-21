package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.LoginFilter;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class rateListActivity extends ListActivity implements Runnable {
    String data[]={"wait..."};
    Handler handler;
    private  String logDate="";
    private  final String DATE_SP_KEY="lastRateDateStr";
    TextView res;
    String TAG="main";

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        SharedPreferences sp=getSharedPreferences("myrate", Context.MODE_PRIVATE);
        logDate=sp.getString(DATE_SP_KEY,"");
        Log.i("List", "lastRateDateStr="+logDate);
        List<String> list1=new ArrayList<String>();
        for(int i=1;i<100;i++){
            list1.add("item"+i);
        }
        //开启子线程
        Thread t=new Thread(this);
        //等待开始运行的代码
        t.start();
        handler = new Handler(){
            @Override
            public  void handleMessage(Message msg){
                if(msg.what==5){
                    List<String> rateList = (List<String>)msg.obj;
                    ListAdapter adapter=new ArrayAdapter<String>(rateListActivity.this,android.R.layout.simple_list_item_1,rateList);
                    setListAdapter(adapter);


                }
            }
        };
    }
    public void run(){
        //获取网络数据，放入List带回到主线程中
        List<String> retList=new ArrayList<String>();
        String curDateStr = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
        Log.i("run","curDateStr:" + curDateStr + " logDate:" + logDate);

        if(curDateStr.equals(logDate)) {
            //如果相等，则不从网络中获取数据
            Log.i("run", "日期相等，从数据库中获取数据");
            RateManager manager=new RateManager(this);
            for(RateItem item:manager.listAll()){
                retList .add(item.getCurName()+"-->"+item.getCurRate());
            }
        }else{
            //从网络获取数据
            Log.i("run", "日期不相等，从网络中获取在线数据");
            try{
                String url="http://www.usd-cny.com/icbc.htm";
                Document doc= Jsoup.connect(url).get();
                Log.i("thread","run:"+doc.title());
                Elements tables=doc.getElementsByTag("table");

                Element table2=tables.get(1);
                //获取TD中的数据
                Elements tds=table2.getElementsByTag("td");
                List<RateItem> rateList=new ArrayList<RateItem>();
                for(int i=0;i<tds.size();i+=8){
                    Element td1=tds.get(i);
                    Element td2=tds.get(i+5);

                    String name=td1.text();
                    String value=td2.text();
                    retList.add(name+"==>"+value);
                    rateList.add(new RateItem(name,value));
                    Log.i("结果","run:  "+name + "==>"+value);
                }
                //把数据写入到数据库中
                RateManager manager=new RateManager(this);
                manager.addAll(rateList);

                //更新记录日期
                SharedPreferences sp = getSharedPreferences("myrate", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString(DATE_SP_KEY, curDateStr);
                edit.commit();
                Log.i("run","更新日期结束： " + curDateStr);
            }catch (IOException e){
            }
        }
        //获取message对象，用于传递给主线程
        Message msg=handler.obtainMessage(7);
        msg.obj=retList;
        handler.sendMessage(msg);
    }
}
