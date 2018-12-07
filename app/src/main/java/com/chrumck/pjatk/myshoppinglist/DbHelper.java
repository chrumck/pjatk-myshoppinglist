package com.chrumck.pjatk.myshoppinglist;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DbHelper {
    public enum COLUMN_NAME {id, name, price, quantity, bought}

    private static GenericTypeIndicator<Map<String, Product>> ListOfProductsType =
            new GenericTypeIndicator<Map<String, Product>>() {
            };

    private final Context context;
    private final DatabaseReference dbReference;

    DbHelper(Context context) {
        this.context = context;

        dbReference = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.firebase_ref_products));
    }

    public CompletableFuture<List<Product>> getAllProducts() {

        CompletableFuture<List<Product>> result = new CompletableFuture();

        dbReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        result.complete(new ArrayList(dataSnapshot.getValue(ListOfProductsType).values()));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("TAG:", "Failed to read value.", error.toException());
                        result.cancel(false);
                    }
                });

        return result;
    }

    public CompletableFuture<Product> getSingleProduct(String id) {
        if (id == null) {
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<Product> result = new CompletableFuture();

        dbReference.child(id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot == null || snapshot.getValue() == null) {
                            result.complete(null);
                        }

                        result.complete(snapshot.getValue(Product.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Log.e("TAG:", "Failed to read value.", error.toException());
                        result.cancel(false);
                    }
                });

        return result;
    }

    public CompletableFuture<Product> upsertProduct(Product product) {
        return getSingleProduct(product.id).thenCompose(saved -> {
            if (saved == null) product.id = dbReference.push().getKey();

            CompletableFuture<Product> result = new CompletableFuture();
            dbReference.child(product.id).setValue(product, (error, ref) -> {
                if (error == null) {
                    if (saved == null) broadcastProductCreated(product);
                    result.complete(product);
                } else {
                    result.cancel(false);
                }
            });

            return result;
        });
    }

    public CompletableFuture deleteProduct(String productId) {
        CompletableFuture result = new CompletableFuture();

        dbReference.child(productId).removeValue((error, ref) -> {
            if (error == null) result.complete(null);
            else result.cancel(false);
        });

        return result;
    }

    private void broadcastProductCreated(Product product) {
        Intent intent = new Intent(context.getResources().getString(R.string.app_action_product_created));
        intent.putExtra(COLUMN_NAME.id.toString(), product.id);
        intent.putExtra(COLUMN_NAME.name.toString(), product.name);
        intent.putExtra(COLUMN_NAME.quantity.toString(), product.quantity);
        context.sendBroadcast(intent, context.getResources().getString(R.string.app_permission_broadcast));

        Log.i("DbHelper", "broadcast intent: " + intent.getAction());
    }
}