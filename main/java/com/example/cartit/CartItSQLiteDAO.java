package com.example.cartit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import java.util.ArrayList;
import java.util.Hashtable;

public class CartItSQLiteDAO implements IProductDAO,ICartDAO, ICartItemDAO {
    Context context;
    public CartItSQLiteDAO(Context ctx) {
        context = ctx;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////-IProductDAO Implementation-////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    //Saves Single Product in the Database Table "Product"
    //Also avoids adding Same Products
    @Override
    public void saveProduct(@NonNull Product inProduct) {
        CartItSQLiteDAOHelper dbHelper = new CartItSQLiteDAOHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //Populating Content with relevant Data in the specified Column
        ContentValues content = new ContentValues();
        content.put(CartItSQLiteDAOHelper.PRODUCT_ID,inProduct.id);
        content.put(CartItSQLiteDAOHelper.PRODUCT_NAME,inProduct.productName);
        content.put(CartItSQLiteDAOHelper.PRODUCT_PRICE,inProduct.productPrice);
        content.put(CartItSQLiteDAOHelper.PRODUCT_DESCRIPTION,inProduct.productDescription);
        content.put(CartItSQLiteDAOHelper.PRODUCT_IMAGE,inProduct.productImage);

        //Checking is the Product already exists
        Product tempProduct = loadProduct(inProduct.productName);

        String [] arguments = new String[1];
        arguments[0] = inProduct.productName;

        //If the Product exists then Update it with provided distinguished information
        if (tempProduct != null && tempProduct.productName.equals(inProduct.productName)){
            database.update(CartItSQLiteDAOHelper.PRODUCT_TABLE_NAME,content,"name = ?",arguments);
        }
        //Otherwise insert it in the Table "Product"
        else {
            database.insert(CartItSQLiteDAOHelper.PRODUCT_TABLE_NAME,null,content);
        }
    }

    //Saves ArrayList of Products in the Database Table "Product" using saveProduct
    @Override
    public void saveProductList(ArrayList<Product> products) {
        for(Product p: products){
            saveProduct(p);
        }
    }

    //Loading Product by specified name
    @Override
    public Product loadProduct(String name) {
        Product tempProduct = new Product();
        CartItSQLiteDAOHelper dbHelper = new CartItSQLiteDAOHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        //Query: "SELECT * FROM Product WHERE name = ?"
        String query = "SELECT * FROM " + CartItSQLiteDAOHelper.PRODUCT_TABLE_NAME + " WHERE name = ?";
        String[] arguments = new String[1];
        arguments[0] = name;

        Cursor cursor = database.rawQuery(query,arguments);
        cursor.moveToFirst();
        //If a record found
        if(cursor.getCount() > 0) {
                String[] column = cursor.getColumnNames();
                //Reading and initializing tempProduct with data from relevant column
                for (String col : column) {
                    switch (col) {
                        case CartItSQLiteDAOHelper.PRODUCT_ID:
                            tempProduct.id = cursor.getString(cursor.getColumnIndexOrThrow(col));
                            break;
                        case CartItSQLiteDAOHelper.PRODUCT_NAME:
                            tempProduct.productName = cursor.getString(cursor.getColumnIndexOrThrow(col));
                            break;
                        case CartItSQLiteDAOHelper.PRODUCT_PRICE:
                            tempProduct.productPrice = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(col)));
                            break;
                        case CartItSQLiteDAOHelper.PRODUCT_DESCRIPTION:
                            tempProduct.productDescription = cursor.getString(cursor.getColumnIndexOrThrow(col));
                            break;
                        case CartItSQLiteDAOHelper.PRODUCT_IMAGE:
                            tempProduct.productImage = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(col)));
                            break;
                    }
            }
            cursor.close();
            return tempProduct;
        }
        else {
            cursor.close();
            return null;
        }
    }

    //Load all the rows from Database Table "Product"
    @Override
    public ArrayList<Product> loadProductList() {
        ArrayList<Product> outProduct = new ArrayList<>();
        CartItSQLiteDAOHelper dbHelper = new CartItSQLiteDAOHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //Query: "SELECT * FROM Product"
        String qry = "SELECT * FROM " + CartItSQLiteDAOHelper.PRODUCT_TABLE_NAME;
        Cursor cursor = database.rawQuery(qry,null);

        //Outer loop for each record
        while(cursor.moveToNext()) {
            String[] column = cursor.getColumnNames();
            Product tempProduct = new Product();
            //Inner loop for each column in a record
            for(String col: column){
                switch (col){
                    case CartItSQLiteDAOHelper.PRODUCT_ID:
                        tempProduct.id = cursor.getString(cursor.getColumnIndexOrThrow(col));
                        break;
                    case CartItSQLiteDAOHelper.PRODUCT_NAME:
                        tempProduct.productName = cursor.getString(cursor.getColumnIndexOrThrow(col));
                        break;
                    case CartItSQLiteDAOHelper.PRODUCT_PRICE:
                        tempProduct.productPrice = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(col)));
                        break;
                    case CartItSQLiteDAOHelper.PRODUCT_DESCRIPTION:
                        tempProduct.productDescription = cursor.getString(cursor.getColumnIndexOrThrow(col));
                        break;
                    case CartItSQLiteDAOHelper.PRODUCT_IMAGE:
                        tempProduct.productImage = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(col)));
                        break;
                }
            }
            outProduct.add(tempProduct);
        }
        cursor.close();
        return outProduct;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////-IProductDAO Implementation-////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    //----------------------------------------------------------------------------------------//

    ////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////-ICartDAO Implementation-/////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    //Adding single product in the Database Table CartList with Product name and quantity attributes
    @Override
    public void saveCartProduct(String inCartProductNAME) {
        int quantity;
        CartItSQLiteDAOHelper dbHelper = new CartItSQLiteDAOHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues content = new ContentValues();
        content.put(CartItSQLiteDAOHelper.CART_PRODUCT_NAME,inCartProductNAME);

        //Is Quantity of the Product to be Added is 0 or Already exists and More than 0
        quantity = existedCartItemQuantity(inCartProductNAME);

        String [] arguments = new String[1];
        arguments[0] = inCartProductNAME;
        //If 0 as the record is to be INSERTED in the Database Table CartList
        if(quantity <= 0){
            content.put(CartItSQLiteDAOHelper.CART_PRODUCT_QUANTITY,1);
            database.insert(CartItSQLiteDAOHelper.CART_TABLE_NAME,null,content);
        }
        //Else more than 0 and the record is to be UPDATED with added quantity
        else {
            quantity = quantity + 1;
            content.put(CartItSQLiteDAOHelper.CART_PRODUCT_QUANTITY,quantity);
            database.update(CartItSQLiteDAOHelper.CART_TABLE_NAME,content,"name = ?",arguments);
        }

    }

    //Remove Product from CartList specified with Product name
    @Override
    public void removeCartProduct(String name) {
        CartItSQLiteDAOHelper dbHelper = new CartItSQLiteDAOHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String [] arguments = new String[1];
        arguments[0] = name;
        database.delete(CartItSQLiteDAOHelper.CART_TABLE_NAME, "name = ?", arguments);
    }

    //Load Complete CartList
    //Relation of CartList and Product is defined as One to Many like
    //A Product has only one Cart but A Cart can have multiple Products
    @Override
    public ArrayList<Product> loadCartProductList() {
        ArrayList<Product> outCartProduct = new ArrayList<>();
        CartItSQLiteDAOHelper dbHelper = new CartItSQLiteDAOHelper(context);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        //Query: "SELECT * FROM Product INNER JOIN CartList ON Product.name = CartList.name"
        String query = "SELECT * FROM " + CartItSQLiteDAOHelper.PRODUCT_TABLE_NAME + " INNER JOIN " +
                        CartItSQLiteDAOHelper.CART_TABLE_NAME + " ON " +
                        CartItSQLiteDAOHelper.PRODUCT_TABLE_NAME + ".name" +
                        " = " +
                        CartItSQLiteDAOHelper.CART_TABLE_NAME + ".name";

        Cursor cursor = database.rawQuery(query,null);

        //Outer loop for each row
        while(cursor.moveToNext()) {
            String[] column = cursor.getColumnNames();
            Product tempProduct = new Product();
            //Inner loop for each column in row
            for(String col: column){
                switch (col){
                    case CartItSQLiteDAOHelper.PRODUCT_ID:
                        tempProduct.id = cursor.getString(cursor.getColumnIndexOrThrow(col));
                        break;
                    case CartItSQLiteDAOHelper.PRODUCT_NAME:
                        tempProduct.productName = cursor.getString(cursor.getColumnIndexOrThrow(col));
                        break;
                    case CartItSQLiteDAOHelper.PRODUCT_PRICE:
                        tempProduct.productPrice = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(col)));
                        break;
                    case CartItSQLiteDAOHelper.PRODUCT_DESCRIPTION:
                        tempProduct.productDescription = cursor.getString(cursor.getColumnIndexOrThrow(col));
                        break;
                    case CartItSQLiteDAOHelper.PRODUCT_IMAGE:
                        tempProduct.productImage = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(col)));
                        break;
                    case CartItSQLiteDAOHelper.CART_PRODUCT_QUANTITY:
                        tempProduct.itemCount = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(col)));
                        break;
                }
            }
            outCartProduct.add(tempProduct);
        }
        cursor.close();
        return outCartProduct;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////-ICartDAO Implementation-/////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    //----------------------------------------------------------------------------------------//

    ////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////-ICartItemDAO Implementation-////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////

    //Adding quantity by 1 in case the specific item's quantity is added
    @Override
    public void addCartItem(String name) {
        int quantity;
        CartItSQLiteDAOHelper dbHelper = new CartItSQLiteDAOHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //Adding to prior quantity
        quantity = existedCartItemQuantity(name) + 1;

        String [] arguments = new String[1];
        arguments[0] = name;

        ContentValues content = new ContentValues();
        content.put(CartItSQLiteDAOHelper.CART_PRODUCT_NAME,name);
        content.put(CartItSQLiteDAOHelper.CART_PRODUCT_QUANTITY,quantity);
        database.update(CartItSQLiteDAOHelper.CART_TABLE_NAME,content,"name = ?", arguments);
    }

    //Removing quantity by 1 in case the specific item's quantity is removed
    @Override
    public void minusCartItem(String name) {
        int quantity;
        CartItSQLiteDAOHelper dbHelper = new CartItSQLiteDAOHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        //Removing from prior quantity
        quantity = existedCartItemQuantity(name) - 1;

        String [] arguments = new String[1];
        arguments[0] = name;

        ContentValues content = new ContentValues();
        content.put(CartItSQLiteDAOHelper.CART_PRODUCT_NAME,name);
        content.put(CartItSQLiteDAOHelper.CART_PRODUCT_QUANTITY,quantity);
        database.update(CartItSQLiteDAOHelper.CART_TABLE_NAME,content,"name = ?", arguments);
    }

    //Helper function to retrieve prior quantity of specific cart item
    public int existedCartItemQuantity(String name){
        CartItSQLiteDAOHelper dbHelper = new CartItSQLiteDAOHelper(context);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int quantity;

        //Query: "SELECT quantity FROM CartList WHERE name = ?"
        String query = "SELECT " + CartItSQLiteDAOHelper.CART_PRODUCT_QUANTITY + " FROM " +
                        CartItSQLiteDAOHelper.CART_TABLE_NAME + " WHERE " +
                        CartItSQLiteDAOHelper.CART_PRODUCT_NAME + " = ?";

        String [] arguments = new String[1];
        arguments[0] = name;
        Cursor cursor = database.rawQuery(query,arguments);
        //If the Product exists
        if(cursor.moveToFirst()){
            quantity = Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(CartItSQLiteDAOHelper.CART_PRODUCT_QUANTITY)));
        }
        //In case Product does no exists
        else {
            quantity = 0;
        }
        cursor.close();
        return quantity;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////-ICartItemDAO Implementation-////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////
}
