package com.example.cartit;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class CartViewModel extends ViewModel {
    ArrayList<Product> product;
    ArrayList<Product> cartProduct;
    IProductDAO dao;
    ICartDAO daoCart;


    //Returns saved instance holding list of product
    public ArrayList<Product> getSavedProduct(Bundle savedInstanceState,String key){
        if (product == null){
            if(savedInstanceState == null){
                if (dao != null){
                    product = dao.loadProductList();
                }
                else product = new ArrayList<Product>();
            } else {
                product = (ArrayList<Product>) savedInstanceState.get(key);
            }
        }
        return product;
    }

    //Returns saved instance holding list of cartProduct
    public ArrayList<Product> getSavedCartProduct(Bundle savedInstanceState,String key){
        if (cartProduct == null){
            if(savedInstanceState == null){
                if (daoCart != null){
                    cartProduct = daoCart.loadCartProductList();
                }
                else cartProduct = new ArrayList<Product>();
            } else {
                cartProduct = (ArrayList<Product>) savedInstanceState.get(key);
            }
        }
        return cartProduct;
    }

    public void setDao(IProductDAO d){
        dao = d;
    }

    public void setDaoCart(ICartDAO d){
        daoCart = d;
    }
}
