package com.chrumck.pjatk.myshoppinglist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationActionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("NotificationActionRec", "received intent: " + intent.getAction());

        if (intent.getAction().equals(context.getResources().getString(R.string.app_action_product_list))) {
            context.startActivity(new Intent(context, ProductListActivity.class));
        }

        if (intent.getAction().equals(context.getResources().getString(R.string.app_action_product_edit))) {
            Intent editIntent = new Intent(context, ProductEditActivity.class);
            editIntent.putExtra(DbHelper.COLUMN_NAME.id.toString(),
                    intent.getIntExtra(DbHelper.COLUMN_NAME.id.toString(), 0));
            context.startActivity(editIntent);
        }
    }
}
