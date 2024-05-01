package com.risky.simplify.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Calendar;

import com.risky.simplify.R;

public class GreetFragment extends Fragment {
    private View view;
    private Context context;
    private TextView greeter;

    public GreetFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.greet_fragment, container, false);

        greeter = view.findViewById(R.id.greeter);

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if(timeOfDay < 12){
            greeter.setText("Good Morning.");
        } else if(timeOfDay < 16){
            greeter.setText("Good Afternoon.");
        } else if(timeOfDay < 21){
            greeter.setText("Good Evening.");
        } else {
            greeter.setText("Good Night.");
        }

        return view;
    }
}
