package com.example.cartit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class Cart extends AppCompatActivity implements RecyclerCartAdapter.RecyclerCartInterface {
    ArrayList<Product> cartList;
    RecyclerView recyclerView;
    RecyclerCartAdapter recyclerCartAdapter;
    TextView cartBill;
    Button continueShop;
    ICartDAO daoCart;
    ICartItemDAO daoCartItem;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_screen);
        getSupportActionBar().setTitle("My Cart");

        //Initializing ICartDAO and ICartItem with implementation in CartItSQLiteDAO Layer
        //as "daoCart" for ArrayList "cartList" and "daoCartItem" for operations in CartItem
        daoCart = new CartItSQLiteDAO(this);
        daoCartItem = new CartItSQLiteDAO(this);

        //Receiving cartList intended by MainActivity on launch
        cartList = daoCart.loadCartProductList();

        //TextView to show total amount of all the items in the cart with their respective amount
        //calculated in "calculateTotal()" function returning total as of type "int"
        cartBill = findViewById(R.id.totalBill);
        cartBill.setText(String.valueOf(calculateTotal()));

        //RecyclerView for showing items in cart
        recyclerView = findViewById(R.id.cartRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Adapter for binding respective provided list to RecyclerView
        recyclerCartAdapter = new RecyclerCartAdapter(this,cartList,this);
        recyclerView.setAdapter(recyclerCartAdapter);

        //Sending the expected updated cartList back to MainActivity
        setResult(RESULT_OK);

        //Go to MainActivity either to add more items to cartList
        //or if the cartList is empty as defined below with "cartItemRemove" implemented method
        continueShop = findViewById(R.id.continueShopping);
        continueShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //OnClick on "+" button (Add one more of relative item) interface method
    //and updating Database as well
    @Override
    public void onCartItemAdd(String name) {
        daoCartItem.addCartItem(name);
        //ReCalculating the total bill if the more of that item added
        cartBill.setText(String.valueOf(calculateTotal()));
    }

    //OnClick on "-" button (Remove one of relative item) interface method
    //and updating Database as well
    @Override
    public void onCartItemRemove(String name, int position) {
        //If Item is removed to 0 then remove from the cartList too
        if(cartList.get(position).getNumItem() == 0) {
            cartList.remove(position);
            daoCart.removeCartProduct(name);
            recyclerCartAdapter.notifyDataSetChanged();
            //If all the items removed from the cartList then back to MainActivity
            if(cartList.isEmpty()) {
                finish();
            }
        }
        //Updating Database in case ItemCount to be lessened
        else {
            daoCartItem.minusCartItem(name);
        }
        ////ReCalculating the total bill if the more of that item removed
        cartBill.setText(String.valueOf(calculateTotal()));
    }

    //Calculating total bill of the items added to the cartList
    //returns "bill" as of type "int"
    public int calculateTotal() {
        int bill = 0;
        if(!cartList.isEmpty()) {
            for (int i = 0; i < cartList.size(); i++) {
                bill = bill + (cartList.get(i).productPrice * cartList.get(i).itemCount);
            }
        }
        return bill;
    }
}
