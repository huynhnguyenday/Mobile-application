package com.example.gasbill;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class SettingsActivity extends BaseActivity {

    private Switch switchAddress, switchUsedNumGas, switchGasLevelTypeName, switchPrice;
    private SharedPreferences sharedPreferences;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;
    private Button btnPlayMusic, btnStopMusic;

    // Static variable to track music playback state
    public static boolean isMusicPlaying = false; // default is false

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize Toolbar and set it as ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back button on toolbar

        // Initialize DrawerLayout and ActionBarDrawerToggle
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        btnPlayMusic = findViewById(R.id.btnPlayMusic);
        btnStopMusic = findViewById(R.id.btnPauseMusic);

        // SharedPreferences for music playback state
        sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        isMusicPlaying = sharedPreferences.getBoolean("music_playing", false);
        updateMusicButtons(isMusicPlaying);

        btnPlayMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(SettingsActivity.this, MusicService.class);
                startService(serviceIntent); // Start the music service
                isMusicPlaying = true; // Update static variable
                updateMusicButtons(true); // Update button states
                sharedPreferences.edit().putBoolean("music_playing", true).apply(); // Save state
            }
        });

        btnStopMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent serviceIntent = new Intent(SettingsActivity.this, MusicService.class);
                stopService(serviceIntent); // Stop the music service
                isMusicPlaying = false; // Update static variable
                updateMusicButtons(false); // Update button states
                sharedPreferences.edit().putBoolean("music_playing", false).apply(); // Save state
            }
        });

        // Handle navigation item selection
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_main) {
                    // Handle "Home" selection
                    Intent intent = new Intent(SettingsActivity.this, MainActivityView.class);
                    startActivity(intent);
                } else if (id == R.id.nav_home) {
                    // Handle "Bill" selection
                    Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_bill) {
                    // Handle "Bill" selection
                    Intent intent = new Intent(SettingsActivity.this, CustomerDetailsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_settings) {
                    // Handle "Settings" selection

                } else if (id == R.id.nav_change_unit) {
                    // Handle "Change unit" selection
                    Intent intent = new Intent(SettingsActivity.this, ChangeUnitActivity.class);
                    startActivity(intent);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Get the DrawerToggle and change the hamburger icon color to white
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize Switches
        switchAddress = findViewById(R.id.switch_show_address);
        switchUsedNumGas = findViewById(R.id.switch_show_used_num_gas);
        switchGasLevelTypeName = findViewById(R.id.switch_show_gas_level_type);
        switchPrice = findViewById(R.id.switch_show_price);

        // Load saved settings
        loadSettings();

        // Save switch states in SharedPreferences
        switchAddress.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("showAddress", isChecked).apply();
        });

        switchUsedNumGas.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("showUsedNumGas", isChecked).apply();
        });

        switchGasLevelTypeName.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("showGasLevelTypeName", isChecked).apply();
        });

        switchPrice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("showPrice", isChecked).apply();
        });
    }

    private void loadSettings() {
        switchAddress.setChecked(sharedPreferences.getBoolean("showAddress", true));
        switchUsedNumGas.setChecked(sharedPreferences.getBoolean("showUsedNumGas", true));
        switchGasLevelTypeName.setChecked(sharedPreferences.getBoolean("showGasLevelTypeName", true));
        switchPrice.setChecked(sharedPreferences.getBoolean("showPrice", true));
    }

    private void updateMusicButtons(boolean isPlaying) {
        btnPlayMusic.setEnabled(!isPlaying); // Disable play button if music is playing
        btnStopMusic.setEnabled(isPlaying); // Disable stop button if music is not playing
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
