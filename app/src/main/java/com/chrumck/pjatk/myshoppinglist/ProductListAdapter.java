package com.chrumck.pjatk.myshoppinglist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListViewHolder> {

    private List<Product> products;
    private Context context;

    public ProductListAdapter(List<Product> products, Context context) {
        this.products = products;
        this.context = context;
    }

    @Override
    public ProductListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.product_list_item, parent, false);

        return new ProductListViewHolder(view, this.context);
    }

    @Override
    public void onBindViewHolder(ProductListViewHolder viewHolder, int position) {
        viewHolder.bind(products.get(position));
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}