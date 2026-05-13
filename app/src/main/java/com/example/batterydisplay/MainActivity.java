package com.example.batterydisplay;

import android.app.Activity;
import android.os.Bundle;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.Looper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextView batteryText;
    private TextView timeText;
    private TextView chargeText;
    private TextView batteryIconText;

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

        TextView titleText = new TextView(this);
        titleText.setText("BATTERY DISPLAY");
        titleText.setTextSize(24);
        titleText.setGravity(Gravity.CENTER);
        titleText.setTextColor(Color.WHITE);
        titleText.setTypeface(Typeface.DEFAULT_BOLD);
        titleText.setLetterSpacing(0.08f);

        LinearLayout batteryRow = new LinearLayout(this);
        batteryRow.setOrientation(LinearLayout.HORIZONTAL);
        batteryRow.setGravity(Gravity.CENTER);

        batteryIconText = new TextView(this);
        batteryIconText.setText("🔋");
        batteryIconText.setTextSize(42);
        batteryIconText.setGravity(Gravity.CENTER);
        batteryIconText.setTextColor(Color.WHITE);
        batteryIconText.setTypeface(Typeface.DEFAULT_BOLD);
        batteryIconText.setPadding(0, 0, 18, 0);

        batteryText = new TextView(this);
        batteryText.setTextSize(96);
        batteryText.setGravity(Gravity.CENTER);
        batteryText.setTextColor(Color.WHITE);
        batteryText.setTypeface(Typeface.DEFAULT_BOLD);

        batteryRow.addView(batteryIconText);
        batteryRow.addView(batteryText);

        timeText = new TextView(this);
        timeText.setTextSize(34);
        timeText.setGravity(Gravity.CENTER);
        timeText.setTextColor(Color.WHITE);
        timeText.setPadding(0, 18, 0, 8);

        chargeText = new TextView(this);
        chargeText.setTextSize(32);
        chargeText.setGravity(Gravity.CENTER);
        chargeText.setTextColor(Color.WHITE);
        chargeText.setTypeface(Typeface.DEFAULT_BOLD);
        chargeText.setPadding(0, 8, 0, 28);

        Button exitButton = new Button(this);
        exitButton.setText("✕ EXIT");
        exitButton.setTextSize(18);
        exitButton.setTextColor(Color.WHITE);
        exitButton.setTypeface(Typeface.DEFAULT_BOLD);
        exitButton.setAllCaps(false);
        exitButton.setPadding(36, 12, 36, 12);
        exitButton.setBackground(makeExitButtonBackground());
        exitButton.setOnClickListener(v -> finishAndRemoveTask());

        root.addView(titleText);
        root.addView(batteryRow);
        root.addView(timeText);
        root.addView(chargeText);
        root.addView(exitButton);

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
            timeText.setText("🕒 --:--:--");
            timeText.setTextColor(Color.WHITE);
            chargeText.setText("⚡ CHARGE UNKNOWN");
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
                "🕒 " + new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date())
        );
        timeText.setTextColor(Color.WHITE);

        if (charging) {
            chargeText.setText("⚡ CHARGE ON");
            chargeText.setTextColor(Color.WHITE);
        } else {
            chargeText.setText("⚡ CHARGE OFF");
            chargeText.setTextColor(Color.RED);
        }
    }

    private GradientDrawable makeExitButtonBackground() {
        GradientDrawable shape = new GradientDrawable();
        shape.setColor(Color.rgb(40, 40, 40));
        shape.setCornerRadius(28f);
        shape.setStroke(2, Color.WHITE);
        return shape;
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
