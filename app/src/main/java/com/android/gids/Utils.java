package com.android.gids;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.android.gids.ReviewModal.ReviewListDao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.UUID;

public class Utils {

    public static final int PENDING_RECORD = 1;
    public static final int REVIEW_RECORD = 2;
    public static final int FEEDBACK_RECORD = 3;


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getCurrentDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }


    public static int getFiveDigitUnique() {
        Random r = new Random(System.currentTimeMillis());
        return 10000 + r.nextInt(20000);
    }

    public static String getRawJSONFromDB(Context context) {
        SurveyRoomDatabase myDatabase = SurveyRoomDatabase.getInstance(context);
        FormListDao data = myDatabase.formListDao();
        if (data == null) {
            return "User not found";
        }
        File file = new File(data.getAllFormList().get(0).getFilePath());
        StringBuilder text = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }

    public static String getSubstringBeforeDollar(String input) {
        int index = input.indexOf("$$");
        if (index != -1) {
            return input.substring(0, index).trim(); // Return substring before $$
        }
        return input; // If $$ is not found, return the entire string
    }


    public static String getRawJSONFromDBForReview(Context context, String recId) {
        SurveyRoomDatabase myDatabase = SurveyRoomDatabase.getInstance(context);
        ReviewListDao data = myDatabase.reviewListDao();
        if (data == null) {
            return "User not found";
        }
        File file = new File(data.getReviewsByRecId(recId).get(0).getFilePath());
        StringBuilder text = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return text.toString();
    }

    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("VersionUtil", "Package name not found", e);
        }
        return versionName;
    }


    public static UUID getUuid() {
        UUID uuid = UUID.randomUUID();
        return uuid;
    }




}
