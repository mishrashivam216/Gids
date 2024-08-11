package com.android.gids.ui.home;

import android.content.Context;
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

import com.android.gids.FormListModal;
import com.android.gids.R;
import com.android.gids.SurveyDao;
import com.android.gids.SurveyData;
import com.android.gids.SurveyRoomDatabase;
import com.android.gids.Utils;
import com.android.gids.databinding.FragmentFormListDetailBinding;
import com.google.gson.Gson;

import java.util.List;


public class FormListDetailFragment extends Fragment {
    String id;
    int json_index = -1;
    FragmentFormListDetailBinding binding;

    NavController navController;

    SurveyRoomDatabase myDatabase;

    String form_name;




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
            myDatabase = SurveyRoomDatabase.getInstance(getContext());
            binding.liError.setVisibility(View.GONE);
            binding.tvError.setVisibility(View.GONE);
            binding.tvRetry.setVisibility(View.GONE);
            id = (String) getArguments().get("id");
            Log.v("FormListDetailFragment:","id :"+id);
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
                        form_name= userinfo1.getGIDS_SURVEY_APP().getDataList().get(i).getName();
                    }
                }
                Log.v("FormListDetailFragment:","idexSend=> "+json_index+"");

                binding.liNewRecord.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int instanceId = Utils.getFiveDigitUnique();
                        String uuid = Utils.getUuid()+"";
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




                updateData(userinfo1);
            } catch (Exception e) {
                binding.liError.setVisibility(View.VISIBLE);
                binding.tvError.setVisibility(View.VISIBLE);
                binding.tvError.setText("Error:" + e.getMessage());
            }
        }
    }

    public void updateData(FormListModal formListModal) {
        if (formListModal != null && formListModal.getGIDS_SURVEY_APP().getDataList().size()>0) {
            binding.tvPendingRecord.setText("" + formListModal.getGIDS_SURVEY_APP().getDataList().get(json_index).getPending_records_count());
            binding.tvUnderReview.setText("" + formListModal.getGIDS_SURVEY_APP().getDataList().get(json_index).getUnderreview_records_count());
            binding.tvFeedback.setText("" + formListModal.getGIDS_SURVEY_APP().getDataList().get(json_index).getFeedback_records_count());
            binding.tvComplete.setText("" + formListModal.getGIDS_SURVEY_APP().getDataList().get(json_index).getComplete_records_count());
            binding.tvTotal.setText("" + formListModal.getGIDS_SURVEY_APP().getDataList().get(json_index).getTotal_records_count());
        }
    }

    public int getPendingNo(){
        SurveyDao surveyDao = myDatabase.surveyDao();
       List<SurveyData> list= surveyDao.getUniqueInstanceIdsByFormId(id);
       Log.v("getAllPendingRecord", list.size()+"   ");
       return  list.size();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.tvPendingRecord.setText(""+getPendingNo());
    }


}