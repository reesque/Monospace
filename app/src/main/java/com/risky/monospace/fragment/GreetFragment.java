package com.risky.monospace.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.List;

import com.risky.monospace.R;
import com.risky.monospace.model.Notification;
import com.risky.monospace.service.NotificationService;
import com.risky.monospace.util.NotificationSubscriber;

public class GreetFragment extends Fragment implements NotificationSubscriber {
    private View view;
    private Context context;
    private TextView greeter;
    private LinearLayout notificationPanel;

    public GreetFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.greet_fragment, container, false);

        greeter = view.findViewById(R.id.greeter);
        notificationPanel = view.findViewById(R.id.notification_container);

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay < 12){
            greeter.setText("Good Morning");
        } else if(timeOfDay < 18){
            greeter.setText("Good Afternoon");
        } else if(timeOfDay < 22){
            greeter.setText("Good Evening");
        } else {
            greeter.setText("Good Night");
        }

        NotificationService.subscribe(this);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        NotificationService.subscribe(null);
    }

    @Override
    public void update(List<Notification> notifications) {
        // Clear child elements
        notificationPanel.removeAllViews();

        int notificationCount = notifications.size();
        for (Notification n : notifications) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50, 50);

            if (notificationCount == notifications.size() - 4) {
                TextView moreIcon = new TextView(context);
                moreIcon.setText("+" + notificationCount);
                params.setMargins(8, 0, 0, 0);
                moreIcon.setLayoutParams(params);
                moreIcon.setTextColor(ContextCompat.getColor(context, R.color.black));
                moreIcon.setTextSize(10);
                moreIcon.setGravity(Gravity.CENTER);
                moreIcon.setTypeface(moreIcon.getTypeface(), Typeface.BOLD);
                moreIcon.setBackgroundResource(R.drawable.round);

                notificationPanel.addView(moreIcon);
                break;
            }

            Drawable icon = null;
            try {
                Resources res = context.getPackageManager()
                        .getResourcesForApplication(n.packageName);
                icon = ResourcesCompat.getDrawable(res, n.icon, null);
                //icon = res.getDrawable(n.icon, null);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }

            ImageView notifIcon = new ImageView(context);
            params.setMargins(0, 0, 10, 0);
            notifIcon.setLayoutParams(params);
            notifIcon.setImageDrawable(icon);
            notifIcon.setColorFilter(ContextCompat.getColor(context, R.color.white));

            notificationPanel.addView(notifIcon);
            notificationCount--;
        }
    }
}
