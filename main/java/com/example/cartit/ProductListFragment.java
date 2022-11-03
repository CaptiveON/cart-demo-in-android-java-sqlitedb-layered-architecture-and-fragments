package com.example.cartit;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filterable;
import java.util.ArrayList;

public class ProductListFragment extends Fragment implements RecyclerProductAdapter.RecyclerProductInterface {

    ArrayList<Product> product = new ArrayList<>();
    RecyclerProductAdapter productAdapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ProductClickListener listener;
    Filterable filterable;
    EditText search;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
            if (context instanceof ProductClickListener) {
                listener = (ProductClickListener) context;
            }
    }

    public ProductListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        search = view.findViewById(R.id.searchBar);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterable.getFilter().filter(search.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if(getArguments() != null){
            product = getArguments().getParcelableArrayList("Product");
        }

        //RecyclerView to list the "product" items
        recyclerView = view.findViewById(R.id.recycleItem);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        //Adapter to bind ArrayList "product" of type "Product" as Views in the recyclerView
        productAdapter = new RecyclerProductAdapter(product,this);
        filterable = productAdapter;
        recyclerView.setAdapter(productAdapter);

        return view;
    }

    public void oneOnOne()
    {
        Log.d("FragmentMessage:","Here I am in List Fragment Custom Method!");
    }

    @Override
    public void onProductClickFA(int position) {
        listener.onProductClickMA(position);
    }

    public interface ProductClickListener{
        void onProductClickMA(int index);
    }
}