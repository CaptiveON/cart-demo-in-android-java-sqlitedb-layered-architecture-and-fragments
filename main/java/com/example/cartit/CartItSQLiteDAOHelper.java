package com.example.cartit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CartItSQLiteDAOHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Cart.db";
    public static final String PRODUCT_TABLE_NAME = "Product";
    public static final String PRODUCT_ID = "id";
    public static final String PRODUCT_NAME = "name";
    public static final String PRODUCT_PRICE = "price";
    public static final String PRODUCT_DESCRIPTION = "description";
    public static final String PRODUCT_IMAGE = "image";
    private static final String CREATE_TABLE_PRODUCT = "CREATE TABLE " +
                                                        PRODUCT_TABLE_NAME +
                                                        " (" +
                                                        PRODUCT_ID + " TEXT PRIMARY KEY, " +
                                                        PRODUCT_NAME + " TEXT, " +
                                                        PRODUCT_PRICE + " INTEGER, " +
                                                        PRODUCT_DESCRIPTION + " TEXT, " +
                                                        PRODUCT_IMAGE + " INTEGER" +
                                                        ")";

    public static final String CART_TABLE_NAME = "CartList";
    public static final String CART_PRODUCT_NAME = "name";
    public static final String CART_PRODUCT_QUANTITY = "quantity";
    public static final String CREATE_TABLE_CART = "CREATE TABLE " +
                                                    CART_TABLE_NAME +
                                                    " (" +
                                                    CART_PRODUCT_NAME + " TEXT, " +
                                                    CART_PRODUCT_QUANTITY + " INTEGER, " +
                                                    "FOREIGN KEY (" +
                                                    CART_PRODUCT_NAME + ") REFERENCES " +
                                                    PRODUCT_TABLE_NAME + " (" + PRODUCT_NAME +
                                                    "))";

    public CartItSQLiteDAOHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Creating Table for Product
        //Query: "CREATE TABLE Product (id TEXT PRIMARY KEY, name TEXT, price INTEGER, description TEXT, image INTEGER)"
        db.execSQL(CREATE_TABLE_PRODUCT);
        //Query: "CREATE TABLE CartList (name TEXT PRIMARY KEY, quantity INTEGER, FOREIGN KEY (name) REFERENCES Product(name))"
        db.execSQL(CREATE_TABLE_CART);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + PRODUCT_TABLE_NAME);
        onCreate(db);
    }
}
