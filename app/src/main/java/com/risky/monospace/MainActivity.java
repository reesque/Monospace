package com.risky.monospace;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import com.risky.monospace.dialog.DialogType;
import com.risky.monospace.fragment.DrawerFragment;
import com.risky.monospace.fragment.GreetFragment;
import com.risky.monospace.gesture.HomeGestureListener;
import com.risky.monospace.model.BluetoothStatus;
import com.risky.monospace.model.LocationStatus;
import com.risky.monospace.model.NetworkStatus;
import com.risky.monospace.model.Notification;
import com.risky.monospace.receiver.AirpodReceiver;
import com.risky.monospace.receiver.AlarmReceiver;
import com.risky.monospace.receiver.AppPackageReceiver;
import com.risky.monospace.receiver.BatteryReceiver;
import com.risky.monospace.receiver.BluetoothReceiver;
import com.risky.monospace.receiver.CalendarReceiver;
import com.risky.monospace.receiver.LocationReceiver;
import com.risky.monospace.receiver.NetworkStateMonitor;
import com.risky.monospace.receiver.NotificationReceiver;
import com.risky.monospace.receiver.TimeReceiver;
import com.risky.monospace.service.BluetoothService;
import com.risky.monospace.service.DialogService;
import com.risky.monospace.service.LocationService;
import com.risky.monospace.service.NetworkService;
import com.risky.monospace.service.NotificationService;
import com.risky.monospace.service.WeatherService;
import com.risky.monospace.service.subscribers.BatterySubscriber;
import com.risky.monospace.service.subscribers.BluetoothSubscriber;
import com.risky.monospace.service.subscribers.LocationSubscriber;
import com.risky.monospace.service.subscribers.NetworkSubscriber;
import com.risky.monospace.service.subscribers.NotificationSubscriber;
import com.risky.monospace.util.AirpodBroadcastParam;
import com.risky.monospace.util.DTFormattertUtil;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements BatterySubscriber, NetworkSubscriber, BluetoothSubscriber,
        LocationSubscriber, NotificationSubscriber {
    private static Runnable clockRunner;
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
    private TextView battPerc;
    private ImageView battCharge;
    private ProgressBar battery;
    private ImageView network;
    private ImageView bluetooth;
    private ImageView location;
    private TextView notificationCount;
    private LinearLayout contentFragment;
    private BatteryReceiver batteryReceiver;
    private AppPackageReceiver appPackageReceiver;
    private NetworkStateMonitor networkMonitor;
    private BluetoothReceiver bluetoothReceiver;
    private LocationReceiver locationReceiver;
    private AirpodReceiver airpodReceiver;
    private TimeReceiver timeReceiver;
    private AlarmReceiver alarmReceiver;
    private Intent notificationReceiver;
    private CalendarReceiver calendarReceiver;
    private boolean isHome = true;

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
        battPerc = findViewById(R.id.battery_perc_main);
        battCharge = findViewById(R.id.battery_charge_main);
        battery = findViewById(R.id.battery_main);
        network = findViewById(R.id.network_main);
        bluetooth = findViewById(R.id.bluetooth_main);
        location = findViewById(R.id.location_main);
        contentFragment = findViewById(R.id.fragment_container);
        notificationCount = findViewById(R.id.notification_count);

        // ###  Clock control ###
        clockRunner = () -> {
            Calendar calendar = Calendar.getInstance(Locale.getDefault());

            dom.setText(DTFormattertUtil.dayOfMonth.format(calendar.getTime()));
            time.setText(DTFormattertUtil.time.format(calendar.getTime()));
            mer.setText(DTFormattertUtil.meridiem.format(calendar.getTime()));
            month.setText(DTFormattertUtil.month.format(calendar.getTime()));

            int dow = calendar.get(Calendar.DAY_OF_WEEK);
            mon.setBackgroundResource(dow == 2 ? R.drawable.round_white : 0);
            mon.setTextColor(dow == 2 ? ContextCompat.getColor(MainActivity.this, R.color.black)
                    : ContextCompat.getColor(MainActivity.this, R.color.white));
            tue.setBackgroundResource(dow == 3 ? R.drawable.round_white : 0);
            tue.setTextColor(dow == 3 ? ContextCompat.getColor(MainActivity.this, R.color.black)
                    : ContextCompat.getColor(MainActivity.this, R.color.white));
            wed.setBackgroundResource(dow == 4 ? R.drawable.round_white : 0);
            wed.setTextColor(dow == 4 ? ContextCompat.getColor(MainActivity.this, R.color.black)
                    : ContextCompat.getColor(MainActivity.this, R.color.white));
            thu.setBackgroundResource(dow == 5 ? R.drawable.round_white : 0);
            thu.setTextColor(dow == 5 ? ContextCompat.getColor(MainActivity.this, R.color.black)
                    : ContextCompat.getColor(MainActivity.this, R.color.white));
            fri.setBackgroundResource(dow == 6 ? R.drawable.round_white : 0);
            fri.setTextColor(dow == 6 ? ContextCompat.getColor(MainActivity.this, R.color.black)
                    : ContextCompat.getColor(MainActivity.this, R.color.white));
            sat.setBackgroundResource(dow == 7 ? R.drawable.round_white : 0);
            sat.setTextColor(dow == 7 ? ContextCompat.getColor(MainActivity.this, R.color.black)
                    : ContextCompat.getColor(MainActivity.this, R.color.white));
            sun.setBackgroundResource(dow == 1 ? R.drawable.round_white : 0);
            sun.setTextColor(dow == 1 ? ContextCompat.getColor(MainActivity.this, R.color.black)
                    : ContextCompat.getColor(MainActivity.this, R.color.white));
        };

        clockRunner.run();

        IntentFilter timeFilter = new IntentFilter();
        timeFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        timeFilter.addAction(Intent.ACTION_TIME_CHANGED);
        timeFilter.addAction(Intent.ACTION_TIME_TICK);
        timeReceiver = new TimeReceiver(clockRunner);
        registerReceiver(timeReceiver, timeFilter);

        IntentFilter alarmFilter = new IntentFilter(AlarmManager.ACTION_NEXT_ALARM_CLOCK_CHANGED);
        alarmReceiver = new AlarmReceiver(this);
        registerReceiver(alarmReceiver, alarmFilter);

        // ### Greeter ###
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, GreetFragment.newInstance())
                .commit();

        // ### Content fragment ###
        HomeGestureListener homeGestureListener = new HomeGestureListener(() -> {
            isHome = false;
            setBackgroundColor(true);
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(R.anim.slide_bottom_in_slow_anim, R.anim.fade_out_slow_anim,
                            R.anim.fade_in_slow_anim, R.anim.slide_bottom_out_slow_anim)
                    .replace(R.id.fragment_container, DrawerFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        }, () -> DialogService.getInstance().show(this, DialogType.SEARCH, null),
            () -> startActivity(new Intent(MainActivity.this, SettingsActivity.class)),
            () -> DialogService.getInstance().show(this, DialogType.NOTIFICATION, null),
            () -> DialogService.getInstance().show(this, DialogType.CALENDAR, null));
        GestureDetector gestureDetector = new GestureDetector(this, homeGestureListener);
        contentFragment.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        // ### Read network ###
        networkMonitor = new NetworkStateMonitor(this);
        networkMonitor.register();
        NetworkService.getInstance().subscribe(MainActivity.this);

        // ### Read bluetooth ###
        IntentFilter bluetoothFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        bluetoothReceiver = new BluetoothReceiver();
        registerReceiver(bluetoothReceiver, bluetoothFilter);
        BluetoothService.getInstance().subscribe(MainActivity.this);

        // ### Read location ###
        IntentFilter locationFilter = new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION);
        locationReceiver = new LocationReceiver(this);
        registerReceiver(locationReceiver, locationFilter);
        LocationService.getInstance().subscribe(MainActivity.this);

        // ### Read battery ###
        IntentFilter batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryReceiver = new BatteryReceiver(this);
        registerReceiver(batteryReceiver, batteryFilter);

        // ### Read airpod ###
        IntentFilter airpodFilter = new IntentFilter(AirpodBroadcastParam.ACTION_STATUS);
        airpodReceiver = new AirpodReceiver();
        registerReceiver(airpodReceiver, airpodFilter);

        // ### Read installed apps ###
        IntentFilter appPackageFilter = new IntentFilter();
        appPackageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        appPackageFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        appPackageFilter.addDataScheme("package");
        appPackageReceiver = new AppPackageReceiver(this);
        registerReceiver(appPackageReceiver, appPackageFilter);

        // ### Bind notification service
        notificationReceiver = new Intent(this, NotificationReceiver.class);
        startService(notificationReceiver);
        NotificationService.getInstance().subscribe(this);

        // ### Read calendar ###
        IntentFilter calendarFilter = new IntentFilter(Intent.ACTION_PROVIDER_CHANGED);
        calendarReceiver = new CalendarReceiver(this);
        registerReceiver(calendarReceiver, calendarFilter);

        // ### Back ###
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (!isHome && getSupportFragmentManager().getBackStackEntryCount() == 0) {
                isHome = true;
                setBackgroundColor(false);
                getSupportFragmentManager().popBackStack();
            }
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!isHome) {
                    isHome = true;
                    setBackgroundColor(false);
                    getSupportFragmentManager().popBackStack();
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(onBackPressedCallback);
    }

    @Override
    protected void onPause() {
        getOnBackPressedDispatcher().onBackPressed();
        DialogService.getInstance().dismissAll();

        NetworkService.getInstance().unsubscribe(this);
        BluetoothService.getInstance().unsubscribe(this);
        LocationService.getInstance().unsubscribe(this);
        NotificationService.getInstance().unsubscribe(this);

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getWindow().getAttributes().layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        NetworkService.getInstance().subscribe(this);
        BluetoothService.getInstance().subscribe(this);
        LocationService.getInstance().subscribe(this);
        NotificationService.getInstance().subscribe(this);

        WeatherService.getInstance(this).notifySubscriber();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        networkMonitor.unregister();
        unregisterReceiver(batteryReceiver);
        unregisterReceiver(appPackageReceiver);
        unregisterReceiver(bluetoothReceiver);
        unregisterReceiver(locationReceiver);
        unregisterReceiver(airpodReceiver);
        unregisterReceiver(timeReceiver);
        unregisterReceiver(alarmReceiver);
        unregisterReceiver(calendarReceiver);

        stopService(notificationReceiver);

        NetworkService.getInstance().unsubscribe(this);
        BluetoothService.getInstance().unsubscribe(this);
        LocationService.getInstance().unsubscribe(this);
        NotificationService.getInstance().unsubscribe(this);

        mainPanel = null;
        month = null;
        dom = null;
        time = null;
        mer = null;
        sun = null;
        mon = null;
        tue = null;
        wed = null;
        thu = null;
        fri = null;
        sat = null;
        battPerc = null;
        battCharge = null;
        battery = null;
        network = null;
        bluetooth = null;
        location = null;
        contentFragment = null;
    }

    private void setBackgroundColor(boolean reversed) {
        int colorMain = ContextCompat.getColor(MainActivity.this, R.color.bg_black_blur);
        int colorSub = ContextCompat.getColor(MainActivity.this, R.color.bg_dark_black_blur);

        ValueAnimator colorAnimation;
        if (reversed) {
            colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorMain, colorSub);
        } else {
            colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorSub, colorMain);
        }
        colorAnimation.setDuration(300); // milliseconds
        colorAnimation.addUpdateListener(animator ->
                mainPanel.setBackgroundColor((int) animator.getAnimatedValue()));
        colorAnimation.start();
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void update(int level, boolean isCharging, boolean isFull) {
        if (isCharging) {
            battCharge.setVisibility(View.VISIBLE);
            battPerc.setVisibility(View.GONE);
        } else {
            battCharge.setVisibility(View.GONE);
            battPerc.setVisibility(View.VISIBLE);
            battPerc.setText(String.format("%d", level));
        }

        battery.setProgress(level);
    }

    @Override
    public void update(NetworkStatus status) {
        switch (status) {
            case UNAVAILABLE:
                network.setImageResource(R.drawable.no_connection_gray);
                break;
            case WIFI:
                network.setImageResource(R.drawable.wifi_black);
                break;
            case MOBILE_DATA:
                network.setImageResource(R.drawable.mobile_data_black);
                break;
        }
    }

    @Override
    public void update(BluetoothStatus status) {
        switch (status) {
            case UNAVAILABLE:
                bluetooth.setImageResource(R.drawable.bluetooth_disable_gray);
                break;
            case ON:
                bluetooth.setImageResource(R.drawable.bluetooth_black);
                break;
        }
    }

    @Override
    public void update(LocationStatus status) {
        switch (status) {
            case UNAVAILABLE:
                location.setImageResource(R.drawable.location_disable_gray);
                break;
            case ON:
                location.setImageResource(R.drawable.location_black);
                break;
        }
    }

    @Override
    public void update(List<Notification> notifications) {
        notificationCount.setText(String.valueOf(notifications.size()));
    }
}