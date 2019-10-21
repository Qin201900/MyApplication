package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {
    TextView scorea,scoreb;
    Button add1a,add2a,add3a,add1b,add2b,add3b,submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        scorea=findViewById(R.id.scoreA);
        scoreb=findViewById(R.id.scoreB);
        add1a=findViewById(R.id.add1A);
        add2a=findViewById(R.id.add2A);
        add3a=findViewById(R.id.add3A);
        add1b=findViewById(R.id.add1B);
        add2b=findViewById(R.id.add2B);
        add3b=findViewById(R.id.add3B);
        submit=findViewById(R.id.reset);

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("score111","save");
        outState.putString("teama_score",scorea.getText().toString());
        outState.putString("teamb_score",scoreb.getText().toString());
    }



    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("score111","restore");
        scorea.setText(savedInstanceState.getString("teama_score"));
        scoreb.setText(savedInstanceState.getString("teamb_score"));

    }
    public void add1A(View view){
        Log.i("error","=====>2");
        add(scorea,1);
        //scorea.setText(String.valueOf(Integer.parseInt(scorea.getText().toString())+1));
    }
    public void add2A(View view){
        add(scorea,2);
    }
    public void add3A(View view){
        add(scorea,3);
    }
    public void add1B(View view){
        add(scoreb,1);
    }
    public void add2B(View view){
        add(scoreb,2);
    }
    public void add3B(View view){
        add(scoreb,3);
    }
    public void add(TextView s,int n){
        Log.i("error","=====>1");
        s.setText(String.valueOf(Integer.parseInt(s.getText().toString())+n));
        Log.i("error","=====>3");
    }
    public void reset(View view){
        scorea.setText("0");
        scoreb.setText("0");
    }
}
