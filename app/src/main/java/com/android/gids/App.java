package com.android.gids;

import android.app.Application;

import androidx.appcompat.app.AppCompatActivity;

public class App extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        SurveyRoomDatabase.getInstance(this); //--AppDatabase_Impl does not exist

    }
}
