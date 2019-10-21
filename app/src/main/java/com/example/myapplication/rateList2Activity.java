package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class rateList2Activity extends ListActivity implements Runnable, AdapterView.OnItemClickListener {
    Handler handler;
    private static final String TAG="ITEMCLICK";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_rate_list2);

        //开启子线程
        Thread t=new Thread(this);
        //等待开始运行的代码
        t.start();

        handler = new Handler(){
            @Override
            public  void handleMessage(Message msg){
                if(msg.what==5){
                    ArrayList<HashMap<String,String>> listItems = ( ArrayList<HashMap<String,String>>)msg.obj;
                    /*ListAdapter adapter=new SimpleAdapter(rateList2Activity.this,listItems,R.layout.list_item,new String[]{"ItemName","ItemValue"},new int[]{R.id.ItemTitle, R.id.ItemDetail});
                    setListAdapter(adapter);*/
                    MyAdapter myAdapter = new MyAdapter(rateList2Activity.this,R.layout.list_item,listItems);
                    setListAdapter(myAdapter);
                    getListView().setOnItemClickListener(rateList2Activity.this);


                }
            }
        };
    }

    public void run(){


        List<HashMap<String,String>> list1=new ArrayList<HashMap<String,String>>();
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

                HashMap<String,String> map = new HashMap<String,String>();
                map.put("ItemName",name);
                map.put("ItemValue",value);

                list1.add(map);
                Log.i("结果","run:  "+name + "==>"+value);

            }
        }catch (IOException e){

        }

        //获取message对象，用于传递给主线程
        Message msg=handler.obtainMessage(5);
        msg.obj=list1;
        handler.sendMessage(msg);

    }

    public void onItemClick(AdapterView<?>parent, View view, int position, long id){
        Object itemAtPosition = getListView().getItemAtPosition(position);
        HashMap<String,String> map=(HashMap<String,String>)itemAtPosition;
        String titleStr = map.get("ItemName");
        String detailStr = map.get("ItemValue");
        Log.i(TAG,"onItemClick: titleStr="+titleStr);
        Log.i(TAG,"onItemClick: detailStr="+detailStr);

        TextView title=(TextView)view.findViewById(R.id.ItemTitle);
        TextView detail=(TextView)view.findViewById(R.id.ItemDetail);

        String title2 = String.valueOf(title.getText());
        String detail2 = String.valueOf(detail.getText());

        Log.i(TAG,"onItemClick: title2="+title2);
        Log.i(TAG,"onItemClick: detail2="+detail2);

        Intent calcu=new Intent(this,calcuRateActivity.class);
        calcu.putExtra("name_key",title2);
        calcu.putExtra("rate_key",detail2);
        startActivity(calcu);
    }

}

