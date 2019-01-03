package com.chrumck.pjatk.myshoppinglist;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DbHelper {
    public enum COLUMN_NAME {id, name, price, quantity, bought}

    private static GenericTypeIndicator<Map<String, Product>> ListOfProductsType =
            new GenericTypeIndicator<Map<String, Product>>() {
            };

    private static GenericTypeIndicator<Map<String, Shop>> ListOfShopsType =
            new GenericTypeIndicator<Map<String, Shop>>() {
            };

    private final Context context;
    private final DatabaseReference productsDbReference;
    private final DatabaseReference shopsDbReference;

    DbHelper(Context context) {
        this.context = context;

        productsDbReference = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.firebase_ref_products));

        shopsDbReference = FirebaseDatabase.getInstance().getReference()
                .child(context.getString(R.string.firebase_ref_shops));
    }

    public CompletableFuture<List<Product>> getAllProducts() {

        CompletableFuture<List<Product>> result = new CompletableFuture<>();

        productsDbReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        result.complete(new ArrayList(dataSnapshot.getValue(ListOfProductsType).values()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
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

        CompletableFuture<Product> result = new CompletableFuture<>();

        productsDbReference.child(id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            result.complete(null);
                        }

                        result.complete(snapshot.getValue(Product.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("TAG:", "Failed to read value.", error.toException());
                        result.cancel(false);
                    }
                });

        return result;
    }

    public CompletableFuture<Product> upsertProduct(Product product) {
        return getSingleProduct(product.id).thenCompose(saved -> {
            if (saved == null) product.id = productsDbReference.push().getKey();

            CompletableFuture<Product> result = new CompletableFuture<>();
            productsDbReference.child(product.id).setValue(product, (error, ref) -> {
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

        productsDbReference.child(productId).removeValue((error, ref) -> {
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

    public CompletableFuture<List<Shop>> getAllShops() {

        CompletableFuture<List<Shop>> result = new CompletableFuture<>();

        shopsDbReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        result.complete(new ArrayList(dataSnapshot.getValue(ListOfShopsType).values()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("TAG:", "Failed to read value.", error.toException());
                        result.cancel(false);
                    }
                });

        return result;
    }

    public CompletableFuture<Shop> getSingleShop(String id) {
        if (id == null) {
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<Shop> result = new CompletableFuture<>();

        shopsDbReference.child(id).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getValue() == null) {
                            result.complete(null);
                        }

                        result.complete(snapshot.getValue(Shop.class));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("TAG:", "Failed to read value.", error.toException());
                        result.cancel(false);
                    }
                });

        return result;
    }

    public CompletableFuture<Shop> upsertShop(Shop shop) {
        return getSingleShop(shop.id).thenCompose(saved -> {
            if (saved == null) shop.id = shopsDbReference.push().getKey();

            CompletableFuture<Shop> result = new CompletableFuture<>();

            shopsDbReference.child(shop.id).setValue(shop, (error, ref) -> {
                if (error == null) {
                    result.complete(shop);
                } else {
                    result.cancel(false);
                }
            });

            return result;
        });
    }

    public CompletableFuture deleteShop(String shopId) {
        CompletableFuture result = new CompletableFuture();

        shopsDbReference.child(shopId).removeValue((error, ref) -> {
            if (error == null) result.complete(null);
            else result.cancel(false);
        });

        return result;
    }
}