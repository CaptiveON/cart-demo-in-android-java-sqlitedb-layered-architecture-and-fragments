package com.example.cartit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecyclerCartAdapter extends RecyclerView.Adapter<RecyclerCartAdapter.ViewHolder>
{
    Context context;
    ArrayList<Product> cartList;
    RecyclerCartInterface recyclerCartInterface;
    RecyclerCartAdapter(Context context, ArrayList<Product> cartList,RecyclerCartInterface recyclerCartInterface){
        this.context = context;
        this.cartList = cartList;
        this.recyclerCartInterface = recyclerCartInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item,parent,false);
        return new ViewHolder(view, cartList, recyclerCartInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cartProductImage.setImageResource(cartList.get(position).productImage);
        holder.cartProductName.setText(cartList.get(position).productName);
        holder.cartProductPrice.setText(String.valueOf(cartList.get(position).productPrice));
        holder.cartItemCount.setText(String.valueOf(cartList.get(position).itemCount));
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView cartProductImage;
        TextView cartProductName;
        TextView cartProductPrice;
        Button moreItem;
        Button lessItem;
        EditText cartItemCount;
        public ViewHolder(@NonNull View itemView,ArrayList<Product> cartList, RecyclerCartInterface recyclerCartInterface) {
            super(itemView);
            cartProductImage = itemView.findViewById(R.id.cartProductImage);
            cartProductName = itemView.findViewById(R.id.cartProductName);
            cartProductPrice = itemView.findViewById(R.id.cartProductPrice);
            moreItem = itemView.findViewById(R.id.cartCountMore);
            lessItem = itemView.findViewById(R.id.cartCountLess);
            cartItemCount = itemView.findViewById(R.id.cartItemCount);

            //"+" button's OnClick implementation in the View
            moreItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //If pressed, add count by 1 and show the count on cartItemCount TextView
                    //by calling "addNumItem" method in Product class
                    cartItemCount.setText(String.valueOf(cartList.get(position).addNumItem()), TextView.BufferType.EDITABLE);
                    recyclerCartInterface.onCartItemAdd(cartList.get(position).productName);
                }});
            //"-" button's OnClick implementation in the View
            lessItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //If pressed, subtract count by 1 and show the count on cartItemCount TextView
                    //by calling "removeNumItem" method in Product class
                    cartItemCount.setText(String.valueOf(cartList.get(position).removeNumItem()), TextView.BufferType.NORMAL);
                    recyclerCartInterface.onCartItemRemove(cartList.get(position).productName,position);
                }});
        }
    }

    //Interface declaring onCartItemAdd and onCartItemRemove methods
    public interface RecyclerCartInterface{
        public void onCartItemAdd(String name);
        public void onCartItemRemove(String name, int position);
    }
}
