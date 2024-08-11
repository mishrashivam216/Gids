package com.android.gids;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SurveyLogFragment extends Fragment {

    SurveyRoomDatabase myDatabase;
    RecyclerView recyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_survey_log, container, false);
        myDatabase = SurveyRoomDatabase.getInstance(getContext());

        recyclerView = v.findViewById(R.id.rvForm);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        String uid = sharedPreferences.getString("id", null);

        SurveyLogDao surveyLogDao = myDatabase.surveyLogDao();
        List<SurveyLog> list = surveyLogDao.getByUserId(uid);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        SurveyLogAdapter adapter = new SurveyLogAdapter(list , getContext());
        recyclerView.setAdapter(adapter);

        return v;
    }
}