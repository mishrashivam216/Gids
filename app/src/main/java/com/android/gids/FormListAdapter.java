package com.android.gids;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FormListAdapter extends RecyclerView.Adapter<FormListAdapter.MyView> {

    FormListModal formListModalList;
    OnClickFormListItem onClickFormListItem;

    public FormListAdapter(FormListModal formListModalList, OnClickFormListItem onClickFormListItem) {
        this.formListModalList = formListModalList;
        this.onClickFormListItem = onClickFormListItem;
    }

    @NonNull
    @Override
    public MyView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_form, parent, false);
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {

        holder.name.setText(formListModalList.getGIDS_SURVEY_APP().getDataList().get(position).getName());
        holder.project_name.setText(formListModalList.getGIDS_SURVEY_APP().getDataList().get(position).getProject_name());
        holder.liItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickFormListItem.onClickFormListItem(formListModalList.getGIDS_SURVEY_APP().getDataList().get(position).getId(),"");
            }
        });

    }

    @Override
    public int getItemCount() {
        try {
            return formListModalList.getGIDS_SURVEY_APP().getDataList().size();
        }catch (Exception e){
            return 0;
        }
    }

    public class MyView extends RecyclerView.ViewHolder {

        TextView name, project_name;
        LinearLayout liItem;

        public MyView(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            project_name = view.findViewById(R.id.project_name);
            liItem = view.findViewById(R.id.liItem);

        }
    }

}
