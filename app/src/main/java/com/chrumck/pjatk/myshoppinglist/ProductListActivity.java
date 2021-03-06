package com.chrumck.pjatk.myshoppinglist;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends BaseActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.product_list_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String addEnabledSettingKey = getResources().getString(R.string.appSettings_allowProductAdd);
        boolean isAddEnabled = preferences.getBoolean(addEnabledSettingKey, true);

        FloatingActionButton fab = findViewById(R.id.fab);
        if (isAddEnabled) {
            fab.setOnClickListener(v -> startActivity(new Intent(this, ProductEditActivity.class)));
        } else {
            fab.setEnabled(false);
            int disabledColor = ResourcesCompat.getColor(getResources(), R.color.buttonDisabled, null);
            fab.setBackgroundTintList(ColorStateList.valueOf(disabledColor));
        }


        recyclerView = findViewById(R.id.product_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        List<Product> allProducts = new ArrayList<>();
        ProductListAdapter adapter = new ProductListAdapter(allProducts, this);
        recyclerView.setAdapter(adapter);

        showProgressDialog("Loading list...");
        new DbHelper(this).getAllProducts().thenAccept(products -> {
            adapter.products = products;
            adapter.notifyDataSetChanged();
            hideProgressDialog();
        });
    }
}
