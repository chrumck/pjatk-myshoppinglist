package com.chrumck.pjatk.myshoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class ProductEditActivity extends BaseActivity {
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DbHelper(this);

        setContentView(R.layout.product_edit_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText name = findViewById(R.id.edit_name);
        EditText price = findViewById(R.id.edit_price);
        EditText qty = findViewById(R.id.edit_qty);
        CheckBox bought = findViewById(R.id.edit_bought);

        Intent intent = getIntent();
        String productId = intent.getStringExtra(DbHelper.COLUMN_NAME.id.toString());

        if (productId != null) {
            showProgressDialog("Loading product...");
            dbHelper.getSingleProduct(productId).thenAccept(product -> {
                name.setText(product.name);
                price.setText(Double.toString(product.price));
                qty.setText(Long.toString(product.quantity));
                if (product.bought) bought.setChecked(true);

                hideProgressDialog();
            });

        } else {
            findViewById(R.id.edit_btn_delete).setVisibility(View.GONE);
        }

        findViewById(R.id.edit_btn_save).setOnClickListener(v -> {
            showProgressDialog("Saving...");
            dbHelper.upsertProduct(new Product(
                    productId,
                    name.getText().toString(),
                    Double.parseDouble(price.getText().toString()),
                    Integer.parseInt(qty.getText().toString()),
                    bought.isChecked()
            )).thenRun(() -> {
                hideProgressDialog();
                startActivity(new Intent(this, ProductListActivity.class));
            });

        });

        findViewById(R.id.edit_btn_delete).setOnClickListener(v -> {
            showProgressDialog("Deleting...");
            dbHelper.deleteProduct(productId).thenRun(() -> {
                hideProgressDialog();
                startActivity(new Intent(this, ProductListActivity.class));
            });
        });

        findViewById(R.id.edit_btn_cancel).setOnClickListener(v -> finish());
    }
}
