package com.android.gids;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.loadinganimation.LoadingAnimation;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PendingListFragment extends Fragment implements OnClickFormListItem, onSyncStarted {

    RecyclerView rvForm;

    public String sectionId = "0";

    public String formId = "0";

    public String userId = "0";


    int index;

    NavController navController;

    OnClickFormListItem onClickFormListItem;

    onSyncStarted onSyncStarted;

    String form_name;

    SurveyRoomDatabase myDatabase;

    LoadingAnimation loadingAnim;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pending_list, container, false);
        loadingAnim = v.findViewById(R.id.loadingAnim);
        rvForm = v.findViewById(R.id.rvForm);
        TextView name = v.findViewById(R.id.name);
        myDatabase = SurveyRoomDatabase.getInstance(getContext());
        onClickFormListItem = this;
        onSyncStarted = this;
        index = getArguments().getInt("index");
        formId = getArguments().getString("from_id");
        form_name = (String) getArguments().get("form_name");
        name.setText(form_name);

        SurveyDao surveyDao = myDatabase.surveyDao();
        List<SurveyData> list = surveyDao.getUniqueInstanceIdsByFormId(formId);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvForm.setLayoutManager(linearLayoutManager);
        PendingListAdapter adapter = new PendingListAdapter(getContext(), onClickFormListItem, list, onSyncStarted);
        rvForm.setAdapter(adapter);
        return v;
    }

    @Override
    public void onClickFormListItem(String instanceId, String uuid) {
        Bundle bundle = new Bundle();
        bundle.putString("uuid", uuid);
        bundle.putInt("index", index);
        bundle.putString("from_id", formId);
        bundle.putInt("instanceId", Integer.parseInt(instanceId));
        navController.navigate(R.id.action_nav_pendingforms_to_nav_formstructure, bundle);
    }


    @Override
    public void onSyncStarted() {
        loadingAnim.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSyncStop() {
        loadingAnim.setVisibility(View.GONE);
    }
}