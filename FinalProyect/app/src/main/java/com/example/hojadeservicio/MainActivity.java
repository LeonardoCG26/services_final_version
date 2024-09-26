package com.example.hojadeservicio;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button BtnServicio, btnHrIncompleto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BtnServicio = findViewById(R.id.btnServicio);
        btnHrIncompleto = findViewById(R.id.btnHrIncompleto);

        BtnServicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RegistroF1.class);
                startActivity(intent);
                finish();
            }
        });

        btnHrIncompleto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HRIncompletoActivity.class);
                startActivity(intent);
            }
        });

    }
}