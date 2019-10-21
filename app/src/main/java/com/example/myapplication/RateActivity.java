package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;

public class RateActivity extends AppCompatActivity implements Runnable{
    private final String TAG="rate";
    private float dollarRate=0.0f;
    private float euroRate=0.0f;
    private float wonRate=0.0f;
    Handler handler=new Handler();
    EditText rmb;
    TextView show;
    Message mes;
    String newTime = "";
    String newDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rmb=(EditText)findViewById(R.id.editText);
        show=(TextView)findViewById(R.id.result);

        //获取sp里的数据
        SharedPreferences sharedPreferences=getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        dollarRate=sharedPreferences.getFloat("dollar",0.0f);
        euroRate=sharedPreferences.getFloat("euro",0.0f);
        wonRate=sharedPreferences.getFloat("won",0.0f);

        Log.i(TAG,"onCreate:sp dollarRate="+dollarRate);
        Log.i(TAG,"onCreate:sp euroRate="+euroRate);
        Log.i(TAG,"onCreate:sp wonRate="+wonRate);

        //开启子线程
        Thread t= new Thread(this);
        t.start();


        handler=new Handler() {
            @Override
            public void handleMessage(Message msg){
                if(mes.what==5){
                    String str=(String)mes.obj;
                    Log.i(TAG,"handleMessage mes="+str);
                    show.setText(str);
                }
                super.handleMessage(msg);
            }

        };//对类方法的重写
    }
    public void onClick(View btn){
        Log.i(TAG,"onClick:");
        String str=rmb.getText().toString();
        Log.i(TAG,"onClick:get str="+str);

        float r=0;
        if(str.length()>0){
            r=Float.parseFloat(str);
        }
        else {
            //用户未输入
            Toast.makeText(this,"请输入内容",Toast.LENGTH_SHORT).show();
        }
        Log.i(TAG,"onClick:r="+r);

        //计算
        if(btn.getId()==R.id.editText2){
            show.setText(String.format("%.2f",r*dollarRate));
        }
        else if(btn.getId()==R.id.editText3){
            show.setText(String.format("%.2f",r*euroRate));
        }
        else {
            show.setText(String.format("%.2f",r*wonRate));
        }
    }
    public void openOne(View btn){
        openConfig();
    }
    public void openConfig(){
        Intent config=new Intent(this,ConfigActivity.class);
        config.putExtra("dollarRate",dollarRate);
        config.putExtra("euroRate",euroRate);
        config.putExtra("wonRate",wonRate);
        Log.i(TAG,"openOne:dollar="+dollarRate);
        Log.i(TAG,"openOne:euro="+euroRate);
        Log.i(TAG,"openOne:won="+wonRate);

        startActivityForResult(config,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && requestCode == 2) {
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("key_dollar", 0.1f);
            euroRate = bundle.getFloat("key_euro", 0.1f);
            wonRate = bundle.getFloat("key_won", 0.1f);
            Log.i(TAG, "onActivityResult: dollarRate=" + dollarRate);
            Log.i(TAG, "onActivityResult: euroRate=" + euroRate);
            Log.i(TAG, "onActivityResult: wonRate=" + wonRate);

            //将新设置的汇率写到SP里
            SharedPreferences sharedPreferences = getSharedPreferences("myrate", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putFloat("dollar_rate", dollarRate);
            editor.putFloat("euro_rate", euroRate);
            editor.putFloat("won_rate", wonRate);
            editor.commit();
            Log.i(TAG, "onActivityResult: 数据已保存到sharedPreferences");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void run(){
        Log.i(TAG,"run:running...");
        for(int i=1;i<6;i++){
            Log.i(TAG,"run:running..."+i);
            try{
                Thread.sleep(2000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        //获取MESSAGE对象，用于返回主线程
        Message mes=handler.obtainMessage(5);
        //what用于标记当前Message的属性
        mes.obj="Hello from run()";
        handler.sendMessage(mes);

        //获取网络数据
        URL url=null;
        //用于保存获取的汇率
        Bundle bundle = new Bundle();
        /*try{
            url=new URL("http://www.usd-cny.com/icbc.htm");
            HttpURLConnection http=(HttpURLConnection) url.openConnection();
            InputStream in=http.getInputStream();
            String html=inputStream2String (in);
            Log.i(TAG,"run:html"+html);
        } catch(MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace ( );
        }*/
        Document doc= null;
        try {
            doc = Jsoup.connect("http://www.usd-cny.com/icbc.htm").get();
            //doc=Jsoup.parse (html);
            Log.i(TAG,"run:"+doc.title());
            Elements tables=doc.getElementsByTag ("table");
            Element table6=tables.get(5);
            Elements tds=table6.getElementsByTag ("td");
            for(int i=0;i<tds.size();i+=8){
                Element td1=tds.get(i);
                Element td2=tds.get(i+5);
                Log.i(TAG,"run:"+td1.text()+"==>"+td2.text());
                String str1=td1.text();
                String val=td2.text();
                if("美元".equals(str1)){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }else if("欧元".equals(str1)){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                } else if("韩国元".equals(str1)){
                    bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
                }
            }

        } catch (IOException e) {
            e.printStackTrace ( );
        }
        //bundle中保存所获取的汇率
        mes=handler.obtainMessage(5);
        //what用于标记当前Message的属性
        mes.obj=bundle;
        handler.sendMessage(mes);

    }
    private String inputStream2String(InputStream inputStream) throws IOException {//输入流转String
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "utf-8");
        for (; ; ) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.rate,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_set) {
            openConfig();
        }else if(item.getItemId()==R.id.open_list){
            //打开列表窗口
            Intent list=new Intent(this,RateActivity.class);
            startActivity(list);
            //测试数据库
//            RateItem item1=new RateItem("aaaa","123");
//            RateManager manager =new RateManager(this);
//            manager.add(item1);
//            manager.add(new RateItem("bbbb","23.5"));
//            Log.i(TAG, "onOptionsItemSelected: 写入数据完毕");
//
//            //查询所有数据
//            List<RateItem> testList=manager.listAll();
//            for(RateItem i:testList){
//                Log.i(TAG, "onOptionsItemSelected: 取出数据[id="+i.getId()+"]Name="+i.getCurName()+"Rate="+i.getCurRate());
//            }

        }
        return super.onOptionsItemSelected(item);
    }
}

