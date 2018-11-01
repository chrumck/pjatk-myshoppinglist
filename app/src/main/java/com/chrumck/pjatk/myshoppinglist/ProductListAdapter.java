package com.chrumck.pjatk.myshoppinglist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.text.DecimalFormat;
import java.util.List;

public class ProductListAdapter extends RecyclerView.Adapter<ProductViewHolder> {

    private static DecimalFormat formatPrice = new DecimalFormat("$#.00");

    private List<Product> list;
    private Context context;

    public ProductListAdapter(List<Product> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.product_list_item, parent, false);

        return new ProductViewHolder(view, this.context);
    }

    @Override
    public void onBindViewHolder(ProductViewHolder viewHolder, int position) {
        Product product = list.get(position);
        viewHolder.name.setText(product.name);
        viewHolder.price.setText(formatPrice.format(product.price));
        viewHolder.qty.setText(Integer.toString(product.quantity));
        viewHolder.bought.setChecked(product.bought);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}