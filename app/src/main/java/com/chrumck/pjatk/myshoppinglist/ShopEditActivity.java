package com.chrumck.pjatk.myshoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

public class ShopEditActivity extends BaseActivity {
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DbHelper(this);

        setContentView(R.layout.shop_edit_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText name = findViewById(R.id.edit_shop_name);
        EditText description = findViewById(R.id.edit_shop_description);
        EditText latitude = findViewById(R.id.edit_shop_latitude);
        EditText longitude = findViewById(R.id.edit_shop_longitude);
        EditText radius = findViewById(R.id.edit_shop_radius);


        Intent intent = getIntent();
        String shopId = intent.getStringExtra(DbHelper.COLUMN_NAME.id.toString());

        if (shopId != null) {
            showProgressDialog("Loading product...");
            dbHelper.getSingleShop(shopId).thenAccept(shop -> {
                hideProgressDialog();

                name.setText(shop.name);
                description.setText(shop.description);

                if (shop.location == null) {
                    return;
                }

                latitude.setText(Double.toString(shop.location.latitude));
                longitude.setText(Double.toString(shop.location.longitude));
                radius.setText(Float.toString(shop.location.radius));
            });

        } else {
            findViewById(R.id.edit_shop_btn_delete).setVisibility(View.GONE);
        }

        findViewById(R.id.edit_shop_btn_save).setOnClickListener(v -> {
            showProgressDialog("Saving...");
            Shop newShop = new Shop(
                    shopId,
                    name.getText().toString(),
                    description.getText().toString());

            if (!TextUtils.isEmpty(latitude.getText()) &&
                    !TextUtils.isEmpty(longitude.getText()) &&
                    !TextUtils.isEmpty(radius.getText())) {

                newShop.location = new Shop.ShopLocation(
                        Double.parseDouble(longitude.getText().toString()),
                        Double.parseDouble(latitude.getText().toString()),
                        Float.parseFloat(radius.getText().toString()));
            }

            dbHelper.upsertShop(newShop).thenRun(() -> {
                hideProgressDialog();
                startActivity(new Intent(this, ShopListActivity.class));
            });

        });

        findViewById(R.id.edit_shop_btn_delete).setOnClickListener(v -> {
            showProgressDialog("Deleting...");
            dbHelper.deleteShop(shopId).thenRun(() -> {
                hideProgressDialog();
                startActivity(new Intent(this, ShopListActivity.class));
            });
        });

        findViewById(R.id.edit_shop_btn_cancel).setOnClickListener(v -> finish());
    }
}
