package com.android.gids;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (isLogin()) {
                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        Intent mainIntent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(mainIntent);
                        finish();
                    }

                    /* Create an Intent that will start the MainActivity. */

                }
            }, 500);
        } catch (Exception e) {
            Toast.makeText(SplashActivity.this, e.getMessage() + " " + e.getCause(), Toast.LENGTH_SHORT).show();
        }
    }


    public boolean isLogin() {
        try {
            SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
            if (!sharedPreferences.getString("id", "").equalsIgnoreCase("")) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Toast.makeText(SplashActivity.this, e.getMessage() + "  " + e.getCause(), Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}