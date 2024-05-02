package com.risky.monospace;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.risky.monospace.fragment.DrawerFragment;
import com.risky.monospace.fragment.GreetFragment;
import com.risky.monospace.receiver.InstallReceiver;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ConstraintLayout mainPanel;
    private TextView month;
    private TextView dom;
    private TextView time;
    private TextView mer;

    private TextView sun;
    private TextView mon;
    private TextView tue;
    private TextView wed;
    private TextView thu;
    private TextView fri;
    private TextView sat;
    private LinearLayout contentFragment;

    private final SimpleDateFormat domFormat = new SimpleDateFormat("dd");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
    private final SimpleDateFormat merFormat = new SimpleDateFormat("a");
    private final SimpleDateFormat monthFormat = new SimpleDateFormat("MMM");

    private InstallReceiver installReceiver;

    private boolean isHome = true;

    // For swipe detection
    private float y1, y2;
    static final int MIN_DISTANCE = 200;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Get elements
        mainPanel = findViewById(R.id.main);
        month = findViewById(R.id.month_main);
        dom = findViewById(R.id.dom_main);
        time = findViewById(R.id.time_main);
        mer = findViewById(R.id.mer_main);
        sun = findViewById(R.id.dow_sun);
        mon = findViewById(R.id.dow_mon);
        tue = findViewById(R.id.dow_tue);
        wed = findViewById(R.id.dow_wed);
        thu = findViewById(R.id.dow_thu);
        fri = findViewById(R.id.dow_fri);
        sat = findViewById(R.id.dow_sat);
        contentFragment = findViewById(R.id.fragment_container);

        // ###  Clock control ###
        updateTime();

        Handler handler = new Handler();
        handler.postDelayed(this::updateTime, 1000);

        // ### Greeter ###
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new GreetFragment(this))
                .commit();

        // ### Content fragment ###
        contentFragment.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    y1 = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    y2 = event.getY();
                    float delta = y2 - y1;

                    if (delta < -MIN_DISTANCE) {
                        isHome = false;
                        setBackgroundColor(true);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_anim, R.anim.fade_out_anim)
                                .replace(R.id.fragment_container, new DrawerFragment(this))
                                .commit();
                    }

                    break;
            }

            return true;
        });

        // ### Read installed apps ###
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        installReceiver = new InstallReceiver(this);
        registerReceiver(installReceiver, intentFilter);

        // ### Back ###
        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isHome) {
                    isHome = true;
                    setBackgroundColor(false);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setCustomAnimations(R.anim.fade_in_anim, R.anim.fade_out_anim)
                            .replace(R.id.fragment_container, new GreetFragment(MainActivity.this))
                            .commit();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);

        // TODO: ASK FOR NOTIFICATION PERMISSION
        // Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
        // startActivity(intent);
    }

    @Override
    protected void onPause() {
        getOnBackPressedDispatcher().onBackPressed();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getAttributes().layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        View decorView = getWindow().getDecorView();
        // Hide both the navigation bar and the status bar.
        // SYSTEM_UI_FLAG_FULLSCREEN is only available on Android 4.1 and higher, but as
        // a general rule, you should design your app to hide the status bar whenever you
        // hide the navigation bar.
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(installReceiver);
    }

    @SuppressLint("SetTextI18n")
    private void updateTime() {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());

        dom.setText(domFormat.format(calendar.getTime()));
        time.setText(timeFormat.format(calendar.getTime()));
        mer.setText(merFormat.format(calendar.getTime()));
        month.setText(monthFormat.format(calendar.getTime()));

        int dow = calendar.get(Calendar.DAY_OF_WEEK);
        mon.setBackgroundResource(dow == 2 ? R.drawable.round : 0);
        mon.setTextColor(dow == 2 ? ContextCompat.getColor(this, R.color.black)
                : ContextCompat.getColor(this, R.color.white));
        tue.setBackgroundResource(dow == 3 ? R.drawable.round : 0);
        tue.setTextColor(dow == 3 ? ContextCompat.getColor(this, R.color.black)
                : ContextCompat.getColor(this, R.color.white));
        wed.setBackgroundResource(dow == 4 ? R.drawable.round : 0);
        wed.setTextColor(dow == 4 ? ContextCompat.getColor(this, R.color.black)
                : ContextCompat.getColor(this, R.color.white));
        thu.setBackgroundResource(dow == 5 ? R.drawable.round : 0);
        thu.setTextColor(dow == 5 ? ContextCompat.getColor(this, R.color.black)
                : ContextCompat.getColor(this, R.color.white));
        fri.setBackgroundResource(dow == 6 ? R.drawable.round : 0);
        fri.setTextColor(dow == 6 ? ContextCompat.getColor(this, R.color.black)
                : ContextCompat.getColor(this, R.color.white));
        sat.setBackgroundResource(dow == 7 ? R.drawable.round : 0);
        sat.setTextColor(dow == 7 ? ContextCompat.getColor(this, R.color.black)
                : ContextCompat.getColor(this, R.color.white));
        sun.setBackgroundResource(dow == 1 ? R.drawable.round : 0);
        sun.setTextColor(dow == 1 ? ContextCompat.getColor(this, R.color.black)
                : ContextCompat.getColor(this, R.color.white));
    }

    private void setBackgroundColor(boolean reversed) {
        int colorMain = ContextCompat.getColor(MainActivity.this, R.color.bg_black_blur);
        int colorSub = ContextCompat.getColor(MainActivity.this, R.color.bg_dark_black_blur);

        ValueAnimator colorAnimation = null;
        if (reversed) {
            colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorMain, colorSub);
        } else {
            colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorSub, colorMain);
        }
        colorAnimation.setDuration(200); // milliseconds
        colorAnimation.addUpdateListener(animator ->
                mainPanel.setBackgroundColor((int) animator.getAnimatedValue()));
        colorAnimation.start();
    }
}