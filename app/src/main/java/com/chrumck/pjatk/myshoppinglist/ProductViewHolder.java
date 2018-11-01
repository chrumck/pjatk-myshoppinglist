package com.chrumck.pjatk.myshoppinglist;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private Context context;

    public TextView name;
    public TextView price;
    public TextView qty;
    public CheckBox bought;

    public ProductViewHolder(View view, Context context) {
        super(view);

        this.context = context;

        name = view.findViewById(R.id.product_name);
        price = view.findViewById(R.id.product_price);
        qty = view.findViewById(R.id.product_qty);
        bought = view.findViewById(R.id.product_bought);

        view.setOnClickListener(this);

        bought.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(context, name.getText() + "is bought!", Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(this.context, "Selected item is" + name.getText(), Toast.LENGTH_LONG)
            .show();
    }
}