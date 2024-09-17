package com.android.gids;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.gids.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;

    SurveyRoomDatabase myDatabase;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());
            myDatabase = SurveyRoomDatabase.getInstance(MainActivity.this);
            initGlobalData();
            setSupportActionBar(binding.appBarMain.toolbar);
            DrawerLayout drawer = binding.drawerLayout;
            NavigationView navigationView = binding.navView;
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_formlistdetail, R.id.nav_formstructure)
                    .setOpenableLayout(drawer)
                    .build();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
            NavigationUI.setupWithNavController(navigationView, navController);
            navigationView.setNavigationItemSelectedListener(this);
            binding.appBarMain.ivLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (Utils.isNetworkAvailable(MainActivity.this)) {

                        Utils.showLogoutConfirmationDialog(MainActivity.this, new Utils.LogoutListener() {
                            @Override
                            public void onLogoutConfirmed() {
                                // Handle the logout action here
                                doLogout();
                            }
                        });

                    } else {
                        Toast.makeText(MainActivity.this, "Internet Connectivity is lost", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            binding.appBarMain.tvName.setText(sharedPreferences.getString("name", "....."));
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, e.getMessage() + " " + e.getCause(), Toast.LENGTH_SHORT).show();
        }


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            LocationService.requestLocation(this);
        }

    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            if (Utils.isNetworkAvailable(MainActivity.this)) {
                Utils.showLogoutConfirmationDialog(MainActivity.this, new Utils.LogoutListener() {
                    @Override
                    public void onLogoutConfirmed() {
                        // Handle the logout action here
                        doLogout();
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "Internet Connectivity is lost", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_home) {
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        } else if (id == R.id.nav_surveylog) {
            Fragment fragment = new SurveyLogFragment();
            String tag = "Survey Sync Log";
            if (fragment != null) {
                // Replace the current fragment
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment_content_main, fragment, tag) // Replace fragment_container with your container ID
                        .addToBackStack(null) // Optional: add the transaction to the back stack
                        .commit();
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void doLogout() {
        LogoutParam logoutParam = new LogoutParam();
        logoutParam.setUser_id(sharedPreferences.getString("id", null));
        logoutParam.setLatitude("4554");
        logoutParam.setLongtitude("65465");
        ApiInterface methods = Api.getRetrofitInstance().create(ApiInterface.class);
        Call<LogoutRes> call = methods.doLogout(logoutParam);
        call.enqueue(new Callback<LogoutRes>() {
            @Override
            public void onResponse(Call<LogoutRes> call, Response<LogoutRes> response) {

                try {
                    setPref(response.body());
                } catch (Exception e) {
                    e.printStackTrace();
                    myEdit = sharedPreferences.edit();
                    myEdit.clear();
                    myEdit.commit();
                    Utils.clearShared(MainActivity.this);
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
            @Override
            public void onFailure(Call<LogoutRes> call, Throwable t) {
                Toast.makeText(MainActivity.this, "An error has occured", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setPref(LogoutRes logoutRes) {
        if (logoutRes.getGIDS_SURVEY_APP().getRes_code().equalsIgnoreCase("1")) {
            myEdit = sharedPreferences.edit();
            myEdit.clear();
            myEdit.commit();
            Utils.clearShared(MainActivity.this);
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(MainActivity.this, logoutRes.getGIDS_SURVEY_APP().getRes_msg(), Toast.LENGTH_LONG).show();
        }
    }


    private void initGlobalData() {
        DatabaseInitializer.populateAsync(myDatabase, this);

        new Thread(() -> {
            List<MapDependencyField> globalDataSets = myDatabase.mapDependencyFieldDao().getAll();

            Log.d("xvfdhgfdhgngfhjfg", "tbl_map_dependency_fields Size" + globalDataSets.size());


            for (MapDependencyField dataSet : globalDataSets) {
                // Do something with the data
                Log.d("xvfdhgfdhgngfhjfg", "dataset name" + dataSet.getChildGlobalSetId());
            }
        }).start();
    }

}