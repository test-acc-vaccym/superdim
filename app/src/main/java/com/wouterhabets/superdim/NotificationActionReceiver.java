package com.wouterhabets.superdim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class NotificationActionReceiver extends BroadcastReceiver {
    public NotificationActionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(context.getResources().getString(R.string.action_stop))) {
            SharedPreferences.Editor editor = context.getSharedPreferences(ActivityMain.PREF_NAME, Context.MODE_PRIVATE).edit();
            editor.putBoolean(ActivityMain.PREF_ENABLED, false);
            editor.apply();
            context.stopService(new Intent(context, DimService.class));
        }
    }
}
