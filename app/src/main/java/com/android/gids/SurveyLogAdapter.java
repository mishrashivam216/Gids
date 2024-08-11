package com.android.gids;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SurveyLogAdapter extends RecyclerView.Adapter<SurveyLogAdapter.MyView> {
    List<SurveyLog> list;

    Context mContext;


    SurveyLogAdapter(List<SurveyLog> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }


    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pending_layout, parent, false);
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SurveyLogAdapter.MyView holder, int position) {

        holder.recId.setText("RecId: " + list.get(position).getUuid());
        holder.created_at.setText("Synced At " + list.get(position).getCreated_date());
        holder.modified_at.setText("Modified At " + list.get(position).getUpdated_date());
        holder.btnSync.setVisibility(View.GONE);

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
