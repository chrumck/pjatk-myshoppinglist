package com.chrumck.pjatk.myshoppinglist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ShopListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Context context;
    private Shop shop;

    private TextView name;
    private TextView description;
    private TextView radius;

    public ShopListViewHolder(View view, Context context) {
        super(view);

        this.context = context;

        name = view.findViewById(R.id.shop_list_name);
        description = view.findViewById(R.id.shop_list_description);
        radius = view.findViewById(R.id.shop_list_radius);

        view.setOnClickListener(this);
    }

    public void bind(Shop shop) {
        this.shop = shop;

        name.setText(shop.name);
        description.setText(shop.description);
        if (shop.location != null) radius.setText(Float.toString(shop.location.radius));
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this.context, ShopEditActivity.class);
        intent.putExtra(DbHelper.COLUMN_NAME.id.toString(), this.shop.id);
        context.startActivity(intent);
    }
}