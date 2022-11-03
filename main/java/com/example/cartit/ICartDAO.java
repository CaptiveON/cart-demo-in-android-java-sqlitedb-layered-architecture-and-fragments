package com.example.cartit;

import java.util.ArrayList;
import java.util.Hashtable;

public interface ICartDAO {
    void saveCartProduct(String inCartProductNAME);
    void removeCartProduct(String name);
    ArrayList<Product> loadCartProductList();
}
