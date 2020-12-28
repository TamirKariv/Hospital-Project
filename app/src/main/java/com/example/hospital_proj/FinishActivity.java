package com.example.hospital_proj;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class FinishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finish);
        Intent intent = getIntent();
        String timer = intent.getStringExtra("time1");
        TextView our_timer = findViewById(R.id.textView_timer);
        our_timer.setText(timer);
    }

    public void returnToMenu(View v){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}
