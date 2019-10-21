package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class ConfigActivity extends AppCompatActivity  implements View.OnClickListener {
    EditText e1,e2,e3;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Intent intent=getIntent();

        float dollar2=intent.getFloatExtra("dollar_rate_key",0.0f);
        float euro2=intent.getFloatExtra("euro_rate_key",0.0f);
        float won2=intent.getFloatExtra("won_rate_key",0.0f);

        e1=findViewById(R.id.editText2);
        e2=findViewById(R.id.editText3);
        e3=findViewById(R.id.editText4);
        btn=findViewById(R.id.button5);

        e1.setText(dollar2+"");
        e2.setText(euro2+"");
        e3.setText(won2+"");

        btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        float newdollar=Float.parseFloat(e1.getText().toString());
        float neweuro=Float.parseFloat(e2.getText().toString());
        float newwon=Float.parseFloat(e3.getText().toString());

        SharedPreferences sp=getSharedPreferences("myrate", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        editor.putFloat("dollar_rate",newdollar);
        editor.putFloat("euro_rate",neweuro);
        editor.putFloat("won_rate",newwon);
        editor.apply();




        //保存到Buddle
        Intent intent = new Intent();
        Bundle bdl=new Bundle();
        bdl.putFloat("key_dollar",newdollar);
        bdl.putFloat("key_euro",neweuro);
        bdl.putFloat("key_won",newwon);
        intent.putExtras(bdl);
        setResult(2,intent);

        //返回调用页面
        finish();

    }
}
