package com.android.gids.ui.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.gids.Api;
import com.android.gids.ApiInterface;
import com.android.gids.FormListDao;
import com.android.gids.FormListEntity;
import com.android.gids.OnClickFormListItem;
import com.android.gids.PendingListAdapter;
import com.android.gids.R;
import com.android.gids.ReviewModal.FormListModalReview;
import com.android.gids.ReviewModal.ReviewListDao;
import com.android.gids.ReviewModal.ReviewListEntity;
import com.android.gids.SurveyDao;
import com.android.gids.SurveyData;
import com.android.gids.SurveyRecord;
import com.android.gids.SurveyRecordDao;
import com.android.gids.SurveyRoomDatabase;
import com.android.gids.UnderReviewListAdapter;
import com.android.gids.Utils;
import com.android.gids.databinding.FragmentFormListDetailBinding;
import com.android.gids.databinding.FragmentUnderReviewBinding;
import com.android.gids.onSyncStarted;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnderReviewFragment extends Fragment implements OnClickFormListItem, onSyncStarted {


    FragmentUnderReviewBinding binding;

    NavController navController;

    RecyclerView rvForm;

    public String sectionId = "0";

    public String formId = "0";

    public String userId = "0";



    String form_name;

    SurveyRoomDatabase myDatabase;

    OnClickFormListItem onClickFormListItem;

    onSyncStarted onSyncStarted;

    SharedPreferences sharedPreferences;

    String uid;

    boolean isClick;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentUnderReviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        isClick = getArguments().getBoolean("isClick");
        formId = getArguments().getString("from_id");
        form_name = (String) getArguments().get("form_name");

        binding.name.setText(form_name);
        myDatabase = SurveyRoomDatabase.getInstance(getContext());
        onClickFormListItem = this;
        onSyncStarted = this;

        sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        uid = sharedPreferences.getString("id", "");


        if(isClick) {
            binding.type.setText("Feedback");
            SurveyRecordDao surveyRecordDao = myDatabase.surveyRecordDao();
            List<SurveyRecord> list = surveyRecordDao.getSurveyRecordByStatus(String.valueOf(Utils.FEEDBACK_RECORD), formId, uid);
            Log.v("List_size", list.size() + "");
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            binding.rvForm.setLayoutManager(linearLayoutManager);
            UnderReviewListAdapter adapter = new UnderReviewListAdapter(getContext(), onClickFormListItem, list, onSyncStarted);
            binding.rvForm.setAdapter(adapter);
        }else{
            SurveyRecordDao surveyRecordDao = myDatabase.surveyRecordDao();
            List<SurveyRecord> list = surveyRecordDao.getSurveyRecordByStatus(String.valueOf(Utils.REVIEW_RECORD), formId, uid);
            Log.v("List_size", list.size() + "");
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            binding.rvForm.setLayoutManager(linearLayoutManager);
            UnderReviewListAdapter adapter = new UnderReviewListAdapter(getContext(), onClickFormListItem, list, onSyncStarted);
            binding.rvForm.setAdapter(adapter);
        }

        return root;
    }

    @Override
    public void onClickFormListItem(String formId, String recid) {
        try {
            if (isClick) {
                if (Utils.isNetworkAvailable(getContext())) {
                    getFormList(recid);
                } else {
                    int instanceId = Utils.getFiveDigitUnique();
                    Bundle bundle = new Bundle();
                    bundle.putInt("instanceId", instanceId);
                    bundle.putString("recid", recid);
                    bundle.putString("from_id", formId);
                    navController.navigate(R.id.action_nav_underreview_to_nav_formstructure_review, bundle);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSyncStarted() {
        binding.loadingAnim.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSyncStop() {
        binding.loadingAnim.setVisibility(View.GONE);
    }


    public void getFormList(String recId) {
        RecordRequestModal formListRequest = new RecordRequestModal();
        formListRequest.setRecord_id(recId);
        ApiInterface methods = Api.getRetrofitInstance().create(ApiInterface.class);
        Call<JsonObject> call = methods.getRecord(formListRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    Log.v("HomeFragment:getForm", response.body().toString());
                    insertInDb(response.body().toString(), recId);
                } catch (Exception e) {
                    e.printStackTrace();
                    binding.rvForm.setVisibility(View.VISIBLE);
                    binding.loadingAnim.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

                Log.v("ErrorInfo", t.getMessage() + " cause " + t.getLocalizedMessage());


                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void insertInDb(String rawjson, String recId) {
        try {
            File file = new File(getContext().getFilesDir(), "review_file.txt");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(rawjson.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v("HomeFragment:insertDB", rawjson);
            ReviewListEntity reviewList = new ReviewListEntity();
            reviewList.setRecId(recId);
            reviewList.setFilePath(file.getAbsolutePath());
            ReviewListDao reviewListDao = myDatabase.reviewListDao();
            reviewListDao.deleteReviewsByRecId(recId);
            reviewListDao.insert(reviewList);

            int instanceId = Utils.getFiveDigitUnique();
            Bundle bundle = new Bundle();
            bundle.putInt("instanceId", instanceId);
            bundle.putString("recid", recId);
            bundle.putString("from_id", formId);
            navController.navigate(R.id.action_nav_underreview_to_nav_formstructure_review, bundle);

        } catch (Exception e) {
            Log.v("HomeFragment:insertDB", e.getMessage());

        }
    }

}