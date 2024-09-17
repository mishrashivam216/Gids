package com.android.gids;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.android.gids.ReviewModal.ReviewListDao;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class Utils {

    public static final int PENDING_RECORD = 1;
    public static final int REVIEW_RECORD = 2;
    public static final int FEEDBACK_RECORD = 3;

    public static long timeStamp;

    private static final String PREFS_NAME = "AppPrefs";
    private static final String TIMESTAMP_KEY = "timeStamp";

    public static long getCurrentTimestampInMillis() {
        return System.currentTimeMillis();
    }


    public static void saveTimeStamp(Context context, long timeStamp) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(TIMESTAMP_KEY, timeStamp);
        editor.apply();  // Commit the changes asynchronously
    }

    public static long getSavedTimeStamp(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(TIMESTAMP_KEY, 0);  // Default value is 0 if not found
    }

    public static void clearShared(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().clear().commit();  // Default value is 0 if not found
    }



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

    public static String generateFileName(String uuid) {
        //String file_name = "form_" + form_id + "_question_" + qid + "_" + instance_id+".jpg";
        String file_name = "uuid_"+uuid;
        Log.v("UploadFile", file_name);
        return file_name;

    }


    public static int getFiveDigitUnique() {
        Random r = new Random(System.currentTimeMillis());
        return 10000 + r.nextInt(20000);
    }

    public static void setDummyLayoutStructure(ImageView elementImage, Context context) {
        elementImage.setScaleType(ImageView.ScaleType.FIT_XY);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Width: match_parent
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 280, context.getResources().getDisplayMetrics()) // Height: 200dp
        );

        elementImage.setLayoutParams(layoutParams);
    }

    public static void setLoadedLayoutStructure(ImageView elementImage, Context context) {
        elementImage.setScaleType(ImageView.ScaleType.FIT_XY);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Width: match_parent
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 380, context.getResources().getDisplayMetrics()) // Height: 200dp
        );

        elementImage.setLayoutParams(layoutParams);

        layoutParams.setMargins(0, 20, 0, 0);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getInstanceOfQuestionByQid(String qid, Context context, String formId) {
        String json_data = getRawJSONFromDB(context);
        Gson gson = new Gson();
        FormListModal data = gson.fromJson(json_data.toString(), FormListModal.class);

        List<DataListModal> formStructureModals = data.getGIDS_SURVEY_APP().getDataList().stream().filter(e -> e.getId().equalsIgnoreCase(formId)).collect(Collectors.toList());


        List<FormStructureModal> formStructureModal = formStructureModals.get(0).getFormStructure().stream().filter(e -> e.getId().equalsIgnoreCase(qid)).collect(Collectors.toList());
        return formStructureModal.get(0).getElement_type();
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


    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // Check if either GPS or Network provider is enabled
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        return isGpsEnabled || isNetworkEnabled;
    }

    public static Bitmap convertFileToBitmap(File file) {
        if (file.exists()) {
            return BitmapFactory.decodeFile(file.getAbsolutePath());
        } else {
            Log.e("ImageHandler", "File does not exist: " + file.getAbsolutePath());
            return null;
        }
    }

    public static File saveBitmapToLocalStorage(Context context, Bitmap bitmap, String fileName) {
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File file = new File(storageDir, fileName);
        try (FileOutputStream outStream = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            Log.d("ImageHandler", "Image saved successfully: " + file.getAbsolutePath());
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ImageHandler", "Error saving image: " + e.getMessage());
            return null;
        }
    }

    public static Bitmap correctImageOrientation(File file) {
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        if (bitmap != null) {
            try {
                ExifInterface exif = new ExifInterface(file.getAbsolutePath());
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                int rotationInDegrees = exifOrientationToDegrees(orientation);
                if (rotationInDegrees != 0) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotationInDegrees);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }


    public static int exifOrientationToDegrees(int exifOrientation) {
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return 90;
            case ExifInterface.ORIENTATION_ROTATE_180:
                return 180;
            case ExifInterface.ORIENTATION_ROTATE_270:
                return 270;
            default:
                return 0;
        }
    }


    public static File getSavedImageFile(Context context, String fileName) {
        // Get the directory where the image was saved
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // Create a File object pointing to the file with the specified fileName
        File file = new File(storageDir, fileName);

        if (file.exists()) {
            Log.d("ImageHandler", "File found: " + file.getAbsolutePath());
            return file;  // Return the file if it exists
        } else {
            Log.e("ImageHandler", "File not found: " + file.getAbsolutePath());
            return null;  // Return null if the file does not exist
        }
    }


    public static void showLogoutConfirmationDialog(Context context, final LogoutListener listener) {
        new AlertDialog.Builder(context)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onLogoutConfirmed();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public interface LogoutListener {
        void onLogoutConfirmed();
    }


}
