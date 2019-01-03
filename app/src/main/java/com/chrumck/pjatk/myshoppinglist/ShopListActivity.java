package com.chrumck.pjatk.myshoppinglist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class ShopListActivity extends BaseActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.shop_list_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = findViewById(R.id.fab_shop_list);
        fab.setOnClickListener(v -> startActivity(new Intent(this, ShopEditActivity.class)));

        recyclerView = findViewById(R.id.shop_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        List<Shop> allShops = new ArrayList<>();
        ShopListAdapter adapter = new ShopListAdapter(allShops, this);
        recyclerView.setAdapter(adapter);

        showProgressDialog("Loading list...");
        new DbHelper(this).getAllShops().thenAccept(shops -> {
            adapter.shops = shops;
            adapter.notifyDataSetChanged();
            hideProgressDialog();
        });
    }
}
