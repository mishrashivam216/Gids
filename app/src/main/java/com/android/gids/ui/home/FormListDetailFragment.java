package com.android.gids.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.android.gids.Api;
import com.android.gids.ApiInterface;
import com.android.gids.FormListModal;
import com.android.gids.FormListStatusRequest;
import com.android.gids.LocationService;
import com.android.gids.LoginActivity;
import com.android.gids.LoginModal;
import com.android.gids.LoginParam;
import com.android.gids.R;
import com.android.gids.SurveyDao;
import com.android.gids.SurveyData;
import com.android.gids.SurveyRecord;
import com.android.gids.SurveyRecordDao;
import com.android.gids.SurveyRoomDatabase;
import com.android.gids.Utils;
import com.android.gids.databinding.FragmentFormListDetailBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FormListDetailFragment extends Fragment {
    String id;
    int json_index = -1;
    FragmentFormListDetailBinding binding;

    NavController navController;

    SurveyRoomDatabase myDatabase;

    String form_name;
    SharedPreferences sharedPreferences;
    String uid;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFormListDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        try {
            sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
            uid = sharedPreferences.getString("id", "");
            myDatabase = SurveyRoomDatabase.getInstance(getContext());
            binding.liError.setVisibility(View.GONE);
            binding.tvError.setVisibility(View.GONE);
            binding.tvRetry.setVisibility(View.GONE);
            id = (String) getArguments().get("id");
            Log.v("FormListDetailFragment:", "id :" + id);
            calculateIndex(Utils.getRawJSONFromDB(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
            binding.liError.setVisibility(View.VISIBLE);
            binding.tvError.setVisibility(View.VISIBLE);
            binding.tvError.setText("Error:" + e.getMessage());
        }


        getPendingNo();

        return root;
    }

    public void calculateIndex(String formjson) {
        if (!formjson.equalsIgnoreCase("")) {
            try {
                Gson gson = new Gson();
                FormListModal userinfo1 = gson.fromJson(formjson.toString(), FormListModal.class);
                for (int i = 0; i < userinfo1.getGIDS_SURVEY_APP().getDataList().size(); i++) {
                    if (userinfo1.getGIDS_SURVEY_APP().getDataList().get(i).getId().equalsIgnoreCase(id)) {
                        json_index = i;
                        form_name = userinfo1.getGIDS_SURVEY_APP().getDataList().get(i).getName();
                    }
                }
                Log.v("FormListDetailFragment:", "idexSend=> " + json_index + "");

                binding.liNewRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int instanceId = Utils.getFiveDigitUnique();
                        String uuid = Utils.getUuid() + "";
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", json_index);
                        bundle.putString("from_id", id);
                        bundle.putInt("instanceId", instanceId);
                        bundle.putString("form_name", form_name);
                        bundle.putString("uuid", uuid);
                        navController.navigate(R.id.action_nav_formlistdetail_to_nav_formstructure, bundle);

                    }
                });


                binding.liPending.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", json_index);
                        bundle.putString("from_id", id);
                        bundle.putString("form_name", form_name);
                        navController.navigate(R.id.action_nav_formlistdetail_to_nav_pendingforms, bundle);
                    }
                });

                binding.liUnderReview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", json_index);
                        bundle.putString("from_id", id);
                        bundle.putString("form_name", form_name);
                        navController.navigate(R.id.action_nav_formlistdetail_to_nav_underreview, bundle);
                    }
                });


                updateData(userinfo1);
            } catch (Exception e) {
                binding.liError.setVisibility(View.VISIBLE);
                binding.tvError.setVisibility(View.VISIBLE);
                binding.tvError.setText("Error:" + e.getMessage());
            }
        }
    }

    public void updateData(FormListModal formListModal) {
        if (formListModal != null && formListModal.getGIDS_SURVEY_APP().getDataList().size() > 0) {
            binding.tvPendingRecord.setText("" + formListModal.getGIDS_SURVEY_APP().getDataList().get(json_index).getPending_records_count());
            binding.tvUnderReview.setText("" + formListModal.getGIDS_SURVEY_APP().getDataList().get(json_index).getUnderreview_records_count());
            binding.tvFeedback.setText("" + formListModal.getGIDS_SURVEY_APP().getDataList().get(json_index).getFeedback_records_count());
            binding.tvComplete.setText("" + formListModal.getGIDS_SURVEY_APP().getDataList().get(json_index).getComplete_records_count());
            binding.tvTotal.setText("" + formListModal.getGIDS_SURVEY_APP().getDataList().get(json_index).getTotal_records_count());

        }
    }

    public int getPendingNo() {
        SurveyDao surveyDao = myDatabase.surveyDao();
        List<SurveyData> list = surveyDao.getUniqueInstanceIdsByFormId(id);
        Log.v("getAllPendingRecord", list.size() + "   ");
        return list.size();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.tvPendingRecord.setText("" + getPendingNo());
        if (Utils.isNetworkAvailable(getContext())) {
            try {
                getFormStatus();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void getFormStatus() {

        FormListStatusRequest formListStatusRequest = new FormListStatusRequest();
        formListStatusRequest.setUser_id(uid);
        formListStatusRequest.setForm_id(id);
        formListStatusRequest.setForm_status("2"); // underreview
        ApiInterface methods = Api.getRetrofitInstance().create(ApiInterface.class);
        Call<JsonObject> call = methods.formListStatus(formListStatusRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {

                    Log.v("FormStatusRequest", response.body().toString());

                    insertInDb(response.body().toString());

                } catch (Exception e) {
                    Log.v("FormStatusRequest", e.getMessage());

                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(getContext(), "An error has occured", Toast.LENGTH_LONG).show();

            }
        });
    }

    public void insertInDb(String data) {
        try {

            JSONObject jsonObject = new JSONObject(data);
            JSONObject GIDS_SURVEY_APP = jsonObject.getJSONObject("GIDS_SURVEY_APP");
            JSONArray jsonArray = GIDS_SURVEY_APP.getJSONArray("DataList");

            List<SurveyRecord> surveyRecords = new ArrayList<>();

            binding.tvUnderReview.setText(surveyRecords.size()+"");

            for (int i = 0; i < jsonArray.length(); i++) {

                SurveyRecord surveyRecord = new SurveyRecord();

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                String record_id = jsonObject1.getString("record_id");
                String form_id = jsonObject1.getString("form_id");
                String surveyor_id = jsonObject1.getString("surveyor_id");
                String surveyor_name = jsonObject1.getString("surveyor_name");
                String section_display_order = jsonObject1.getString("section_display_order");
                String form_step = jsonObject1.getString("form_step");
                String status = jsonObject1.getString("status");
                String created_at = jsonObject1.getString("created_at");
                String updated_at = jsonObject1.getString("updated_at");


                surveyRecord.setRecordId(record_id);
                surveyRecord.setFormId(form_id);
                surveyRecord.setSurveyorId(surveyor_id);
                surveyRecord.setSurveyorName(surveyor_name);
                surveyRecord.setSectionDisplayOrder(section_display_order);
                surveyRecord.setFormStep(form_step);
                surveyRecord.setStatus(status);
                surveyRecord.setCreatedAt(created_at);
                surveyRecord.setUpdatedAt(updated_at);

                surveyRecords.add(surveyRecord);
            }

            SurveyRecordDao surveyRecordDao = myDatabase.surveyRecordDao();
            surveyRecordDao.deleteAllSurveyRecords();
            surveyRecordDao.insert(surveyRecords);

        } catch (Exception e) {
            Log.v("HomeFragment:insertDB", e.getMessage());

        }
    }


}