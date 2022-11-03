package com.example.cartit;

import java.util.ArrayList;

public interface IProductDAO {
    void saveProduct(Product inProduct);
    void saveProductList(ArrayList<Product> products);
    Product loadProduct(String id);
    ArrayList<Product> loadProductList();
}
