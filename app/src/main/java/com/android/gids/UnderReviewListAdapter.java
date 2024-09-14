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

import com.android.gids.ReviewModal.FormListModalReview;
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

    onSyncStarted onSyncStarted;

    String uid;

    String uuid;


    public UnderReviewListAdapter(Context mContext, OnClickFormListItem onClickFormListItem, List<SurveyRecord> list, onSyncStarted onSyncStarted) {
        this.onClickFormListItem = onClickFormListItem;
        this.list = list;
        this.mContext = mContext;
        this.onSyncStarted = onSyncStarted;
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
        Log.v("dfdsfdsf", list.get(position).getFormId() + "    " + uid);


        try {
            String json_data = Utils.getRawJSONFromDBForReview(mContext, list.get(position).getRecordId());
            Log.v("FormStructureFragment:", "JSON String Recieved: " + json_data);
            Log.v("FormId:", "JFormId: " + list.get(position).getFormId());
            Gson gson = new Gson();
            FormListModalReview data = gson.fromJson(json_data.toString(), FormListModalReview.class);
            uuid = data.getGIDS_SURVEY_APP().getDataList().get(0).getUuid();

            instanceStatusDao = myDatabase.instanceStatusDao();
            InstanceStatus instanceStatus = instanceStatusDao.getToSyncByFormByUUID(list.get(position).getFormId(), list.get(position).getRecordId());
            Log.v("dfdsfdsf", instanceStatus.getIsSubmitted() + "");

            if (instanceStatus != null && instanceStatus.getIsSubmitted() == 1) {
                holder.btnSync.setVisibility(View.VISIBLE);
            } else {
                holder.btnSync.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("dfdsfdsf", e.getMessage() + "");

        }


        holder.liMian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.btnSync.getVisibility() == View.VISIBLE) {

                } else {
                    onClickFormListItem.onClickFormListItem(list.get(position).getFormId() + "", list.get(position).getRecordId());
                }
            }
        });

        holder.btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SurveyDao s = myDatabase.surveyDao();
                    SurveyData surveyData = s.getInstanceID(list.get(position).getFormId(), list.get(position).getRecordId());
                    if (surveyData != null && surveyData.getRecord_id() != null && !surveyData.getRecord_id().isEmpty()) {
                        int instanceId = surveyData.getInstance_id();
                        if (Utils.isNetworkAvailable(mContext)) {
                            sendData(list.get(position).getFormId(), instanceId, uuid);
                        } else {
                            Toast.makeText(mContext, "Please Check your internet Connection!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    public void sendData(String formId, int instanceId, String uuid) {
        onSyncStarted.onSyncStarted();
        SurveyDao surveyDao = myDatabase.surveyDao();
        List<SurveyData> list = surveyDao.getToSyncByFormInstanceId(formId, instanceId);
        List<SurveyData> list1 = surveyDao.getAll();
        Log.v("getData", list1.size() + " all");
        Log.v("getData", list.size() + " sync");
        if (list.size() <= 0) {
            Toast.makeText(mContext, "No Data To Be Synced", Toast.LENGTH_SHORT).show();
            return;
        }
        FormRequest formRequest = new FormRequest();
        List<FormData> formDataList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).getQuestion_id().equalsIgnoreCase("0") && !list.get(i).getQuestion_id().equalsIgnoreCase("")) {
                FormData formData = new FormData();
                formData.setField_name(list.get(i).getField_name());
                formData.setQuestion_id(list.get(i).getQuestion_id());
                formData.setField_value(list.get(i).getField_value());
                formData.setSection_id(list.get(i).getSection_id());
                formData.setLat(list.get(i).getLat());
                formData.setLogitude(list.get(i).getLogitude());
                formData.setCreate_date_time(list.get(i).getCreate_date_time());
                formData.setSync_status(list.get(i).getSync_status());
                formDataList.add(formData);
            }
        }
        formRequest.setRecord_id(uuid);
        formRequest.setForm_id(list.get(0).getForm_id());
        formRequest.setUser_id(list.get(0).getUser_id());
        formRequest.setLatitude(list.get(0).getLat());
        formRequest.setLongtitute(list.get(0).getLogitude());
        formRequest.setCreated_at(list.get(0).getCreate_date_time());

        formRequest.setForm_data(formDataList);
        Log.v("FormRequestJSON Size", formDataList.size() + "");
        Log.v("dsdsfdsf", uuid);
        Gson gson = new Gson();
        String json = gson.toJson(formRequest);
        logLargeJson("FormRequestJSONs", json);
        try {
            insertLogInDb(json, uuid);
        } catch (Exception e) {
            Log.v("ExceptionData", e.getMessage());
        }
        ApiInterface methods = Api.getRetrofitInstance().create(ApiInterface.class);
        Call<JsonObject> call = methods.sendFormData(formRequest);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    onSyncStarted.onSyncStop();
                    Log.v("SyncAPI", response.body().toString());
                    if (response.isSuccessful()) {

                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        JSONObject GIDS_SURVEY_APP = jsonObject.getJSONObject("GIDS_SURVEY_APP");

                        if (GIDS_SURVEY_APP.getString("res_code").equalsIgnoreCase("1")) {

                            Log.v("SyncAPI", "Done");

                            Toast.makeText(mContext, GIDS_SURVEY_APP.getString("res_msg"), Toast.LENGTH_SHORT).show();

                            surveyDao.deletebyFormIdInstanceId(formId, instanceId);
                            instanceStatusDao.deletebyInstanceId(formId, instanceId);
                            mContext.startActivity(new Intent(mContext, MainActivity.class));

                        } else {
                            Toast.makeText(mContext, "Some Technical Issue Please try after some time!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    onSyncStarted.onSyncStop();
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                onSyncStarted.onSyncStop();
                Log.v("SyncAPI", t.getMessage() + "    :" + t.getCause());
                Toast.makeText(mContext, t.getMessage() + " " + t.getCause(), Toast.LENGTH_LONG).show();

            }
        });
    }

    private void logLargeJson(String tag, String json) {
        final int chunkSize = 2048; // Set the chunk size according to your needs
        for (int i = 0; i < json.length(); i += chunkSize) {
            int end = Math.min(json.length(), i + chunkSize);
            Log.v(tag, json.substring(i, end));
        }
    }


    public void insertLogInDb(String rawjson, String uuid) {
        try {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("MySharedPref", MODE_PRIVATE);
            String uid = sharedPreferences.getString("id", null);

            File file = new File(mContext.getFilesDir(), uuid + "_userlog.txt");
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(rawjson.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v("HomeFragment:insertDB", rawjson);
            SurveyLog surveyLog = new SurveyLog();
            surveyLog.setUuid(uuid);
            surveyLog.setUser_id(uid);
            surveyLog.setData(file.getAbsolutePath());
            surveyLog.setCreated_date(Utils.getCurrentDate());
            surveyLog.setUpdated_date(Utils.getCurrentDate());
            SurveyLogDao surveyLogDao = myDatabase.surveyLogDao();
            surveyLogDao.insert(surveyLog);

            Toast.makeText(mContext, "Sucessfully Inserted", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.v("HomeFragment:LogDB", e.getMessage());

        }
    }


}
