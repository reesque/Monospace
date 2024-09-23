package com.risky.monospace;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.risky.monospace.dialog.DialogType;
import com.risky.monospace.fragment.GreetFragment;
import com.risky.monospace.gesture.HomeGestureListener;
import com.risky.monospace.model.AppPackage;
import com.risky.monospace.model.AppPagerAdapter;
import com.risky.monospace.model.BluetoothStatus;
import com.risky.monospace.model.LocationStatus;
import com.risky.monospace.model.NetworkStatus;
import com.risky.monospace.receiver.AirpodReceiver;
import com.risky.monospace.receiver.AlarmReceiver;
import com.risky.monospace.receiver.AppPackageReceiver;
import com.risky.monospace.receiver.BatteryReceiver;
import com.risky.monospace.receiver.BluetoothReceiver;
import com.risky.monospace.receiver.LocationReceiver;
import com.risky.monospace.receiver.NetworkStateMonitor;
import com.risky.monospace.receiver.NotificationReceiver;
import com.risky.monospace.receiver.TimeReceiver;
import com.risky.monospace.service.AppPackageService;
import com.risky.monospace.service.BluetoothService;
import com.risky.monospace.service.DialogService;
import com.risky.monospace.service.LocationService;
import com.risky.monospace.service.NetworkService;
import com.risky.monospace.service.WeatherService;
import com.risky.monospace.service.subscribers.AppPackageSubscriber;
import com.risky.monospace.service.subscribers.BatterySubscriber;
import com.risky.monospace.service.subscribers.BluetoothSubscriber;
import com.risky.monospace.service.subscribers.LocationSubscriber;
import com.risky.monospace.service.subscribers.NetworkSubscriber;
import com.risky.monospace.util.AirpodBroadcastParam;
import com.risky.monospace.util.DTFormattertUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements BatterySubscriber, NetworkSubscriber, BluetoothSubscriber, LocationSubscriber, AppPackageSubscriber {
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
    private LinearLayout contentFragment;
    private ConstraintLayout drawer;
    private BottomSheetBehavior<View> bsb;
    private AppPagerAdapter appAdapter;
    private TabLayout appPageIndicator;
    private ViewPager2 appList;
    private TabLayoutMediator tabLayoutMediator;
    private BatteryReceiver batteryReceiver;
    private AppPackageReceiver appPackageReceiver;
    private NetworkStateMonitor networkMonitor;
    private BluetoothReceiver bluetoothReceiver;
    private LocationReceiver locationReceiver;
    private AirpodReceiver airpodReceiver;
    private TimeReceiver timeReceiver;
    private AlarmReceiver alarmReceiver;
    private Intent notificationReceiver;

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
        drawer = findViewById(R.id.drawer);
        appList = findViewById(R.id.app_page);
        appPageIndicator = findViewById(R.id.app_page_indicator);

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

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(airpodReceiver, airpodFilter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(airpodReceiver, airpodFilter);
        }

        // ### Read installed apps ###
        IntentFilter appPackageFilter = new IntentFilter();
        appPackageFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        appPackageFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        appPackageFilter.addDataScheme("package");
        appPackageReceiver = new AppPackageReceiver(this);
        registerReceiver(appPackageReceiver, appPackageFilter);

        // ### App list ###
        AppPackageService.getInstance(this).subscribe(this);

        bsb = BottomSheetBehavior.from(drawer);
        bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
        drawer.setAlpha(0);
        bsb.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        appList.setVisibility(View.GONE);
                        appList.setCurrentItem(0, false);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        appList.setVisibility(View.VISIBLE);
                        contentFragment.setVisibility(View.GONE);
                        break;
                    default:
                        appList.setVisibility(View.VISIBLE);
                        contentFragment.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                contentFragment.setAlpha(1 - slideOffset);
                bottomSheet.setAlpha(slideOffset);

                mainPanel.setBackgroundColor(Color.valueOf(0.165f, 0.165f, 0.165f,
                        (1 - slideOffset) * 0.6f + slideOffset * 0.933f).toArgb());
            }
        });
        HomeGestureListener homeGestureListener = new HomeGestureListener(
                () -> DialogService.getInstance().show(this, DialogType.SEARCH, null),
                () -> DialogService.getInstance().show(this, DialogType.CALENDAR, null));
        GestureDetector gestureDetector = new GestureDetector(this, homeGestureListener);
        drawer.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        // ### Bind notification service
        notificationReceiver = new Intent(this, NotificationReceiver.class);
        startService(notificationReceiver);

        // ### Back ###
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });

        OnBackPressedCallback onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                bsb.setState(BottomSheetBehavior.STATE_COLLAPSED);
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
        AppPackageService.getInstance(this).unsubscribe(this);

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
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        NetworkService.getInstance().subscribe(this);
        BluetoothService.getInstance().subscribe(this);
        LocationService.getInstance().subscribe(this);

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

        stopService(notificationReceiver);

        NetworkService.getInstance().unsubscribe(this);
        BluetoothService.getInstance().unsubscribe(this);
        LocationService.getInstance().unsubscribe(this);
        AppPackageService.getInstance(this).unsubscribe(this);

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

        appList.setAdapter(null);
        appAdapter = null;
        appList = null;
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
    public void update(List<AppPackage> packages) {
        appAdapter = new AppPagerAdapter(this, createPages(packages));
        appList.setAdapter(appAdapter);

        // Makes the bottom sheet works
        RecyclerView innerRecyclerView = (RecyclerView) appList.getChildAt(0);
        if (innerRecyclerView != null) {
            innerRecyclerView.setNestedScrollingEnabled(false);
            innerRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        if (tabLayoutMediator != null && tabLayoutMediator.isAttached()) {
            tabLayoutMediator.detach();
        }
        tabLayoutMediator = new TabLayoutMediator(appPageIndicator, appList, ((tab, position) -> {}));
        tabLayoutMediator.attach();
    }

    private List<List<AppPackage>> createPages(List<AppPackage> items) {
        List<List<AppPackage>> pages = new ArrayList<>();

        // Get the screen height in pixels
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float screenHeightInDp = displayMetrics.heightPixels / displayMetrics.density;

        int pageSize = ((int) (4 * (screenHeightInDp / 170)) + 2) / 4 * 4;

        for (int i = 0; i < items.size(); i += pageSize) {
            int end = Math.min(i + pageSize, items.size());
            pages.add(items.subList(i, end));
        }
        return pages;
    }
}