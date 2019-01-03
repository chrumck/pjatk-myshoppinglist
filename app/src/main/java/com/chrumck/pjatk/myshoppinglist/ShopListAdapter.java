package com.chrumck.pjatk.myshoppinglist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class ShopListAdapter extends RecyclerView.Adapter<ShopListViewHolder> {

    public List<Shop> shops;
    private Context context;

    public ShopListAdapter(List<Shop> shops, Context context) {
        this.shops = shops;
        this.context = context;
    }

    @Override
    public ShopListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.shop_list_item, parent, false);

        return new ShopListViewHolder(view, this.context);
    }

    @Override
    public void onBindViewHolder(ShopListViewHolder viewHolder, int position) {
        viewHolder.bind(shops.get(position));
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }
}