package com.chrumck.pjatk.myshoppinglist;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends BaseActivity {

    private static final long GEOFENCE_EXPIRATION = 3600000L;

    private TextView emailField;
    private TextView passwordField;

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    private GeofencingClient geofencingClient;
    private PendingIntent geofencePendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.main_activity);

        findViewById(R.id.btn_showShopsList).setOnClickListener(v ->
                startActivity(new Intent(this, ShopListActivity.class)));
        findViewById(R.id.btn_showShopsMap).setOnClickListener(v ->
                startActivity(new Intent(this, ShopMapActivity.class)));
        findViewById(R.id.btn_showShoppingList).setOnClickListener(v ->
                startActivity(new Intent(this, ProductListActivity.class)));
        findViewById(R.id.btn_showSettings).setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));

        emailField = findViewById(R.id.login_email);
        passwordField = findViewById(R.id.login_password);

        findViewById(R.id.btn_logIn).setOnClickListener(v ->
                logIn(emailField.getText().toString(), passwordField.getText().toString()));

        findViewById(R.id.btn_createAccount).setOnClickListener(v ->
                createAccount(emailField.getText().toString(), passwordField.getText().toString()));

        findViewById(R.id.btn_logOut).setOnClickListener(v -> logOut());

        IntentFilter filter = new IntentFilter();
        filter.addAction(getResources().getString(R.string.app_action_product_edit));
        filter.addAction(getResources().getString(R.string.app_action_product_list));

        registerGeofences();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        updateUI(currentUser);
    }

    @Override
    protected void onDestroy() {
        unregisterGeofences();

        super.onDestroy();
    }

    private void createAccount(String email, String password) {
        if (!validateForm()) {
            return;
        }

        showProgressDialog("Creating account...");

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                    hideProgressDialog();
                });
    }

    private void logIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        showProgressDialog("Logging in...");

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }

                    hideProgressDialog();
                });
    }

    private void logOut() {
        firebaseAuth.signOut();
        updateUI(null);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = emailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            emailField.setError("Required.");
            valid = false;
        } else {
            emailField.setError(null);
        }

        String password = passwordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            passwordField.setError("Required.");
            valid = false;
        } else {
            passwordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        hideProgressDialog();
        if (user != null) {
            TextView email = findViewById(R.id.login_email);
            email.setText(user.getEmail());
            email.setEnabled(false);
            findViewById(R.id.login_password).setVisibility(View.GONE);
            findViewById(R.id.layout_loginButtons).setVisibility(View.GONE);
            findViewById(R.id.btn_logOut).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_main).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.login_email).setEnabled(true);
            findViewById(R.id.login_password).setVisibility(View.VISIBLE);
            findViewById(R.id.layout_loginButtons).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_logOut).setVisibility(View.GONE);
            findViewById(R.id.layout_main).setVisibility(View.INVISIBLE);
        }
    }

    private void registerGeofences() {
        geofencingClient = LocationServices.getGeofencingClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            return;

        }

        new DbHelper(this).getAllShops().thenAccept(shops -> {
            List<Geofence> geofences = new ArrayList<>();
            for (Shop shop : shops) {
                if (shop.location == null) continue;

                geofences.add(new Geofence.Builder()
                        .setRequestId(shop.id)
                        .setCircularRegion(shop.location.latitude, shop.location.longitude, shop.location.radius)
                        .setExpirationDuration(GEOFENCE_EXPIRATION)
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                        .build());
            }

            geofencingClient.addGeofences(getGeofencingRequest(geofences), getGeofencePendingIntent());
        });
    }

    private void unregisterGeofences() {
        new DbHelper(this).getAllShops().thenAccept(shops -> {
            List<String> shopIds = shops.stream().map(shop -> shop.id).collect(Collectors.toList());
            geofencingClient.removeGeofences(shopIds);
        });
    }

    private GeofencingRequest getGeofencingRequest(List<Geofence> geofences) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);
        builder.addGeofences(geofences);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {

        if (geofencePendingIntent != null) return geofencePendingIntent;

        Intent intent = new Intent(this, ShopGeofenceService.class);
        geofencePendingIntent = PendingIntent.getService(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return geofencePendingIntent;
    }
}
