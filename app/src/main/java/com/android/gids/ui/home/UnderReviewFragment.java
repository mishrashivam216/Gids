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

import com.android.gids.FormListDao;
import com.android.gids.FormListEntity;
import com.android.gids.OnClickFormListItem;
import com.android.gids.PendingListAdapter;
import com.android.gids.R;
import com.android.gids.SurveyDao;
import com.android.gids.SurveyData;
import com.android.gids.SurveyRecord;
import com.android.gids.SurveyRecordDao;
import com.android.gids.SurveyRoomDatabase;
import com.android.gids.UnderReviewListAdapter;
import com.android.gids.databinding.FragmentFormListDetailBinding;
import com.android.gids.databinding.FragmentUnderReviewBinding;
import com.android.gids.onSyncStarted;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UnderReviewFragment extends Fragment implements OnClickFormListItem, onSyncStarted {


    FragmentUnderReviewBinding binding;

    NavController navController;

    RecyclerView rvForm;

    public String sectionId = "0";

    public String formId = "0";

    public String userId = "0";


    int index;

    String form_name;

    SurveyRoomDatabase myDatabase;

    OnClickFormListItem onClickFormListItem;

    onSyncStarted onSyncStarted;

    SharedPreferences sharedPreferences;

    String uid;


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
        index = getArguments().getInt("index");
        formId = getArguments().getString("from_id");
        form_name = (String) getArguments().get("form_name");
        binding.name.setText(form_name);
        myDatabase = SurveyRoomDatabase.getInstance(getContext());
        onClickFormListItem = this;
        onSyncStarted = this;

        sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        uid = sharedPreferences.getString("id", "");

        SurveyRecordDao surveyRecordDao = myDatabase.surveyRecordDao();
        List<SurveyRecord> list = surveyRecordDao.getSurveyRecordByStatus("2", formId, uid);

        Log.v("List_size",list.size()+"");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        binding.rvForm.setLayoutManager(linearLayoutManager);
        UnderReviewListAdapter adapter = new UnderReviewListAdapter(getContext(), onClickFormListItem, list);
        binding.rvForm.setAdapter(adapter);

        return root;
    }

    @Override
    public void onClickFormListItem(String id, String uuid) {

    }

    @Override
    public void onSyncStarted() {

    }

    @Override
    public void onSyncStop() {

    }

}