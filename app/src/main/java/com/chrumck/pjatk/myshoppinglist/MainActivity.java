package com.chrumck.pjatk.myshoppinglist;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity {

    private TextView emailField;
    private TextView passwordField;

    private NotificationActionReceiver notificationActionReceiver = new NotificationActionReceiver();
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setContentView(R.layout.main_activity);

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

        registerReceiver(notificationActionReceiver, filter);
        Log.i("MainActivity", "notificationActionReceiver registered");

        firebaseAuth = FirebaseAuth.getInstance();
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
        unregisterReceiver(notificationActionReceiver);
        Log.i("MainActivity", "notificationActionReceiver un-registered");

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
}
