package com.wouterhabets.superdim;

import android.Manifest;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class DimService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {

    private WindowManager windowManager;
    private FrameLayout dimView;
    private SharedPreferences sharedPrefs;

    public DimService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (checkSelfPermission(Manifest.permission.SYSTEM_ALERT_WINDOW)
                != PackageManager.PERMISSION_GRANTED) {
            createPermissionNotification();
        } else {
            startDim();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dimView != null) windowManager.removeView(dimView);
        sharedPrefs.unregisterOnSharedPreferenceChangeListener(this);
        dismissNotification(NOTIFY_ID_DIM);
    }

    private void startDim() {
        sharedPrefs = getSharedPreferences(ActivityMain.PREF_NAME, Context.MODE_PRIVATE);
        int level = sharedPrefs.getInt(ActivityMain.PREF_LEVEL, 0);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        dimView = new FrameLayout(this) {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent ev) {
                return true;
            }
        };

        FrameLayout.LayoutParams dimParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dimView.setLayoutParams(dimParams);
        dimView.setBackgroundColor(Color.parseColor(Utils.toHex(level)));

        WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        windowManager.addView(dimView, windowParams);
        createDimNotification(level);

        sharedPrefs.registerOnSharedPreferenceChangeListener(this);
    }

    private static final int NOTIFY_ID_DIM = 1001;
    private static final int NOTIFY_ID_PERMISSION = 1002;

    private void createDimNotification(int level) {
        Intent viewIntent = new Intent(this, ActivityMain.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Action actionDisable = new NotificationCompat.Action(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Stop",
                PendingIntent.getBroadcast(this, 0, new Intent(getResources().getString(R.string.action_stop)), 0));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("SuperDim dimming")
                .setContentText(String.format(getResources().getString(R.string.dim_level), level))
//                .setContentText(Utils.toHex(level))
                .setContentIntent(viewPendingIntent)
                .addAction(actionDisable)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true);

        NotificationManagerCompat.from(this).notify(NOTIFY_ID_DIM, builder.build());
    }

    private void createPermissionNotification() {
        Intent viewIntent = new Intent(this, ActivityMain.class);
        PendingIntent viewPendingIntent = PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("SuperDim permission")
                .setContentText("SuperDim requires a permission to dim the screen. Tap to grant.")
                .setContentIntent(viewPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setOngoing(false);

        NotificationManagerCompat.from(this).notify(NOTIFY_ID_PERMISSION, builder.build());
    }

    private void dismissNotification(int id) {
        NotificationManagerCompat.from(this).cancel(id);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(ActivityMain.PREF_LEVEL)) {
            int level = sharedPreferences.getInt(key, 0);
            createDimNotification(level);
            if (dimView != null)
                dimView.setBackgroundColor(Color.parseColor(Utils.toHex(level)));
        }
    }

    public static boolean isItRunning(Context context) {
        Class<?> serviceClass = DimService.class;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
