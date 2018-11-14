package com.chrumck.pjatk.myshoppinglist;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private NotificationActionReceiver notificationActionReceiver = new NotificationActionReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.main_activity);

        findViewById(R.id.btn_showShoppingList).setOnClickListener(v ->
                startActivity(new Intent(this, ProductListActivity.class)));
        findViewById(R.id.btn_showSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.app_action_product_edit));
        filter.addAction(getResources().getString(R.string.app_action_product_list));

        registerReceiver(notificationActionReceiver, filter);
        Log.i("MainActivity", "notificationActionReceiver registered");
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(notificationActionReceiver);
        Log.i("MainActivity", "notificationActionReceiver un-registered");

        super.onDestroy();
    }
}
