package com.example.gasbill;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ChangeUnitActivity extends BaseActivity {
    DatabaseHelper dbHelper;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    LinearLayout cardContainer; // Container for dynamically adding CardViews


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_unit); // Updated layout with CardView

        // Initialize Toolbar and set it as the ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize DrawerLayout and ActionBarDrawerToggle
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Get the DrawerToggle and change the hamburger icon color to white
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Handle navigation item selection
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.nav_main) {
                    Intent intent = new Intent(ChangeUnitActivity.this, MainActivityView.class);
                    startActivity(intent);
                } else if (id == R.id.nav_home) {
                    Intent intent = new Intent(ChangeUnitActivity.this, MainActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_bill) {
                    Intent intent = new Intent(ChangeUnitActivity.this, CustomerDetailsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_settings) {
                    Intent intent = new Intent(ChangeUnitActivity.this, SettingsActivity.class);
                    startActivity(intent);
                } else if (id == R.id.nav_change_unit) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return true;
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        cardContainer = findViewById(R.id.card_container); // Reference to the container

        dbHelper = new DatabaseHelper(this);
        loadGasLevelTypes();
    }

    private void loadGasLevelTypes() {
        // Clear the container before adding new CardViews to prevent duplicates
        cardContainer.removeAllViews();

        Cursor cursor = dbHelper.getGasLevelTypes();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                double unitPrice = cursor.getDouble(2);

                // Create and add a CardView for each gas level
                addCardView(id, name, unitPrice);
            }
        } else {
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }


    // Function to dynamically add CardView to the layout
    private void addCardView(int gasLevelId, String gasLevelName, double price) {
        // Inflate a new CardView
        LayoutInflater inflater = LayoutInflater.from(this);
        View cardView = inflater.inflate(R.layout.cardview_gas_level, cardContainer, false);

        // Set the data in the CardView
        TextView gasLevelIdTextView = cardView.findViewById(R.id.tv_gas_level_id);
        TextView gasLevelTypeTextView = cardView.findViewById(R.id.tv_gas_level_type);
        TextView priceTextView = cardView.findViewById(R.id.tv_price);

        gasLevelIdTextView.setText("Gas Level ID: " + gasLevelId);
        gasLevelTypeTextView.setText("Gas Level Type: " + gasLevelName);
        priceTextView.setText("Price: " + price);

        // Set OnClickListener to handle card click for editing price
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditPriceDialog(gasLevelId, gasLevelName, price);
            }
        });

        // Add the card to the container
        cardContainer.addView(cardView);
    }

    private void showEditPriceDialog(int gasLevelId, String gasLevelName, double currentPrice) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Price for " + gasLevelName);


        final EditText input = new EditText(this);
        input.setText(String.valueOf(currentPrice));
        builder.setView(input);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPriceStr = input.getText().toString();
                if (!newPriceStr.isEmpty()) {
                    try {
                        double newPrice = Double.parseDouble(newPriceStr);
                        if (newPrice != currentPrice) {
                            double priceDifference = newPrice - currentPrice;
                            String notificationType = priceDifference > 0 ? "increase" : "decrease";

                            dbHelper.updateGasLevelPrice(gasLevelId, newPrice);
                            loadGasLevelTypes();

                            String currentTime = getCurrentTime();
                            String notification = "Already " + notificationType + "d gas unit price for " + gasLevelName +
                                    " with amount " + Math.abs(priceDifference) +
                                    " at " + currentTime;
                            showNotificationDialog(notification);
                        } else {
                            Toast.makeText(ChangeUnitActivity.this, "New price is the same as the current price", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(ChangeUnitActivity.this, "Invalid price input", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ChangeUnitActivity.this, "Price cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private String getCurrentTime() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new java.util.Date());
    }

    private void showNotificationDialog(String message) {
        AlertDialog.Builder notificationDialog = new AlertDialog.Builder(this);
        notificationDialog.setTitle("Price Update Notification");
        notificationDialog.setMessage(message);
        notificationDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        notificationDialog.show();
    }



}
