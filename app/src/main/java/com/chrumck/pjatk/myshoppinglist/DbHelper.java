package com.chrumck.pjatk.myshoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    public enum COLUMN_NAME {id, name, price, quantity, bought}

    public static String[] COLUMN_NAMES_ARRAY =
            Arrays.toString(COLUMN_NAME.values()).replaceAll("^.|.$", "").split(", ");

    public static final String TABLE_NAME = "Products";
    private static final String DATABASE_NAME = "MyShoppingList.db";
    private static final int DATABASE_VERSION = 5;

    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_NAME.id + " INTEGER PRIMARY KEY," +
            COLUMN_NAME.name + " TEXT," +
            COLUMN_NAME.price + " REAL," +
            COLUMN_NAME.quantity + " INTEGER," +
            COLUMN_NAME.bought + " NUMERIC);";

    private static final String SQL_DELETE_TABLE =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);

        List<Product> products = new ArrayList<>();
        products.add(new Product(1, "Item1", 5, 6, false));
        products.add(new Product(2, "Item2", 56.345677, 7, false));
        products.add(new Product(3, "Item3", 566, 8, true));
        products.add(new Product(4, "Item4", 5666, 9, true));

        for (Product product : products) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME.id.toString(), product.id);
            values.put(COLUMN_NAME.name.toString(), product.name);
            values.put(COLUMN_NAME.price.toString(), product.price);
            values.put(COLUMN_NAME.quantity.toString(), product.quantity);
            values.put(COLUMN_NAME.bought.toString(), product.bought);

            db.insert(TABLE_NAME, null, values);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_TABLE);
        onCreate(db);
    }

    public List<Product> getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, COLUMN_NAMES_ARRAY, null, null, null, null, null);

        List<Product> products = new ArrayList<>();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(getColumnIndex(cursor, COLUMN_NAME.id));
            String name = cursor.getString(getColumnIndex(cursor, COLUMN_NAME.name));
            double price = cursor.getDouble(getColumnIndex(cursor, COLUMN_NAME.price));
            int quantity = cursor.getInt(getColumnIndex(cursor, COLUMN_NAME.quantity));
            boolean bought = cursor.getInt(getColumnIndex(cursor, COLUMN_NAME.bought)) == 1;
            products.add(new Product(id, name, price, quantity, bought));
        }
        cursor.close();

        return products;
    }

    private static int getColumnIndex(Cursor cursor, COLUMN_NAME name) {
        return cursor.getColumnIndexOrThrow(name.toString());
    }
}