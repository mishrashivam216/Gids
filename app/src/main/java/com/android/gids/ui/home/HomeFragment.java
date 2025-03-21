package com.android.gids.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.gids.Api;
import com.android.gids.ApiInterface;
import com.android.gids.FormData;
import com.android.gids.FormListAdapter;
import com.android.gids.FormListDao;
import com.android.gids.FormListEntity;
import com.android.gids.FormListModal;
import com.android.gids.FormListRequest;
import com.android.gids.FormRequest;
import com.android.gids.LocationService;
import com.android.gids.LoginActivity;
import com.android.gids.LoginModal;
import com.android.gids.OnClickFormListItem;
import com.android.gids.R;
import com.android.gids.SurveyDao;
import com.android.gids.SurveyData;
import com.android.gids.SurveyRoomDatabase;
import com.android.gids.Utils;
import com.android.gids.databinding.FragmentHomeBinding;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.intuit.sdp.BuildConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements OnClickFormListItem {

    private FragmentHomeBinding binding;

    SharedPreferences sharedPreferences;

    SurveyRoomDatabase myDatabase;

    NavController navController;

    OnClickFormListItem onClickFormListItem;


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    getOnlineOfflinedata();
                } else {
                    // Permission denied - inform the user about the necessity of the permission
                    Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            });


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }


    public void getOnlineOfflinedata() {
        binding.loadingAnim.setVisibility(View.VISIBLE);
        FormListDao formListDao = myDatabase.formListDao();
        if (formListDao.getAllFormList().size() == 0 || Utils.isNetworkAvailable(getContext())) {
            getFormList();
        } else {
            try {
                updateList();
            } catch (NullPointerException e) {
                Log.v("HomeFragment:onCreate", e.getMessage());
            }
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);


        View root = binding.getRoot();
        try {
            binding.rvForm.setVisibility(View.GONE);

            onClickFormListItem = this;
            sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
            myDatabase = SurveyRoomDatabase.getInstance(getContext());

            if (hasStoragePermission()) {
                getOnlineOfflinedata();
            } else {
                requestStoragePermission();
            }


            binding.tvSync.setVisibility(View.GONE);
            binding.tvSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //sendData();

                    //navController.navigate(R.id.action_nav_home_to_nav_surveylog);

                }
            });

            binding.tvOffline.setText("Offline Application [Version:" + Utils.getVersionName(getContext()) + "]");

            binding.tvOffline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    SurveyDao surveyDao = myDatabase.surveyDao();
//                    surveyDao.delete();
                }
            });


        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage() + " " + e.getCause(), Toast.LENGTH_SHORT).show();
        }


        try {
            LocationService.requestLocation(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return root;
    }


    private boolean hasStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse(String.format("package:%s", getContext().getPackageName())));
                    startActivityForResult(intent, 1000);
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 1000);
                }
            }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    public void getFormList() {
        Log.v("NewUpdatedTimeStampSend", Utils.getSavedTimeStamp(getContext()) + "");
        Log.v("UserId", sharedPreferences.getString("id", null));
        FormListRequest formListRequest = new FormListRequest();
        formListRequest.setUser_id(sharedPreferences.getString("id", null));
        formListRequest.setLast_sync_time(Utils.getSavedTimeStamp(getContext()) + "");
        ApiInterface methods = Api.getRetrofitInstance().create(ApiInterface.class);
        Call<JsonObject> call = methods.formList(formListRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    Log.v("HomeFragment:getForm", response.body().toString());
                    JSONObject jsonObject = new JSONObject(response.body().toString());
                    JSONObject GIDS_SURVEY_APP = jsonObject.getJSONObject("GIDS_SURVEY_APP");
                    if (GIDS_SURVEY_APP.getString("res_code").equalsIgnoreCase("1")) {
                        insertInDb(response.body().toString());
                        Utils.saveTimeStamp(getContext(), GIDS_SURVEY_APP.getLong("synced_timestamp"));
                    } else {
                        updateList();
                    }
                } catch (Exception e) {
                    updateList();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.v("ErrorInfo", t.getMessage() + " cause " + t.getLocalizedMessage());
                Toast.makeText(getContext(), "internet too slow to load the form!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void insertInDb(String rawjson) {
        try {
            File file = new File(getContext().getFilesDir(), "large_text_file.txt");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(rawjson.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v("HomeFragment:insertDB", rawjson);
            FormListEntity formList = new FormListEntity();
            formList.setFilePath(file.getAbsolutePath());
            FormListDao formListDao = myDatabase.formListDao();
            formListDao.deleteAll();
            formListDao.insert(formList);
            updateList();
            Log.v("HomeFragment:ReadFile", readTextDataFromFile());
        } catch (Exception e) {
            Log.v("HomeFragment:insertDB", e.getMessage());

        }
    }


    public String readTextDataFromFile() {
        FormListDao user = myDatabase.formListDao();
        if (user == null) {
            return "User not found";
        }

        StringBuilder text = new StringBuilder();
        File file = new File(user.getAllFormList().get(0).getFilePath());

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            char[] buffer = new char[1024]; // Read in chunks of 1KB
            int numCharsRead;
            while ((numCharsRead = br.read(buffer)) != -1) {
                text.append(buffer, 0, numCharsRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return text.toString();
    }


    public void updateList() {
        try {
            Gson gson = new Gson();
            FormListModal userinfo1 = gson.fromJson(readTextDataFromFile(), FormListModal.class);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            binding.rvForm.setLayoutManager(linearLayoutManager);
            FormListAdapter adapter = new FormListAdapter(userinfo1, onClickFormListItem);
            binding.rvForm.setAdapter(adapter);
            binding.rvForm.setVisibility(View.VISIBLE);
            binding.loadingAnim.setVisibility(View.GONE);
        } catch (Exception e) {
            Log.v("HomeFragment:updateList", e.getMessage());
            binding.rvForm.setVisibility(View.VISIBLE);
            binding.loadingAnim.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClickFormListItem(String id, String uuid) {
        Log.v("HomeFragment:id Clicked", id);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        navController.navigate(R.id.action_nav_home_to_nav_formlistdetail, bundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    getOnlineOfflinedata();

//                    Toast.makeText(getContext(), "Allowed", Toast.LENGTH_SHORT).show();
                    // Permission granted, proceed with your operation
                } else {

//                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();

                    // Permission not granted, notify the user
                }
            }
        }
    }

}