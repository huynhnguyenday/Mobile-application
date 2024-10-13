package com.example.gasbill;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;

public class MainActivityView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_view);  // Set the main view layout

        Intent musicIntent = new Intent(MainActivityView.this, MusicService.class);
        startService(musicIntent);

        // Set up button listeners
        Button btnEnterInvoice = findViewById(R.id.btn_enter_invoice);
        Button btnCustomerDetail = findViewById(R.id.btn_customer_detail);
        Button btnChangeUnit = findViewById(R.id.btn_change_unit);
        Button btnSettings = findViewById(R.id.btn_settings);

        btnEnterInvoice.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityView.this, MainActivity.class);
            startActivity(intent);
        });

        btnCustomerDetail.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityView.this, CustomerDetailsActivity.class);
            startActivity(intent);
        });

        btnChangeUnit.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityView.this, ChangeUnitActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivityView.this, SettingsActivity.class);
            startActivity(intent);
        });
    }
}
