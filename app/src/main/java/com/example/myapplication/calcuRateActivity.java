package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

public class calcuRateActivity extends AppCompatActivity {
    TextView country,result;
    EditText input;
    float rate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calcu_rate);

        country = findViewById(R.id.money);
        result = findViewById(R.id.result);
        input = findViewById(R.id.input);

        Intent intent=getIntent();
        String money=intent.getStringExtra("name_key");
        rate=100f/Float.parseFloat(intent.getStringExtra("rate_key"));

        country.setText(money);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                float rmb = Float.parseFloat(input.getText().toString());
                float res = rmb*rate;
                result.setText(res+"");

            }
        });

    }
}
