package com.example.gasbill;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    DatabaseHelper dbHelper;
    EditText etName, etYYYYMM, etAddress, etUsedNumGas;
    Spinner spinnerGasLevelTypeID;
    Button btnSave, btnViewCustomer;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get the saved state of the music
        SharedPreferences preferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        boolean isMusicPlaying = preferences.getBoolean("music_playing", true); // Default to true

        // Start music service only if music was previously playing
        if (isMusicPlaying) {
            Intent serviceIntent = new Intent(this, MusicService.class);
            startService(serviceIntent);
        }
        // Khởi tạo Toolbar và thiết lập như ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Khởi tạo DrawerLayout và ActionBarDrawerToggle
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Xử lý các sự kiện khi chọn item trong NavigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_main) {
                    // Xử lý khi chọn "Bill" và chuyển đến CustomerDetailsActivity
                    Intent intent = new Intent(MainActivity.this, MainActivityView.class);
                    startActivity(intent);
                } else if (id == R.id.nav_bill) {
                    // Xử lý khi chọn "Settings"
                    Intent intent = new Intent(MainActivity.this, CustomerDetailsActivity.class);  // Tạo Activity ChangeUnitActivity
                    startActivity(intent);
                } else if (id == R.id.nav_settings) {
                    // Xử lý khi chọn "Settings"
                    Intent intent = new Intent(MainActivity.this, SettingsActivity.class);  // Tạo Activity ChangeUnitActivity
                    startActivity(intent);
                } else if (id == R.id.nav_change_unit) {
                    // Xử lý khi chọn "Change unit"
                    Intent intent = new Intent(MainActivity.this, ChangeUnitActivity.class);  // Tạo Activity ChangeUnitActivity
                    startActivity(intent);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        dbHelper = new DatabaseHelper(this);

        // Get the DrawerToggle and change the hamburger icon color to white
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Các phần còn lại của code như thiết lập EditText, Spinner và các sự kiện khác
        etName = findViewById(R.id.et_name);
        etYYYYMM = findViewById(R.id.et_yyyymm);
        etAddress = findViewById(R.id.et_address);
        etUsedNumGas = findViewById(R.id.et_used_num_gas);
        spinnerGasLevelTypeID = findViewById(R.id.spinner_gas_level_type_id);
        btnSave = findViewById(R.id.btn_save);

        // Thiết lập DatePicker cho etYYYYMM
        etYYYYMM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(Calendar.YEAR, selectedYear);
                    calendar.set(Calendar.MONTH, selectedMonth);
                    calendar.set(Calendar.DAY_OF_MONTH, selectedDay);

                    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault());
                    String formattedDate = dateFormat.format(calendar.getTime());

                    etYYYYMM.setText(formattedDate);
                }, year, month, day);

                datePickerDialog.show();
            }
        });

        // Thiết lập Spinner với các giá trị "Hãy chọn giá trị", "Level 1", "Level 2"
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"Choose level", "Level 1", "Level 2"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGasLevelTypeID.setAdapter(adapter);


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString();
                String yyyymm = etYYYYMM.getText().toString();
                String address = etAddress.getText().toString();
                String usedNumGasStr = etUsedNumGas.getText().toString();
                String gasLevelTypeIDStr = spinnerGasLevelTypeID.getSelectedItem().toString();

                // Check if spinner selection is valid
                if (gasLevelTypeIDStr.equals("Choose level")) {
                    Toast.makeText(MainActivity.this, "Please choose level type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (name.isEmpty() || yyyymm.isEmpty() || address.isEmpty() || usedNumGasStr.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double usedNumGas = Double.parseDouble(usedNumGasStr);
                int gasLevelTypeID = 0; // default value

                // Map spinner values to gasLevelTypeID
                if (gasLevelTypeIDStr.equals("Level 1")) {
                    gasLevelTypeID = 1;
                } else if (gasLevelTypeIDStr.equals("Level 2")) {
                    gasLevelTypeID = 2;
                }

                boolean isInserted = dbHelper.insertCustomerData(name, yyyymm, address, usedNumGas, gasLevelTypeID);

                if (isInserted) {
                    Toast.makeText(MainActivity.this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Error Inserting Data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnViewCustomer = findViewById(R.id.btn_viewcustomer);
        btnViewCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an intent to start CheckDetailCustomerActivity
                Intent intent = new Intent(MainActivity.this, CustomerDetailsActivity.class);
                // Start the new activity
                startActivity(intent);
            }
        });

    }
}