package com.example.gasbill;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "GasBill.db";
    private static final int DATABASE_VERSION = 4; // Tăng phiên bản cơ sở dữ liệu

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE customer (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT NOT NULL, " +
                "YYYYMM TEXT NOT NULL, " +
                "ADDRESS TEXT NOT NULL, " +
                "USED_NUM_GAS REAL NOT NULL, " +
                "GAS_LEVEL_TYPE_ID INTEGER, " +
                "FOREIGN KEY(GAS_LEVEL_TYPE_ID) REFERENCES gas_level_type(ID))");

        db.execSQL("CREATE TABLE gas_level_type (" +
                "ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "GAS_LEVEL_TYPE_NAME TEXT NOT NULL, " +
                "UNIT_PRICE REAL NOT NULL, " +
                "MAX_NUM_GAS INTEGER NOT NULL, " +
                "RATE_PRICE_FOR_OVER REAL NOT NULL)");

        // Insert sample data
        db.execSQL("INSERT INTO gas_level_type (GAS_LEVEL_TYPE_NAME, UNIT_PRICE, MAX_NUM_GAS, RATE_PRICE_FOR_OVER) VALUES " +
                "('Level1', 1000, 50, 1.5), " +
                "('Level2', 2000, 100, 1.8)");

        db.execSQL("INSERT INTO customer (NAME, YYYYMM, ADDRESS, USED_NUM_GAS, GAS_LEVEL_TYPE_ID) VALUES " +
                "('Ramesh', '202401', 'Ahmedabad', 2000, 1), " +
                "('Khilan', '202401', 'Delhi', 1500, 2), " +
                "('Kaushik', '202402', 'Kota', 2000, 1), " +
                "('Chaitali', '202402', 'Mumbai', 6500, 2), " +
                "('Hardik', '202403', 'Bhopal', 8500, 2), " +
                "('Komal', '202403', 'MP', 4500, 1)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS customer");
        db.execSQL("DROP TABLE IF EXISTS gas_level_type");
        onCreate(db);
    }

    public void updateCustomer(int customerId, String name, String yyyymm, String address, double usedNumGas, int gasLevelTypeId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("YYYYMM", yyyymm); // Thêm trường YYYYMM vào cập nhật
        contentValues.put("ADDRESS", address);
        contentValues.put("USED_NUM_GAS", usedNumGas);
        contentValues.put("GAS_LEVEL_TYPE_ID", gasLevelTypeId);

        // Cập nhật bản ghi trong cơ sở dữ liệu
        db.update("customer", contentValues, "ID = ?", new String[]{String.valueOf(customerId)});
        db.close();
    }

    public boolean insertCustomerData(String name, String yyyymm, String address, double usedNumGas, int gasLevelTypeID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("NAME", name);
        contentValues.put("YYYYMM", yyyymm);
        contentValues.put("ADDRESS", address);
        contentValues.put("USED_NUM_GAS", usedNumGas);
        contentValues.put("GAS_LEVEL_TYPE_ID", gasLevelTypeID);

        long result = db.insert("customer", null, contentValues);
        return result != -1;  // Trả về true nếu dữ liệu được chèn, false nếu không
    }

    public Cursor getAllCustomers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM customer", null);
    }

    public Cursor searchCustomerByNameOrAddress(String query) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM customer WHERE NAME LIKE ? OR ADDRESS LIKE ?";
        String[] selectionArgs = new String[]{"%" + query + "%", "%" + query + "%"};
        return db.rawQuery(sql, selectionArgs);
    }

    public Cursor getGasLevelTypes() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT ID, GAS_LEVEL_TYPE_NAME, UNIT_PRICE FROM gas_level_type", null);
    }
    public void updateGasLevelPrice(int gasLevelId, double newPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("UNIT_PRICE", newPrice);

        db.update("gas_level_type", contentValues, "ID = ?", new String[]{String.valueOf(gasLevelId)});
        db.close();
    }

    public double getUnitPriceByGasLevelTypeId(int gasLevelTypeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT UNIT_PRICE FROM gas_level_type WHERE ID = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(gasLevelTypeId)});

        double unitPrice = 0; // Khởi tạo giá trị mặc định
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("UNIT_PRICE");
                if (columnIndex != -1) { // Kiểm tra nếu cột tồn tại
                    unitPrice = cursor.getDouble(columnIndex);
                }
            }
            cursor.close();
        }

        return unitPrice; // Trả về giá trị (hoặc 0 nếu không tìm thấy)
    }

    public double getMaxNumGasByGasLevelTypeId(int gasLevelTypeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT MAX_NUM_GAS FROM gas_level_type WHERE ID = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(gasLevelTypeId)});

        double maxNumGas = 0; // Khởi tạo giá trị mặc định
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("MAX_NUM_GAS");
                if (columnIndex != -1) { // Kiểm tra nếu cột tồn tại
                    maxNumGas = cursor.getDouble(columnIndex);
                }
            }
            cursor.close();
        }

        return maxNumGas; // Trả về giá trị (hoặc 0 nếu không tìm thấy)
    }

    public double getRateByGasLevelTypeId(int gasLevelTypeId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT RATE_PRICE_FOR_OVER FROM gas_level_type WHERE ID = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(gasLevelTypeId)});

        double rate = 0; // Khởi tạo giá trị mặc định
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex("RATE_PRICE_FOR_OVER");
                if (columnIndex != -1) { // Kiểm tra nếu cột tồn tại
                    rate = cursor.getDouble(columnIndex);
                }
            }
            cursor.close();
        }

        return rate; // Trả về giá trị (hoặc 0 nếu không tìm thấy)
    }
}
