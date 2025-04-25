package com.android.gids;

import static android.content.Context.MODE_PRIVATE;

import static com.android.gids.Utils.getRawJSONFromDBForReview;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.gids.ReviewModal.FormListModalReview;
import com.android.gids.ReviewModal.FormStructureModalReview;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnderReviewListAdapter extends RecyclerView.Adapter<UnderReviewListAdapter.MyView> {

    private OnClickFormListItem onClickFormListItem;
    private List<SurveyRecord> list;
    private Context mContext;
    private SurveyRoomDatabase myDatabase;
    private InstanceStatusDao instanceStatusDao;
    private onSyncStarted onSyncStarted;

    private String json_data;
    private FormListModalReview data;
    private String uuid;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyView holder, int position) {
        try {
            json_data = getRawJSONFromDBForReview(mContext, list.get(position).getRecordId());
            Gson gson = new Gson();
            data = gson.fromJson(json_data, FormListModalReview.class);
            uuid = data.getGIDS_SURVEY_APP().getDataList().get(0).getUuid();

            instanceStatusDao = myDatabase.instanceStatusDao();
            InstanceStatus instanceStatus = instanceStatusDao.getToSyncByFormByUUID(list.get(position).getFormId(), list.get(position).getRecordId());

            if (instanceStatus != null && instanceStatus.getIsSubmitted() == 1) {
                holder.btnSync.setVisibility(View.VISIBLE);
            } else {
                holder.btnSync.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.liMian.setOnClickListener(view -> {
            if (holder.btnSync.getVisibility() != View.VISIBLE) {
                onClickFormListItem.onClickFormListItem(list.get(position).getFormId() + "", list.get(position).getRecordId());
            }
        });

        holder.btnSync.setOnClickListener(view -> {
            try {
                Log.d("UploadFile", "Sync Started");
                upLoadDataInBackground(list.get(position).getFormId(), list.get(position).getRecordId(), uuid);

                SurveyDao s = myDatabase.surveyDao();
                SurveyData surveyData = s.getInstanceID(list.get(position).getFormId(), list.get(position).getRecordId());
                if (surveyData != null && surveyData.getRecord_id() != null && !surveyData.getRecord_id().isEmpty()) {
                    if (Utils.isNetworkAvailable(mContext)) {
                        sendData(list.get(position).getFormId(), list.get(position).getRecordId(), uuid);
                    } else {
                        Toast.makeText(mContext, "Please Check your internet Connection!", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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


    public FormRequest createFormRequest(String formId, String recId, String uuid) {
        // Get all the form data to be sent.
        SurveyDao surveyDao = myDatabase.surveyDao();
        List<SurveyData> list = surveyDao.getToSyncByFormRecordId(formId, recId);

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

        // Create and populate the FormRequest object
        FormRequest formRequest = new FormRequest();
        formRequest.setRecord_id(recId);
        formRequest.setUuid(uuid);
        formRequest.setApp_version(Utils.getVersionName(mContext));
        formRequest.setForm_id(formId);
        formRequest.setUser_id(list.get(0).getUser_id());  // Assuming the first item has the user_id
        formRequest.setLatitude(list.get(0).getLat());
        formRequest.setLongtitute(list.get(0).getLogitude());
        formRequest.setCreated_at(list.get(0).getCreate_date_time());
        formRequest.setMock_app_package(Utils.mock_app_package);
        formRequest.setIs_location_from_mock_apps(Utils.is_location_from_mock_apps);
        formRequest.setForm_data(formDataList);

        return formRequest;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void sendData(String formId, String recId, String uuid) {
        onSyncStarted.onSyncStarted();

        // Fetch data that needs to be synced
        SurveyDao surveyDao = myDatabase.surveyDao();
        List<SurveyData> list = surveyDao.getToSyncByFormRecordId(formId, recId);
        List<SurveyData> list1 = surveyDao.getAll();

        Log.v("getData", list1.size() + " all");
        Log.v("getData", list.size() + " sync");

        // If there's no data to sync, show a toast and return
        if (list.size() <= 0) {
            Toast.makeText(mContext, "No Data To Be Synced", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the form request using the helper method
        FormRequest formRequest = createFormRequest(formId, recId, uuid);

        // Log the form request in JSON format
        Gson gson = new Gson();
        String json = gson.toJson(formRequest);
        logLargeJson("FormRequestJSONs", json);

        try {
            insertLogInDb(json, uuid);
        } catch (Exception e) {
            Log.v("ExceptionData", e.getMessage());
        }

        // Set up API call for form data submission
        ApiInterface methods = Api.getRetrofitInstance().create(ApiInterface.class);

        long nonBlankFieldValueCount = formRequest.getForm_data().stream()
                .filter(formData -> formData.getField_value() != null && !formData.getField_value().isEmpty())
                .count();

        Log.v("FormRequestJSON Size", nonBlankFieldValueCount + "");

        // Execute the API call
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

                            // Remove synced data from local database
                            surveyDao.deletebyFormIdRecordId(formId, recId);
                            instanceStatusDao.deletebyRecordId(formId, recId);
                            mContext.startActivity(new Intent(mContext, MainActivity.class));
                        } else {
                            Toast.makeText(mContext, "Some Technical Issue, Please try after some time!", Toast.LENGTH_SHORT).show();
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
        final int chunkSize = 2048;
        for (int i = 0; i < json.length(); i += chunkSize) {
            int end = Math.min(json.length(), i + chunkSize);
            Log.v(tag, json.substring(i, end));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
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

            SurveyLog surveyLog = new SurveyLog();
            surveyLog.setUuid(uuid);
            surveyLog.setUser_id(uid);
            surveyLog.setData(file.getAbsolutePath());
            surveyLog.setCreated_date(Utils.getCurrentDate());
            surveyLog.setUpdated_date(Utils.getCurrentDate());

            SurveyLogDao surveyLogDao = myDatabase.surveyLogDao();
            surveyLogDao.insert(surveyLog);
            Toast.makeText(mContext, "Successfully Inserted", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.v("InsertLogInDb", e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void upLoadDataInBackground(String formId, String recId, String uuid) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> upLoadData(formId, recId, uuid));
        executor.shutdown();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void upLoadData(String formId, String recId, String uuid) {
        SurveyDao surveyDao = myDatabase.surveyDao();
        List<SurveyData> list = surveyDao.getToSyncByFormRecordId(formId, recId);
        for (SurveyData data : list) {
            String qid = data.getQuestion_id();
            String type = getInstanceOfQuestionByQid(qid, formId);
            if ("image".equalsIgnoreCase(type)) {
                File savedFile = Utils.getSavedImageFile(mContext, Utils.generateFileName(uuid));
                if (savedFile != null && savedFile.exists()) {
                    uploadFile(savedFile, uuid);
                }
            }
        }
    }

    public void uploadFile(File file, String uuid) {
        ApiInterface methods = Api.getRetrofitInstance().create(ApiInterface.class);
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        RequestBody recordIdBody = RequestBody.create(MediaType.parse("text/plain"), uuid);
        Call<Void> call = methods.uploadFile(body, recordIdBody);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("FileUploader", "Upload successful!");
                } else {
                    Log.e("FileUploader", "Upload failed with status code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("FileUploader", "Upload failed: " + t.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getInstanceOfQuestionByQid(String qid, String formId) {
        try {
            List<FormStructureModalReview> formStructureModal = data.getGIDS_SURVEY_APP().getDataList().stream()
                    .filter(e -> e.getId().equalsIgnoreCase(formId))
                    .findFirst()
                    .get()
                    .getFormStructure()
                    .stream()
                    .filter(e -> e.getId().equalsIgnoreCase(qid))
                    .collect(Collectors.toList());

            return formStructureModal.get(0).getElement_type();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @Override
    public void onViewRecycled(@NonNull MyView holder) {
        super.onViewRecycled(holder);
        holder.btnSync.setVisibility(View.GONE); // Reset the visibility when view is recycled
    }
}
