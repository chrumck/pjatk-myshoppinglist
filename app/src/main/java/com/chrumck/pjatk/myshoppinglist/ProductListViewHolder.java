package com.chrumck.pjatk.myshoppinglist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class ProductListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static DecimalFormat formatPrice = new DecimalFormat("$#.00");

    private Context context;
    private DbHelper dbHelper;

    private Product product;

    private TextView name;
    private TextView price;
    private TextView qty;
    private CheckBox bought;

    private boolean isEditEnabled;

    public ProductListViewHolder(View view, Context context) {
        super(view);

        this.context = context;

        dbHelper = new DbHelper(context);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String editEnabledSettingKey = context.getResources().getString(R.string.appSettings_allowProductEdit);
        isEditEnabled = preferences.getBoolean(editEnabledSettingKey, true);

        name = view.findViewById(R.id.product_name);
        price = view.findViewById(R.id.product_price);
        qty = view.findViewById(R.id.product_qty);
        bought = view.findViewById(R.id.product_bought);

        view.setOnClickListener(this);

        if (!isEditEnabled) {
            bought.setEnabled(false);
            return;
        }

        bought.setOnClickListener(v -> {
            product.bought = !product.bought;
            dbHelper.upsertProduct(product);

            if (product.bought) {
                Toast.makeText(context, name.getText() + " bought!", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    public void bind(Product product) {
        this.product = product;

        name.setText(product.name);
        price.setText(formatPrice.format(product.price));
        qty.setText(Integer.toString(product.quantity));
        bought.setChecked(product.bought);
    }

    @Override
    public void onClick(View v) {
        if (!isEditEnabled) return;

        Intent intent = new Intent(this.context, ProductEditActivity.class);
        intent.putExtra(DbHelper.COLUMN_NAME.id.toString(), this.product.id);
        context.startActivity(intent);
    }
}