package com.example.george.betamdl;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class userSettings extends AppCompatActivity {

    Switch prefNotif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        prefNotif = findViewById(R.id.prefNotif);
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SETTINGS_NAME, MODE_PRIVATE);
        prefNotif.setChecked(sharedPreferences.getBoolean(MainActivity.NOTIFICATION_KEY, true));

        prefNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Intent intent = new Intent(userSettings.this, HelloService.class);
                    startService(intent);
                } else {
                    Intent intent = new Intent(userSettings.this, HelloService.class);
                    stopService(intent);
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(MainActivity.NOTIFICATION_KEY, isChecked);
                editor.apply();
            }
        });
    }
}
