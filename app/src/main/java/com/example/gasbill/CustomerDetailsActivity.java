package com.example.gasbill;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.SearchView;
import com.google.android.material.navigation.NavigationView;
import java.text.DecimalFormat;

public class CustomerDetailsActivity extends BaseActivity {

    private TextView tvCustomerId, tvCustomerName, tvCustomerYYYYMM, tvCustomerAddress, tvCustomerUsedNumGas, tvCustomerGasLevelTypeID;
    private Button btnFirst, btnPrevious, btnNext, btnLast;
    private Cursor cursor;
    private DatabaseHelper dbHelper;
    private int currentPosition = 0;
    private TextView tvCustomerPrice;
    private ActionBarDrawerToggle drawerToggle;
    private double calculatedPrice;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        // Khởi tạo Toolbar và thiết lập như ActionBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tvCustomerPrice = findViewById(R.id.tv_customer_price);
        checkPriceDisplay();

        // Khởi tạo DrawerLayout và ActionBarDrawerToggle
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        // Start music service when the activity is created
        Intent serviceIntent = new Intent(this, MusicService.class);
        startService(serviceIntent);

        // Kết nối DrawerLayout với ActionBar
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState(); // Đồng bộ hóa trạng thái ban đầu với biểu tượng hamburger

        // Hiển thị biểu tượng hamburger trên ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        // Xử lý các sự kiện khi chọn item trong NavigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_main) {
                    // Xử lý khi chọn "Home"
                    Intent intent = new Intent(CustomerDetailsActivity.this, MainActivityView.class);
                    startActivity(intent);
                } else if (id == R.id.nav_home) {
                    // Xử lý khi chọn "Settings"
                    Intent intent = new Intent(CustomerDetailsActivity.this, MainActivity.class);  // Tạo Activity ChangeUnitActivity
                    startActivity(intent);
                } else if (id == R.id.nav_settings) {
                    // Xử lý khi chọn "Settings"
                    Intent intent = new Intent(CustomerDetailsActivity.this, SettingsActivity.class);  // Tạo Activity ChangeUnitActivity
                    startActivity(intent);
                } else if (id == R.id.nav_change_unit) {
                    // Xử lý khi chọn "Change unit"
                    Intent intent = new Intent(CustomerDetailsActivity.this, ChangeUnitActivity.class);  // Tạo Activity ChangeUnitActivity
                    startActivity(intent);
                }

                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        // Tắt tiêu đề mặc định
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        // Set màu trắng cho hamburger icon
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                // Không cần xử lý ở đây
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                // Khi mở ngăn kéo, chắc chắn hamburger vẫn giữ màu trắng
                toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                // Khi đóng ngăn kéo, cũng đặt lại màu trắng
                toggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                // Không cần xử lý ở đây
            }
        });

        toggle.syncState();


        // Tìm kiếm SearchView
        SearchView searchView = findViewById(R.id.search_view);

        // SearchView mở
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ẩn biểu tượng hamburger
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setHomeButtonEnabled(false);
                }

                // Buộc cập nhật lại menu để thay đổi trạng thái ngay lập tức
                invalidateOptionsMenu();

                // Mở rộng SearchView
                searchView.setLayoutParams(new Toolbar.LayoutParams(
                        Toolbar.LayoutParams.MATCH_PARENT, // Set width to match_parent
                        Toolbar.LayoutParams.WRAP_CONTENT
                ));
                searchView.setQueryHint("Search by name or address");
            }
        });

        // SearchView đóng
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Hiển thị lại biểu tượng hamburger
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                }

                // Buộc cập nhật lại menu để đảm bảo biểu tượng hamburger không bị thay đổi
                invalidateOptionsMenu();

                // Đặt lại layout của SearchView
                Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
                        Toolbar.LayoutParams.WRAP_CONTENT,
                        Toolbar.LayoutParams.WRAP_CONTENT
                );
                layoutParams.gravity = Gravity.END; // Đặt gravity cho nút ở bên phải
                searchView.setLayoutParams(layoutParams);

                // Đồng bộ lại DrawerToggle để đảm bảo biểu tượng hamburger đúng
                drawerToggle.syncState();

                return false;
            }
        });


        // Đặt màu chữ cho SearchView
        int searchTextColor = Color.WHITE; // Màu trắng
        int hintColor = Color.WHITE; // Màu trắng cho gợi ý văn bản
        int cursorColor = Color.WHITE; // Màu trắng cho con trỏ

        // Áp dụng màu chữ cho SearchView
        searchView.setQueryHint("Search by name or address");
        searchView.setIconifiedByDefault(true);

        // Lấy TextView bên trong SearchView
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(searchTextColor); // Đặt màu chữ
        searchEditText.setHintTextColor(hintColor); // Đặt màu cho gợi ý văn bản

        //đặt màu cho con trỏ
        searchEditText.setTextCursorDrawable(new ColorDrawable(cursorColor));

        // Nếu muốn con trỏ nhấp nháy, bạn có thể sử dụng thuộc tính này
        searchEditText.setCursorVisible(true);

        // Tắt nhấp nháy của con trỏ
        searchEditText.setFocusable(true);
        searchEditText.setFocusableInTouchMode(true);
        searchEditText.requestFocus(); // Đặt tiêu điểm vào EditText

        // Khởi tạo các thành phần giao diện
        TextView tvCustomerAddress = findViewById(R.id.tv_customer_address);
        TextView tvCustomerUsedNumGas = findViewById(R.id.tv_customer_used_num_gas);
        TextView tvCustomerGasLevelTypeID = findViewById(R.id.tv_customer_gas_level_type_id);
        TextView tvCustomerPrice = findViewById(R.id.tv_customer_price);

        // Kiểm tra cài đặt từ SharedPreferences
        SharedPreferences preferences = getSharedPreferences("customer_settings", MODE_PRIVATE);

        if (!preferences.getBoolean("show_address", true)) {
            tvCustomerAddress.setVisibility(View.GONE);
        }
        if (!preferences.getBoolean("show_used_num_gas", true)) {
            tvCustomerUsedNumGas.setVisibility(View.GONE);
        }
        if (!preferences.getBoolean("show_gas_level_type", true)) {
            tvCustomerGasLevelTypeID.setVisibility(View.GONE);
        }
        if (!preferences.getBoolean("show_price", true)) {
            tvCustomerPrice.setVisibility(View.GONE);
        }

        dbHelper = new DatabaseHelper(this);
        initializeViews();

        cursor = dbHelper.getAllCustomers();
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            displayCustomerDetails();
            calculateAndDisplayPrice();
        } else {
            Toast.makeText(this, "No customers found", Toast.LENGTH_SHORT).show();
            finish();
        }

        TextView tvResultsCount = findViewById(R.id.tv_results_count);
        tvResultsCount.setVisibility(View.GONE); // Ẩn TextView ban đầu

        // Khi SearchView được mở, hiển thị tv_results_count
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeButtonEnabled(false);
                tvResultsCount.setVisibility(View.VISIBLE);
                searchEditText.requestFocus(); // Đặt tiêu điểm vào EditText
                searchEditText.setCursorVisible(true); // Đảm bảo con trỏ được hiển thị
                searchEditText.setSelection(searchEditText.getText().length()); // Đưa con trỏ về cuối văn bản
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                // Hiển thị lại biểu tượng hamburger
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setHomeButtonEnabled(true);
                }

                // Buộc cập nhật lại menu để đảm bảo biểu tượng hamburger không bị thay đổi
                invalidateOptionsMenu();

                // Đồng bộ lại DrawerToggle để đảm bảo biểu tượng hamburger đúng
                drawerToggle.syncState();

                // Đặt lại màu trắng cho biểu tượng hamburger
                drawerToggle.getDrawerArrowDrawable().setColor(getResources().getColor(android.R.color.white));
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setHomeButtonEnabled(true);
                TextView tvResultsCount = findViewById(R.id.tv_results_count);
                tvResultsCount.setVisibility(View.GONE); // Ẩn TextView


                return false;
            }
        });


        setButtonListeners();
        setCardViewClickListener();
    }

    private void initializeViews() {
        tvCustomerId = findViewById(R.id.tv_customer_id);
        tvCustomerName = findViewById(R.id.tv_customer_name);
        tvCustomerYYYYMM = findViewById(R.id.tv_customer_yyyymm);
        tvCustomerAddress = findViewById(R.id.tv_customer_address);
        tvCustomerUsedNumGas = findViewById(R.id.tv_customer_used_num_gas);
        tvCustomerGasLevelTypeID = findViewById(R.id.tv_customer_gas_level_type_id);
        tvCustomerPrice = findViewById(R.id.tv_customer_price);
        btnFirst = findViewById(R.id.btn_first);
        btnPrevious = findViewById(R.id.btn_previous);
        btnNext = findViewById(R.id.btn_next);
        btnLast = findViewById(R.id.btn_last);
    }

    private void setButtonListeners() {
        btnFirst.setOnClickListener(v -> {
            if (cursor.moveToFirst()) {
                currentPosition = 0;
                displayCustomerDetails();
            }
        });

        btnPrevious.setOnClickListener(v -> {
            if (cursor.moveToPrevious()) {
                currentPosition--;
                displayCustomerDetails();
            } else {
                Toast.makeText(CustomerDetailsActivity.this, "No previous customer", Toast.LENGTH_SHORT).show();
            }
        });

        btnNext.setOnClickListener(v -> {
            if (cursor.moveToNext()) {
                currentPosition++;
                displayCustomerDetails();
            } else {
                Toast.makeText(CustomerDetailsActivity.this, "No next customer", Toast.LENGTH_SHORT).show();
            }
        });

        btnLast.setOnClickListener(v -> {
            if (cursor.moveToLast()) {
                currentPosition = cursor.getCount() - 1;
                displayCustomerDetails();
            }
        });

        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCustomer(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchCustomer(newText);
                return false;
            }
        });

    }

    private void setCardViewClickListener() {
        findViewById(R.id.card_view).setOnClickListener(v -> showUpdateDialog());
    }

    private void showUpdateDialog() {
        // Create an AlertDialog.Builder instance
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create a LinearLayout for the dialog's title
        LinearLayout titleLayout = new LinearLayout(this);
        titleLayout.setOrientation(LinearLayout.VERTICAL);
        titleLayout.setGravity(Gravity.CENTER); // Center the title
        titleLayout.setPadding(16, 16, 16, 16); // Padding for the title

        // Create a TextView for the title
        TextView titleTextView = new TextView(this);
        titleTextView.setText("Update Customer Details");
        titleTextView.setTextSize(20); // Set the text size
        titleTextView.setGravity(Gravity.CENTER); // Center the title text
        titleTextView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        // Add the title TextView to the title layout
        titleLayout.addView(titleTextView);

        // Create a ScrollView to make the dialog scrollable
        ScrollView scrollView = new ScrollView(this);

        // Create a LinearLayout to hold the TextViews and EditTexts
        LinearLayout dialogView = new LinearLayout(this);
        dialogView.setOrientation(LinearLayout.VERTICAL);
        dialogView.setPadding(16, 16, 16, 16); // Padding for LinearLayout

        // Create the EditTexts
        EditText editTextName = new EditText(this);
        EditText editTextAddress = new EditText(this);
        EditText editTextUsedNumGas = new EditText(this);
        EditText editTextGasLevelTypeId = new EditText(this);

        // Set properties for the EditTexts
        editTextName.setHint("Enter customer name");
        editTextAddress.setHint("Enter customer address");
        editTextUsedNumGas.setHint("Enter used number of gas");
        editTextGasLevelTypeId.setHint("Enter gas level");

        // Create and add TextViews to the LinearLayout
        TextView textViewName = new TextView(this);
        textViewName.setText("Name");
        textViewName.setTextSize(20); // Increase text size
        textViewName.setPadding(15, 20, 0, 10); // Add padding top
        dialogView.addView(textViewName);
        dialogView.addView(editTextName);

        TextView textViewAddress = new TextView(this);
        textViewAddress.setText("Address");
        textViewAddress.setTextSize(20); // Increase text size
        textViewAddress.setPadding(15, 20, 0, 10); // Add padding top
        dialogView.addView(textViewAddress);
        dialogView.addView(editTextAddress);

        TextView textViewUsedNumGas = new TextView(this);
        textViewUsedNumGas.setText("Used Num Gas");
        textViewUsedNumGas.setTextSize(20); // Increase text size
        textViewUsedNumGas.setPadding(15, 20, 0, 10); // Add padding top
        dialogView.addView(textViewUsedNumGas);
        dialogView.addView(editTextUsedNumGas);

        TextView textViewGasLevelTypeId = new TextView(this);
        textViewGasLevelTypeId.setText("Gas Level");
        textViewGasLevelTypeId.setTextSize(20); // Increase text size
        textViewGasLevelTypeId.setPadding(15, 20, 0, 10); // Add padding top
        dialogView.addView(textViewGasLevelTypeId);
        dialogView.addView(editTextGasLevelTypeId);

        // Set the text for the EditTexts from the TextViews
        editTextName.setText(tvCustomerName.getText().toString().replace("Name: ", ""));
        editTextAddress.setText(tvCustomerAddress.getText().toString().replace("Address: ", ""));
        editTextUsedNumGas.setText(tvCustomerUsedNumGas.getText().toString().replace("Used Num Gas: ", ""));
        editTextGasLevelTypeId.setText(tvCustomerGasLevelTypeID.getText().toString().replace("Gas Level: ", ""));

        // Add the LinearLayout to the ScrollView
        scrollView.addView(dialogView);

        // Create a new LinearLayout to hold both title and scrollView
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.addView(titleLayout);
        mainLayout.addView(scrollView);

        // Set the main layout as the dialog view
        builder.setView(mainLayout);

        builder.setPositiveButton("Update", (dialog, which) -> updateCustomerDetails(
                editTextName.getText().toString(),
                editTextAddress.getText().toString(),
                Double.parseDouble(editTextUsedNumGas.getText().toString()),
                Integer.parseInt(editTextGasLevelTypeId.getText().toString())
        ));

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void updateCustomerDetails(String name, String address, double usedNumGas, int gasLevelTypeId) {
        int customerId = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
        String yyyymm = tvCustomerYYYYMM.getText().toString().replace("Date: ", "");

        dbHelper.updateCustomer(customerId, name, yyyymm, address, usedNumGas, gasLevelTypeId);

        // Cập nhật lại hiển thị sau khi cập nhật
        cursor = dbHelper.getAllCustomers();
        cursor.moveToPosition(currentPosition);
        displayCustomerDetails();
    }


    private void displayCustomerDetails() {
        if (cursor != null) {
            // Lấy SharedPreferences để kiểm tra trạng thái hiển thị
            SharedPreferences sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
            boolean showAddress = sharedPreferences.getBoolean("showAddress", true);
            boolean showUsedNumGas = sharedPreferences.getBoolean("showUsedNumGas", true);
            boolean showGasLevelTypeName = sharedPreferences.getBoolean("showGasLevelTypeName", true);
            boolean showPrice = sharedPreferences.getBoolean("showPrice", true);
            Log.d("CustomerDetailsActivity", "showPrice: " + showPrice);

            // Lấy và hiển thị thông tin từ cursor
            int idIndex = cursor.getColumnIndex("ID");
            int nameIndex = cursor.getColumnIndex("NAME");
            int yyyymmIndex = cursor.getColumnIndex("YYYYMM");
            int addressIndex = cursor.getColumnIndex("ADDRESS");
            int usedNumGasIndex = cursor.getColumnIndex("USED_NUM_GAS");
            int gasLevelTypeIdIndex = cursor.getColumnIndex("GAS_LEVEL_TYPE_ID");

            if (idIndex != -1) {
                tvCustomerId.setText("ID: " + cursor.getInt(idIndex));
            }

            if (nameIndex != -1) {
                tvCustomerName.setText("Name: " + cursor.getString(nameIndex));
            }

            if (yyyymmIndex != -1) {
                tvCustomerYYYYMM.setText("Date: " + cursor.getString(yyyymmIndex));
            }

            // Hiển thị Address, hoặc "***" nếu bị ẩn
            if (addressIndex != -1) {
                tvCustomerAddress.setText("Address: " + cursor.getString(addressIndex));
            }

            // Hiển thị Used Num Gas, tính toán giá, hoặc "***" nếu bị ẩn
            if (usedNumGasIndex != -1) {
                double usedNumGas = cursor.getDouble(usedNumGasIndex);
                tvCustomerUsedNumGas.setText("Used Num Gas: " + usedNumGas);
            }

            // Hiển thị Gas Level Type ID, hoặc "***" nếu bị ẩn
            if (gasLevelTypeIdIndex != -1) {
                tvCustomerGasLevelTypeID.setText("Gas Level: " + cursor.getInt(gasLevelTypeIdIndex));
            }

            calculateAndDisplayPrice(); // Tính toán và hiển thị giá khi hiển thị thông tin khách hàng

            // Hiển thị số kết quả tìm kiếm
            TextView tvResultsCount = findViewById(R.id.tv_results_count);
            tvResultsCount.setText(String.format("%d/%d", cursor.getPosition() + 1, cursor.getCount()));
        } else {
            Log.e("TAG", "No data found");
        }
    }


    private void calculateAndDisplayPrice() {
        // Lấy SharedPreferences để kiểm tra trạng thái hiển thị giá
        SharedPreferences sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        boolean showPrice = sharedPreferences.getBoolean("showPrice", true);

        if (cursor != null) {
            int usedNumGasIndex = cursor.getColumnIndex("USED_NUM_GAS");
            int gasLevelTypeIdIndex = cursor.getColumnIndex("GAS_LEVEL_TYPE_ID");
            double usedNumGas = cursor.getDouble(usedNumGasIndex);
            int gasLevelTypeId = cursor.getInt(gasLevelTypeIdIndex);

            // Lấy thông tin từ bảng gas_level_type
            double unitPrice = dbHelper.getUnitPriceByGasLevelTypeId(gasLevelTypeId);
            double maxNumGas = dbHelper.getMaxNumGasByGasLevelTypeId(gasLevelTypeId);
            double rate = dbHelper.getRateByGasLevelTypeId(gasLevelTypeId);

            // Tính toán giá
            if (usedNumGas <= maxNumGas) {
                calculatedPrice = usedNumGas * unitPrice;
            } else {
                calculatedPrice = maxNumGas * unitPrice + (usedNumGas - maxNumGas) * unitPrice * rate;
            }

            // Định dạng giá
            DecimalFormat df = new DecimalFormat("#,###.##"); // Định dạng với dấu phẩy và 2 chữ số thập phân
            if (showPrice) {
                tvCustomerPrice.setText("Price: " + df.format(calculatedPrice)); // Hiển thị giá
            } else {
                tvCustomerPrice.setText("Price: ***"); // Hiển thị "***" nếu không cho phép
            }
        }
    }

    private void checkPriceDisplay() {
        // Lấy SharedPreferences để kiểm tra trạng thái hiển thị
        SharedPreferences sharedPreferences = getSharedPreferences("SettingsPrefs", MODE_PRIVATE);
        boolean showPrice = sharedPreferences.getBoolean("showPrice", true);

        // Cập nhật hiển thị cho giá
        if (showPrice) {
            tvCustomerPrice.setVisibility(View.VISIBLE); // Hiển thị nếu được phép
        } else {
            tvCustomerPrice.setVisibility(View.VISIBLE); // Vẫn hiển thị TextView
            tvCustomerPrice.setText("Price: ***"); // Thay thế nội dung bằng "***"
        }
    }


    private void searchCustomer(String query) {
        cursor = dbHelper.searchCustomerByNameOrAddress(query);

        TextView tvResultsCount = findViewById(R.id.tv_results_count);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            displayCustomerDetails();
            // Cập nhật TextView với số lượng kết quả
            tvResultsCount.setText(String.format("%d/%d", cursor.getPosition() + 1, cursor.getCount()));
        } else {
            Toast.makeText(this, "No customer found", Toast.LENGTH_SHORT).show();
            tvResultsCount.setText("0/0"); // Không có kết quả
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cursor != null) {
            cursor.close();
        }
    }
}