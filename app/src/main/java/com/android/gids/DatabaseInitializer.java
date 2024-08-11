package com.android.gids;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Room;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DatabaseInitializer {

    public static void populateAsync(final SurveyRoomDatabase db, Context context) {
        new PopulateDbAsync(db, context).execute();
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final SurveyRoomDatabase db;
        private final Context context;

        PopulateDbAsync(SurveyRoomDatabase db, Context context) {
            this.db = db;
            this.context = context;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithGlobalDataSet(db, context);
            populateWithGlobalDataSetValue(db, context);
            populateWithMapDependencyFieldValue(db, context);
            populateWithMapDependencyField(db, context);
            return null;
        }
    }

    private static void populateWithGlobalDataSet(SurveyRoomDatabase db, Context context) {
        try (InputStream is = context.getAssets().open("tbl_global_data_set.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignore comments and empty lines
                if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                    continue;
                }
                sqlBuilder.append(line).append("\n");
                if (line.trim().endsWith(";")) {  // End of SQL statement
                    String sql = sqlBuilder.toString().trim();
                    sqlBuilder.setLength(0);  // Clear the builder

                    // Log the SQL statement
                    Log.d("DatabaseInitializer", "Executing SQL: " + sql);

                    // Execute the SQL statement
                    try {
                        db.getOpenHelper().getWritableDatabase().execSQL(sql);
                    } catch (Exception e) {
                        Log.e("DatabaseInitializer", "Error executing SQL: " + sql, e);
                    }
                }
            }
        } catch (IOException e) {
            Log.e("DatabaseInitializer", "Error reading SQL file", e);
        }
    }

    private static void populateWithGlobalDataSetValue(SurveyRoomDatabase db, Context context) {
        try (InputStream is = context.getAssets().open("tbl_global_data_set_values.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignore comments and empty lines
                if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                    continue;
                }
                sqlBuilder.append(line).append("\n");
                if (line.trim().endsWith(";")) {  // End of SQL statement
                    String sql = sqlBuilder.toString().trim();
                    sqlBuilder.setLength(0);  // Clear the builder

                    // Log the SQL statement
                    Log.d("DatabaseInitializer", "Executing SQL: " + sql);

                    // Execute the SQL statement
                    try {
                        db.getOpenHelper().getWritableDatabase().execSQL(sql);
                    } catch (Exception e) {
                        Log.e("DatabaseInitializer", "Error executing SQL: " + sql, e);
                    }
                }
            }
        } catch (IOException e) {
            Log.e("DatabaseInitializer", "Error reading SQL file", e);
        }
    }

    private static void populateWithMapDependencyFieldValue(SurveyRoomDatabase db, Context context) {
        try (InputStream is = context.getAssets().open("tbl_map_dependency_field_value.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignore comments and empty lines
                if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                    continue;
                }
                sqlBuilder.append(line).append("\n");
                if (line.trim().endsWith(";")) {  // End of SQL statement
                    String sql = sqlBuilder.toString().trim();
                    sqlBuilder.setLength(0);  // Clear the builder

                    // Log the SQL statement
                    Log.d("DatabaseInitializer", "Executing SQL: " + sql);

                    // Execute the SQL statement
                    try {
                        db.getOpenHelper().getWritableDatabase().execSQL(sql);
                    } catch (Exception e) {
                        Log.e("DatabaseInitializer", "Error executing SQL: " + sql, e);
                    }
                }
            }
        } catch (IOException e) {
            Log.e("DatabaseInitializer", "Error reading SQL file", e);
        }
    }

    private static void populateWithMapDependencyField(SurveyRoomDatabase db, Context context) {
        try (InputStream is = context.getAssets().open("tbl_map_dependency_fields.sql");
             BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sqlBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignore comments and empty lines
                if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                    continue;
                }
                sqlBuilder.append(line).append("\n");
                if (line.trim().endsWith(";")) {  // End of SQL statement
                    String sql = sqlBuilder.toString().trim();
                    sqlBuilder.setLength(0);  // Clear the builder

                    // Log the SQL statement
                    Log.d("DatabaseInitializer", "Executing SQL: " + sql);

                    // Execute the SQL statement
                    try {
                        db.getOpenHelper().getWritableDatabase().execSQL(sql);
                    } catch (Exception e) {
                        Log.e("DatabaseInitializer", "Error executing SQL: " + sql, e);
                    }
                }
            }
        } catch (IOException e) {
            Log.e("DatabaseInitializer", "Error reading SQL file", e);
        }
    }



}

