package com.example.cartit;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ProductListFragment.ProductClickListener, ProductDetailFragment.AddItemToCart {
    ArrayList<Product> product = new ArrayList<>();
    ArrayList<Product> cartProduct = new ArrayList<>();
    ActivityResultLauncher<Intent> myCartActivityLauncher;
    ProductListFragment recyclerFrag;
    ProductDetailFragment detailFrag;
    IProductDAO dao;
    ICartDAO daoCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializing IProductDAO and ICartDAO with implementation in CartItSQLiteDAO Layer
        //as "dao" for ArrayList "product" and "daoCart" for ArrayList "cartProduct"
        dao = new CartItSQLiteDAO(this);
        daoCart = new CartItSQLiteDAO(this);

        //Getting SavedInstanceState and Setting DAO to initialize ArrayList "product" retrieval from Database
        CartViewModel vm = new ViewModelProvider(this).get(CartViewModel.class);
        //Initializing IProductDAO and ICartDAO instances in CartViewModel
        vm.setDao(dao);
        vm.setDaoCart(daoCart);
        //Invoking Relevant Database accessing functions in CartViewModel using Data Access Objects
        product = vm.getSavedProduct(savedInstanceState,"product");
        cartProduct = vm.getSavedCartProduct(savedInstanceState,"cartProduct");

        //Populates dummy product data in ArrayList "product" of type "Product"
        //Populates Dummy Data for Database Table "Product" every time the app installs
//        populateData();
//        dao.saveProductList(product);
//        product = dao.loadProductList();

        //Go to cart activity button
        Button cartButton = findViewById(R.id.cartButton);
        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                //Initializes Cart Activity on "cartButton" button click
                myCart();
            }
        });

        //Load RecyclerView ProductListFragment as default
        loadPLFrag();

        //Activity Launcher for CartActivity which expects updated ArrayList "cartList" of type "Product"
        cartProductActivityLauncher();
    }

    //Loading Default ProductListFragment and further Transaction between ProductDetailFragment
    public void loadPLFrag()
    {
        recyclerFrag = new ProductListFragment();
        Bundle argument = new Bundle();
        argument.putParcelableArrayList("Product", product);
        recyclerFrag.setArguments(argument);
        //RecyclerView Fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.recycleFrag, recyclerFrag);
        ft.commit();
    }
    public void replacePLFrag()
    {
        recyclerFrag = new ProductListFragment();
        Bundle argument = new Bundle();
        argument.putParcelableArrayList("Product",product);
        recyclerFrag.setArguments(argument);
        //RecyclerView Fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.recycleFrag,recyclerFrag);
        ft.commit();
    }

    //CartActivityLauncher
    public void cartProductActivityLauncher()
    {
        myCartActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            //Expecting updated cartProduct in case of addition and removal on Cart Activity
                                cartProduct = daoCart.loadCartProductList();
                        }
                    }
                }
        );
    }

    //On cartButton click go to CartActivity with cartProduct list
    public void myCart() {
        //If there is least one item exists in the cart then go to CartActivity
        if(cartProduct.isEmpty()) {
            Intent intent = new Intent(this, EmptyCart.class);
            startActivity(intent);
        }
        //Else go to EmptyCartActivity
        else {
            Intent intent = new Intent(this, Cart.class);
            myCartActivityLauncher.launch(intent);
        }
    }

    //Saving Product in the cartProduct using daoCart (Data Access Object)
    @Override
    public void addToCart(int index) {
        //Replicating finish() functionality in case of Fragment just like Activity Launch
        if(!isMulti()) {
            replacePLFrag();
        }
        daoCart.saveCartProduct(product.get(index).productName);
        //Loading cartProduct to distinguish the case either to move to EmptyCartActivity or Launch CartActivity
        cartProduct = daoCart.loadCartProductList();
    }

    //Interface implemented as making RecyclerView Views clickable
    //launching go to ProductDetailActivity with relevant data
    //to Launch ProductDetailFragment
    @Override
    public void onProductClickMA(int position) {
        detailFrag = new ProductDetailFragment();
        Bundle argument = new Bundle();
        argument.putParcelable("ProductItem", product.get(position));
        argument.putInt("Position", position);
        detailFrag.setArguments(argument);
        //RecyclerView Fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if(!isMulti()) {
            ft.replace(R.id.recycleFrag, detailFrag);
        }
        else
        {
            ft.replace(R.id.detailFrag, detailFrag);
        }
        ft.addToBackStack(null);
        ft.commit();
    }

    //Setting Instance in Bundle to retrieve in case of State change or OS kills the process
    @Override
    protected void onSaveInstanceState(@NonNull Bundle state) {
        super.onSaveInstanceState(state);
        state.putSerializable("product",product);
        state.putSerializable("cartProduct",cartProduct);
    }

    //Getting Value of Landscape mode in boolean
    public boolean isMulti() {
        return getResources().getBoolean(R.bool.multiPane);
    }

    //Populating dummy data
    public void populateData() {
        if (product.isEmpty()) {
            product.add(new Product("Nike Shoe", "The product is for some specific use and launched by a specific brand. It has many features regardless of it's price. It is an affordable product verified by the authentic retailers.", 500, R.drawable.a));
            product.add(new Product("Summer Menswear", "The product is for some specific use and launched by a specific brand. It has many features regardless of it's price. It is an affordable product verified by the authentic retailers.", 250, R.drawable.d));
            product.add(new Product("Hand Bag", "The product is for some specific use and launched by a specific brand. It has many features regardless of it's price. It is an affordable product verified by the authentic retailers.", 750, R.drawable.c));
            product.add(new Product("Adidas Shoe", "The product is for some specific use and launched by a specific brand. It has many features regardless of it's price. It is an affordable product verified by the authentic retailers.", 250, R.drawable.e));
            product.add(new Product("Summer Fashion", "The product is for some specific use and launched by a specific brand. It has many features regardless of it's price. It is an affordable product verified by the authentic retailers.", 1500, R.drawable.f));
            product.add(new Product("Men's Jacket", "The product is for some specific use and launched by a specific brand. It has many features regardless of it's price. It is an affordable product verified by the authentic retailers.", 800, R.drawable.g));
            product.add(new Product("Apple", "The product is for some specific use and launched by a specific brand. It has many features regardless of it's price. It is an affordable product verified by the authentic retailers.", 10, R.drawable.h));
            product.add(new Product("Puma Shoe", "The product is for some specific use and launched by a specific brand. It has many features regardless of it's price. It is an affordable product verified by the authentic retailers.", 350, R.drawable.b));
            product.add(new Product("Strawberry", "The product is for some specific use and launched by a specific brand. It has many features regardless of it's price. It is an affordable product verified by the authentic retailers.", 25, R.drawable.i));
            product.add(new Product("Toy Car", "The product is for some specific use and launched by a specific brand. It has many features regardless of it's price. It is an affordable product verified by the authentic retailers.", 15, R.drawable.j));
            product.add(new Product("Color Tie", "The product is for some specific use and launched by a specific brand. It has many features regardless of it's price. It is an affordable product verified by the authentic retailers.", 80, R.drawable.k));
            product.add(new Product("Football", "The product is for some specific use and launched by a specific brand. It has many features regardless of it's price. It is an affordable product verified by the authentic retailers.", 125, R.drawable.l));
            product.add(new Product("Laptop", "The product is for some specific use and launched by a specific brand. It has many features regardless of it's price. It is an affordable product verified by the authentic retailers.", 2500, R.drawable.m));
            product.add(new Product("School Bag", "The product is for some specific use and launched by a specific brand. It has many features regardless of it's price. It is an affordable product verified by the authentic retailers.", 25, R.drawable.n));
        }
    }
}