package com.example.batterydisplay;

import android.app.Activity;
import android.os.Bundle;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView batteryText;
    private TextView timeText;
    private TextView chargeText;

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateDisplay();
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Window window = getWindow();
        window.setStatusBarColor(Color.BLACK);
        window.setNavigationBarColor(Color.BLACK);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setBackgroundColor(Color.BLACK);
        root.setPadding(24, 24, 24, 24);

        batteryText = new TextView(this);
        batteryText.setTextSize(96);
        batteryText.setGravity(Gravity.CENTER);
        batteryText.setTextColor(Color.WHITE);

        timeText = new TextView(this);
        timeText.setTextSize(34);
        timeText.setGravity(Gravity.CENTER);
        timeText.setTextColor(Color.WHITE);

        chargeText = new TextView(this);
        chargeText.setTextSize(32);
        chargeText.setGravity(Gravity.CENTER);
        chargeText.setTextColor(Color.WHITE);

        root.addView(batteryText);
        root.addView(timeText);
        root.addView(chargeText);

        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updater.run();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updater);
    }

    private void updateDisplay() {
        Intent batteryStatus = registerReceiver(
                null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        );

        if (batteryStatus == null) {
            batteryText.setText("--%");
            batteryText.setTextColor(Color.WHITE);
            timeText.setTextColor(Color.WHITE);
            chargeText.setText("CHARGE UNKNOWN");
            chargeText.setTextColor(Color.RED);
            return;
        }

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        int percent = 0;
        if (level >= 0 && scale > 0) {
            percent = Math.round((level * 100f) / scale);
        }

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        boolean charging =
                status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        batteryText.setText(percent + "%");
        batteryText.setTextColor(getBatteryColor(percent));

        timeText.setText(
                new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date())
        );
        timeText.setTextColor(Color.WHITE);

        if (charging) {
            chargeText.setText("CHARGE ON");
            chargeText.setTextColor(Color.WHITE);
        } else {
            chargeText.setText("CHARGE OFF");
            chargeText.setTextColor(Color.RED);
        }
    }

    private int getBatteryColor(int percent) {
        if (percent < 20) {
            return Color.RED;
        } else if (percent < 70) {
            return Color.YELLOW;
        } else {
            return Color.GREEN;
        }
    }
}
