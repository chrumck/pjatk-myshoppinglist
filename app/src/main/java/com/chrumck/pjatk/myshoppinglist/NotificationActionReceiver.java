package com.chrumck.pjatk.myshoppinglist;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class NotificationActionReceiver extends BroadcastReceiver {

    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("NotificationActionRec", "received intent: " +
                intent.getAction() + " id:" + intent.getIntExtra(NOTIFICATION_ID, 0));

        context.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        NotificationManager notifManager = (NotificationManager) context.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancel(intent.getIntExtra(NOTIFICATION_ID, 0));

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
