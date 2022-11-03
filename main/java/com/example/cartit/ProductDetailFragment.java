package com.example.cartit;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class ProductDetailFragment extends Fragment {
    AddItemToCart listener;
    ImageView productImage;
    TextView productName, productPrice, productDescription;
    Button addToCart;
    Product item;
    int index;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if(context instanceof AddItemToCart)
        {
            listener = (AddItemToCart) context;
        }
    }

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail, container, false);

        productImage = view.findViewById(R.id.ProductImage);
        productName = view.findViewById(R.id.ProductName);
        productPrice = view.findViewById(R.id.ProductPrice);
        productDescription = view.findViewById(R.id.ProductDescription);
        addToCart = view.findViewById(R.id.buyButton);

        if(getArguments() != null)
        {
            item = getArguments().getParcelable("ProductItem");

            index = getArguments().getInt("Position");
        }

        productImage.setImageResource(item.productImage);
        productName.setText(item.productName);
        productPrice.setText(String.valueOf(item.productPrice));
        productDescription.setText(item.productDescription);

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.addToCart(index);
            }
        });
        return view;
    }

    public interface AddItemToCart
    {
        void addToCart(int position);
    }
}