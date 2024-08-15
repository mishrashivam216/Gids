package com.android.gids;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnderReviewListAdapter extends RecyclerView.Adapter<UnderReviewListAdapter.MyView> {

    OnClickFormListItem onClickFormListItem;

    List<SurveyRecord> list;

    Context mContext;

    SurveyRoomDatabase myDatabase;

    InstanceStatusDao instanceStatusDao;



    public UnderReviewListAdapter(Context mContext, OnClickFormListItem onClickFormListItem, List<SurveyRecord> list) {
        this.onClickFormListItem = onClickFormListItem;
        this.list = list;
        this.mContext = mContext;
        myDatabase = SurveyRoomDatabase.getInstance(mContext);
    }


    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_layout, parent, false);
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UnderReviewListAdapter.MyView holder, int position) {


        holder.liMian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.recId.setText("RecId: " + list.get(position).getRecordId());
        holder.created_at.setText("Created At " + list.get(position).getCreatedAt());
        holder.modified_at.setText("Modified At " + list.get(position).getUpdatedAt());


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyView extends RecyclerView.ViewHolder {

        LinearLayout liMian;
        TextView created_at, modified_at;

        Button btnSync;
        TextView recId;


        public MyView(View view) {
            super(view);
            liMian = view.findViewById(R.id.liMain);
            recId = view.findViewById(R.id.recId);
            btnSync = view.findViewById(R.id.btnSync);
            created_at = view.findViewById(R.id.created_at);
            modified_at = view.findViewById(R.id.modified_at);
        }
    }



}
