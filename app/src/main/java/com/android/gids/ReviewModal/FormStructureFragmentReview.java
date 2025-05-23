package com.android.gids.ReviewModal;


import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.view.Gravity.TOP;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.android.gids.AddMoreList;
import com.android.gids.Api;
import com.android.gids.CustomSpinnerAdapter;
import com.android.gids.ElementChoice;
import com.android.gids.FormStructureModal;
import com.android.gids.FunctionProcessor;
import com.android.gids.GlobalDataSetValue;
import com.android.gids.GlobalDataSetValueDao;
import com.android.gids.InstanceStatus;
import com.android.gids.InstanceStatusDao;
import com.android.gids.Item;
import com.android.gids.LocationService;
import com.android.gids.MainActivity;
import com.android.gids.MapDependencyField;
import com.android.gids.MapDependencyFieldDao;
import com.android.gids.MapDependencyFieldValue;
import com.android.gids.MapDependencyFieldValueDao;
import com.android.gids.OptionSplitter;
import com.android.gids.R;
import com.android.gids.SurveyDao;
import com.android.gids.SurveyData;
import com.android.gids.SurveyRoomDatabase;
import com.android.gids.Utils;
import com.android.gids.databinding.FormStructureReviewBinding;
import com.android.gids.ui.home.BranchinglogicModal;
import com.android.gids.ui.home.FormStructureFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;

import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FormStructureFragmentReview extends Fragment {

    boolean fromAdd = false;
    int index = 0;
    String json_data;

    LinearLayout.LayoutParams layoutParams;

    private int currentPageIndex = 0;

    List<List<FormStructureModalReview>> list;

    List<FormStructureModalReview> FormStructureModalReviewList;

    private String repeatValue = "";

    List<FormStructureModalReview> FormStructureModalReviewList1;

    int counter = 0;
    int Backcounter = 0;

    List<List<FormStructureModalReview>> addMoreList;


    List<AddMoreListReview> addMoreListList;

    Stack<String> stackIds;

    Stack<Stack<String>> layoutAddedList = new Stack<>();
    Stack<String> stackRepeatVal;


    SurveyRoomDatabase myDatabase;

    public String sectionId = "0";

    public String formId = "0";

    public String userId = "0";

    public String uuid = "0";

    public String recid = "0";

    SharedPreferences sharedPreferences;

    FormStructureReviewBinding binding;

    private String lastSearchQuery = "";
    private String lastSearchQueryGlobal = "";
    private String lastSearchQueryGlobalReview = "";


    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int SELECT_FILE = 2;

    ImageView elementImage;

    String file_name = "";

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;

    int flag_for_disable_addmore = 0;

    private Uri photoURI;
    private File photoFile;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FormStructureReviewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        try {
            sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
            userId = sharedPreferences.getString("id", "");
            myDatabase = SurveyRoomDatabase.getInstance(getContext());


            recid = getArguments().getString("recid");
            formId = getArguments().getString("from_id");

            json_data = Utils.getRawJSONFromDBForReview(getContext(), recid);

            Log.v("FormStructureFragment:", "JSON String Recieved: " + json_data);
            Log.v("FormId:", "JFormId: " + formId);
            Gson gson = new Gson();
            FormListModalReview data = gson.fromJson(json_data.toString(), FormListModalReview.class);
            FormStructureModalReviewList = data.getGIDS_SURVEY_APP().getDataList().get(index).getFormStructure();
            uuid = data.getGIDS_SURVEY_APP().getDataList().get(index).getUuid();
            Log.v("FormStructureFragment:", FormStructureModalReviewList.size() + " Size");


            List<FormStructureModalReview> FormStructureModalReviewListt = FormStructureModalReviewList.stream().filter(e -> !e.getAnswers().equalsIgnoreCase("")).collect(Collectors.toList());

            Log.v("dfsgsdggg:", FormStructureModalReviewListt.size() + " Size");

            prepareData();

            binding.tvProjectName.setText(data.getGIDS_SURVEY_APP().getDataList().get(index).getName() + "\nRecord Id: " + recid);
            layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 20, 0, 20);

            getCurrentIndex();

            binding.nextButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View view) {
                    try {
                        try {
                            binding.loadingAnim.setVisibility(VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (validateEmpty() && validateEmptyCheckBox()) {

                                    new ProcessSurveyTask().execute();

                                } else {
                                    try {
                                        Toast.makeText(getContext(), "Please Fill the Required Fields", Toast.LENGTH_SHORT).show();
                                        binding.loadingAnim.setVisibility(View.GONE);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }

                            }
                        }, 100);

                    } catch (Exception e) {
                        Toast.makeText(getContext(), e.getMessage() + " " + e.getCause(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


            binding.finalSubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (validateEmpty()) {

                        showConfirmDialog();

                    } else {
                        try {
                            Toast.makeText(getContext(), "Please Fill the Required Fields", Toast.LENGTH_SHORT).show();
                            binding.loadingAnim.setVisibility(View.GONE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


                }
            });

            binding.prevButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        binding.loadingAnim.setVisibility(VISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (currentPageIndex > 0) {
                        currentPageIndex--;
                        parseData(currentPageIndex);
                    }
                    binding.finalSubmitButton.setVisibility(View.GONE);
                    binding.nextButton.setText("SAVE AND NEXT");

                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                binding.loadingAnim.setVisibility(View.GONE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }, 1000);

                }
            });

        } catch (Exception e) {
            Log.v("FormStructureFragment", e.getMessage());
        }
        return root;
    }


    private class ProcessSurveyTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Show loading animation before starting the task
            binding.loadingAnim.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... voids) {

            String checkBoxData = "";
            String preQid = "";
            List<SurveyData> surveyDataList = new ArrayList<>();

            try {
                for (int j = 0; j < binding.layout.getChildCount(); j++) {
                    View view = binding.layout.getChildAt(j);

                    if (view.getVisibility() == VISIBLE) {

                        if (view instanceof CheckBox) {
                            preQid = String.valueOf(view.getTag());
                            if (((CheckBox) view).isChecked()) {

                                checkBoxData = checkBoxData.isEmpty() ? String.valueOf(view.getId()) : checkBoxData + "," + view.getId();
                            }
                            if (j != binding.layout.getChildCount() - 1) {
                                continue;
                            }
                        }


                        if (!preQid.isEmpty()) {
                            Log.v("cdsfdsfdsf", checkBoxData);
                            surveyDataList.add(createSurveyData(preQid, checkBoxData));
                            checkBoxData = "";
                            preQid = "";
                        }

                        SurveyData surveyData = createSurveyData(String.valueOf(view.getId()), "");

                        if (view instanceof Spinner) {
                            Item selectedItem = (Item) ((Spinner) view).getSelectedItem();
                            if (selectedItem != null) {
                                surveyData.setField_value(selectedItem.getId());
                                surveyDataList.add(surveyData);
                            }
                        } else if (view instanceof EditText) {
                            surveyData.setField_value(((EditText) view).getText().toString());
                            surveyDataList.add(surveyData);
                        } else if (view instanceof ImageView) {
                            if (((ImageView) view).getDrawable() != null) {
                                surveyData.setField_value(String.valueOf(((ImageView) view).getTag()));
                                surveyDataList.add(surveyData);
                                Drawable drawable = ((ImageView) view).getDrawable();
                                if (drawable instanceof BitmapDrawable) {
                                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                                    if (bitmap != null) {
                                        Utils.saveBitmapToLocalStorage(getContext(), bitmap, String.valueOf(((ImageView) view).getTag()));
                                    }
                                } else {
                                    Log.e("ImageViewCheck", "Drawable is not a BitmapDrawable.");
                                }
                            }
                        } else if (view instanceof RadioGroup) {
                            int selectedId = ((RadioGroup) view).getCheckedRadioButtonId();
                            if (selectedId != -1) {
                                surveyData.setField_value(String.valueOf(selectedId));
                                surveyDataList.add(surveyData);
                            }
                        } else if (view instanceof LinearLayout) {
                            LinearLayout linearLayout = (LinearLayout) view;
                            for (int k = 0; k < linearLayout.getChildCount(); k++) {
                                View childView = linearLayout.getChildAt(k);
                                if (childView instanceof EditText) {
                                    Log.v("MyDebuggingData", ((EditText) childView).getText().toString() + " EditText found");
                                    surveyData.setField_value(String.valueOf(((EditText) childView).getText().toString()));
                                    surveyDataList.add(surveyData);
                                }
                            }
                        }
                    }

                }

                if (!checkBoxData.isEmpty()) {
                    Log.v("LineNos479", "Added Data at end: " + checkBoxData);
                    surveyDataList.add(createSurveyData(preQid, checkBoxData));
                    checkBoxData = "";
                    preQid = "";
                }

                // Perform the bulk insert/update operation
                addInDb(surveyDataList);

            } catch (Exception e) {
                Log.v("FormStructureFragment", e.getMessage());
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            return null; // No error, return null
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);


            // Hide the loading animation after task completion
            binding.loadingAnim.setVisibility(View.GONE);

            if (binding.nextButton.getText().toString().equalsIgnoreCase("SAVE AND SUBMIT")) {
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            }

            if (currentPageIndex < list.size() - 1) {
                currentPageIndex++;
                parseData(currentPageIndex);

            }
            if (currentPageIndex == list.size() - 1) {
                binding.finalSubmitButton.setVisibility(VISIBLE);
                binding.nextButton.setText("SAVE AND SUBMIT");
            }
            binding.scrollView.scrollTo(0, 0);


        }
    }


    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("FINAL SUBMIT");
        builder.setMessage("FINAL SUBMIT?");
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int which) {
                new ProcessSurveyTask().execute();
                InstanceStatus instanceStatus = new InstanceStatus();

                instanceStatus.setIsSubmitted(1);
                instanceStatus.setForm_id(formId);
                instanceStatus.setUuid(recid);
                InstanceStatusDao instanceStatusDao = myDatabase.instanceStatusDao();
                instanceStatusDao.insert(instanceStatus);
                dialog.dismiss();

                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void prepareData() {
        //list => List<List<FormStructureModalReview>> => [ sec1[{},{}] , sec2[{},{}]]
        list = new ArrayList<>();
        List<FormStructureModalReview> FormStructureModalReviewList1 = new ArrayList<>();

        //We store the data section wise in list

        for (int i = 0; i < FormStructureModalReviewList.size(); i++) {
            try {
                if (((FormStructureModalReviewList.get(i).getElement_type().equalsIgnoreCase("section") && i != 0)) || i == FormStructureModalReviewList.size() - 1) {
                    Log.v("Itr", i + "");

                    if (i == FormStructureModalReviewList.size() - 1) {
                        FormStructureModalReviewList1.add(FormStructureModalReviewList.get(i));
                    }

                    list.add(FormStructureModalReviewList1);
                    FormStructureModalReviewList1 = new ArrayList<>();

                }
                FormStructureModalReviewList1.add(FormStructureModalReviewList.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (list.size() == 0 && FormStructureModalReviewList1.size() > 0) {
            list.add(FormStructureModalReviewList1);
            binding.finalSubmitButton.setVisibility(VISIBLE);
            binding.nextButton.setText("SAVE AND SUBMIT");
            //handle only one section
        }
        Log.v("FormStructureFragment", FormStructureModalReviewList.size() + "");


        handlePaging();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void handlePaging() {
        List<String> sectionLabels = new ArrayList<>();
        sectionLabels.add("Select Section"); // Manually add the first entry
        List<Boolean> shouldHighlight = new ArrayList<>();
        shouldHighlight.add(false); // No highlight for "Select Section"

        for (List<FormStructureModalReview> section : list) {
            if (!section.isEmpty()) {
                List<FormStructureModalReview> list1 = section.stream()
                        .filter(e -> !e.getFeedback().equalsIgnoreCase(""))
                        .collect(Collectors.toList());

                sectionLabels.add(section.get(0).element_label);

                if (list1.size() > 0) {
                    shouldHighlight.add(true); // Mark this section to be highlighted
                } else {
                    shouldHighlight.add(false); // No highlight needed
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.custom_spinner_item, sectionLabels) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position < shouldHighlight.size() && shouldHighlight.get(position)) {
                    textView.setTextColor(Color.RED);
                } else {
                    textView.setTextColor(Color.BLACK); // Default color
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position < shouldHighlight.size() && shouldHighlight.get(position)) {
                    textView.setTextColor(Color.RED);
                } else {
                    textView.setTextColor(Color.BLACK); // Default color
                }
                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerPaging.setAdapter(adapter);


        binding.spinnerPaging.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {

//                binding.loadingAnim.setVisibility(VISIBLE);
//                try {
//                    if (position != 0) {
//                        createLayoutFromJson();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    binding.loadingAnim.setVisibility(View.GONE);
//                }
//
//                if (position != 0) {
//                    try {
//                        currentPageIndex = position - 1;
//                        parseData(currentPageIndex);
//                        binding.finalSubmitButton.setVisibility(View.GONE);
//                        binding.nextButton.setText("SAVE AND NEXT");
//                        if (currentPageIndex == list.size() - 1) {
//                            binding.finalSubmitButton.setVisibility(VISIBLE);
//                            binding.nextButton.setText("SAVE AND SUBMIT");
//                        }
//                        binding.loadingAnim.setVisibility(View.GONE);
//                    } catch (Exception e) {
//                        Log.v("afsdfsd", e.getMessage());
//                        e.printStackTrace();
//                        binding.loadingAnim.setVisibility(View.GONE);
//                    }
//                }
//                binding.loadingAnim.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing if nothing is selected
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void inserAllQuestionIdToSync() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                List<SurveyData> surveyDataList = new ArrayList<>();
                int c = 0;
                for (int i = 0; i < FormStructureModalReviewList.size(); i++) {
                    try {
                        if (!FormStructureModalReviewList.get(i).getElement_type().equalsIgnoreCase("section") &&
                                !FormStructureModalReviewList.get(i).getElement_type().equalsIgnoreCase("label") &&
                                !FormStructureModalReviewList.get(i).getElement_type().equalsIgnoreCase("repeat")) {
                            Log.v("FormRequestJSON Size", c++ + "");
                            SurveyData surveyData = createSurveyData(String.valueOf(FormStructureModalReviewList.get(i).getId()), FormStructureModalReviewList.get(i).getAnswers());
                            surveyDataList.add(surveyData);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                addAnswerInDb(surveyDataList);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addAnswerInDb(List<SurveyData> surveyDataList) {
        try {
            SurveyDao surveyDao = myDatabase.surveyDao();

            for (SurveyData s : surveyDataList) {
                Log.v("InsertedDataInDB", s.getField_value());
                SurveyData insertedData = surveyDao.getPredefinedAnswerReview(formId, recid, s.getQuestion_id());
                if (insertedData != null && !insertedData.getQuestion_id().isEmpty()) {
                    Log.v("gfdgfdgfdgf:", "Data is already there - " + recid);
                } else {
                    surveyDao.insert(s);
                    Log.v("gfdgfdgfdgf:", "SuccessFully Inserted - " + recid);
                }
            }
        } catch (Exception e) {
            Log.v("gfdgfdgfdgf:", e.getMessage());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createLayoutFromJson() {


        String checkBoxData = "";
        String preQid = "";
        List<SurveyData> surveyDataList = new ArrayList<>();

        try {
            for (int j = 0; j < binding.layout.getChildCount(); j++) {
                View view = binding.layout.getChildAt(j);

                if (view.getVisibility() == VISIBLE) {


                    if (view instanceof CheckBox) {
                        if (((CheckBox) view).isChecked()) {
                            preQid = String.valueOf(view.getTag());
                            checkBoxData = checkBoxData.isEmpty() ? String.valueOf(view.getId()) : checkBoxData + "," + view.getId();
                        }
                        if (j != binding.layout.getChildCount() - 1) {
                            continue;
                        }
                    }

                    if (!checkBoxData.isEmpty()) {
                        surveyDataList.add(createSurveyData(preQid, checkBoxData));
                        checkBoxData = "";
                        preQid = "";
                    }

                    SurveyData surveyData = createSurveyData(String.valueOf(view.getId()), "");

                    if (view instanceof Spinner) {
                        Item selectedItem = (Item) ((Spinner) view).getSelectedItem();
                        if (selectedItem != null) {
                            surveyData.setField_value(selectedItem.getId());
                            surveyDataList.add(surveyData);
                        }
                    } else if (view instanceof EditText) {
                        surveyData.setField_value(((EditText) view).getText().toString());
                        surveyDataList.add(surveyData);
                    } else if (view instanceof ImageView) {
                        if (((ImageView) view).getDrawable() != null) {
                            surveyData.setField_value(String.valueOf(((ImageView) view).getTag()));
                            surveyDataList.add(surveyData);
                            Drawable drawable = ((ImageView) view).getDrawable();
                            if (drawable instanceof BitmapDrawable) {
                                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                                if (bitmap != null) {
                                    Utils.saveBitmapToLocalStorage(getContext(), bitmap, String.valueOf(((ImageView) view).getTag()));
                                }
                            } else {
                                Log.e("ImageViewCheck", "Drawable is not a BitmapDrawable.");
                            }
                        }
                    } else if (view instanceof RadioGroup) {
                        int selectedId = ((RadioGroup) view).getCheckedRadioButtonId();
                        if (selectedId != -1) {
                            surveyData.setField_value(String.valueOf(selectedId));
                            surveyDataList.add(surveyData);
                        }
                    } else if (view instanceof LinearLayout) {
                        LinearLayout linearLayout = (LinearLayout) view;
                        for (int k = 0; k < linearLayout.getChildCount(); k++) {
                            View childView = linearLayout.getChildAt(k);
                            if (childView instanceof EditText) {
                                Log.v("MyDebuggingData", ((EditText) childView).getText().toString() + " EditText found");
                                surveyData.setField_value(String.valueOf(((EditText) childView).getText().toString()));
                                surveyDataList.add(surveyData);
                            }
                        }
                    }


                    // Perform the bulk insert/update operation
                    addInDb(surveyDataList);
                }

            }

        } catch (Exception e) {
            Log.v("FormStructureFragment", e.getMessage());
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private SurveyData createSurveyData(String questionId, String fieldValue) {
        SurveyData surveyData = new SurveyData();
        surveyData.setForm_id(String.valueOf(formId));
        surveyData.setSection_id(sectionId);
        surveyData.setUser_id(userId);
        surveyData.setRecord_id(recid);
        surveyData.setSource(Utils.FEEDBACK_RECORD);
        surveyData.setLat(LocationService.getLat());
        surveyData.setLogitude(LocationService.getLong());
        surveyData.setSync_status("0");
        surveyData.setCreate_date_time(Utils.getCurrentDate());
        surveyData.setField_name("");  // Set field name if necessary
        surveyData.setQuestion_id(questionId);
        surveyData.setField_value(fieldValue);
        return surveyData;
    }


    private SurveyData createSurveyDataWithAnswer(String questionId, String fieldValue) {
        SurveyData surveyData = new SurveyData();
        surveyData.setForm_id(String.valueOf(formId));
        surveyData.setSection_id(sectionId);
        surveyData.setUser_id(userId);
        surveyData.setRecord_id(recid);
        surveyData.setSource(Utils.FEEDBACK_RECORD);
        surveyData.setLat(LocationService.getLat());
        surveyData.setLogitude(LocationService.getLong());
        surveyData.setSync_status("0");
        surveyData.setCreate_date_time(Utils.getCurrentDate());
        surveyData.setField_name("");  // Set field name if necessary
        surveyData.setQuestion_id(questionId);
        surveyData.setField_value(fieldValue);
        return surveyData;
    }


    private void addInDb(List<SurveyData> surveyDataList) {
        try {
            SurveyDao surveyDao = myDatabase.surveyDao();

            for (SurveyData s : surveyDataList) {
                Log.v("InsertedDataInDB", "Question ID: " + s.getQuestion_id() + ", Value: " + s.getField_value());

                SurveyData existingData = surveyDao.getPredefinedAnswerReview(formId, recid, s.getQuestion_id());

                if (s.getField_value().isEmpty()) {
                    // If field_value is empty, delete the corresponding entry
                    if (existingData != null) {

                        surveyDao.updateByFieldsReview(s.getQuestion_id(), recid, formId, s.getField_value());

                        // surveyDao.deleteByQuestionId(formId, recid, s.getQuestion_id());
                        Log.v("DBAction", "Deleted Question ID: " + s.getQuestion_id());
                    }
                } else {
                    // If data exists, update it; otherwise, insert new data
                    if (existingData != null && !existingData.getQuestion_id().isEmpty()) {
                        surveyDao.updateByFieldsReview(s.getQuestion_id(), recid, formId, s.getField_value());
                        Log.v("DBAction", "Updated Question ID: " + s.getQuestion_id() + "  " + s.getField_value());
                    } else {
                        surveyDao.insert(s);
                        Log.v("DBAction", "Inserted Question ID: " + s.getQuestion_id());
                    }
                }
            }

            Log.v("FormStructureFragment", "Successfully Inserted/Updated");

        } catch (Exception e) {
            Log.v("FormStructureFragment", "Error: " + e.getMessage());
        }
    }

    public void parseData(int index) {

        updateProgressBar(index);

        if (currentPageIndex == 0) {
            binding.prevButton.setVisibility(View.GONE);
        } else {
            binding.prevButton.setVisibility(VISIBLE);
        }
        binding.layout.removeAllViews();

        //List<FormStructureModalReview> => [{},{}]
        FormStructureModalReviewList1 = new ArrayList<>();

        //List<List<FormStructureModalReview>> => [[{},{},{}], [{},{},{}]]
        addMoreList = new ArrayList<>();

        //List<AddMoreList> => [  [[{},{},{}], [{},{},{}]],  [[{},{},{}], [{},{},{}]],  [[{},{},{}], [{},{},{}]],  [[{},{},{}], [{},{},{}]]  ]
        addMoreListList = new ArrayList<>();

        repeatValue = "";

        // => list : List<List<FormStructureModalReview>> : [[{},{}], [{},{}]]

        //First time index is zero

        //populate First Section Data


        for (int j = 0; j < list.get(index).size(); j++) {
            try {
                FormStructureModalReview FormStructureModalReview = list.get(index).get(j);

                if (FormStructureModalReview.getRepeat().equalsIgnoreCase("")) {

                    if (FormStructureModalReviewList1.size() > 0) {
                        addMoreList.add(new ArrayList<>(FormStructureModalReviewList1));
                        FormStructureModalReviewList1 = new ArrayList<>();
                    }

                    if (FormStructureModalReview.getElement_type().equalsIgnoreCase("section")) {
                        sectionId = FormStructureModalReview.getId();
                        View v = createLabelTextView(FormStructureModalReview);
                        binding.layout.addView(v);
                    } else if (FormStructureModalReview.getElement_type().equalsIgnoreCase("image")) {
                        View v = createImageElement(FormStructureModalReview);
                        binding.layout.addView(v);

                        View elElement = createLabelEditElement(FormStructureModalReview, v);
                        if (!FormStructureModalReview.getFeedback().isEmpty()) {
                            binding.layout.addView(elElement);
                        }


                        Button button = createButtonForImage(FormStructureModalReview);
                        binding.layout.addView(button);


                    } else if (FormStructureModalReview.getElement_type().equalsIgnoreCase("label")) {
                        View v = createLabelTextView(FormStructureModalReview);
                        binding.layout.addView(v);
                    } else if (FormStructureModalReview.getElement_type().equalsIgnoreCase("text") || FormStructureModalReview.getElement_type().equalsIgnoreCase("textarea")) {
                        View etLabel = createLabelEditTextView(FormStructureModalReview);
                        binding.layout.addView(etLabel);
                        EditText view = createEditText(FormStructureModalReview);
                        binding.layout.addView(view);
                        View elElement = createLabelEditElement(FormStructureModalReview, view);
                        if (!FormStructureModalReview.getFeedback().isEmpty()) {
                            binding.layout.addView(elElement);
                        }
                    } else if (FormStructureModalReview.getElement_type().equalsIgnoreCase("select")) {

                        View label = createLabelEditTextView(FormStructureModalReview);
                        binding.layout.addView(label);

                        if (FormStructureModalReview.getSelect_type().equalsIgnoreCase("global")) {
                            try {
                                Spinner spinner = createSpinnerForGlobal(FormStructureModalReview);
                                binding.layout.addView(spinner);

                                View elElement = createLabelEditElement(FormStructureModalReview, spinner);
                                if (!FormStructureModalReview.getFeedback().isEmpty()) {
                                    binding.layout.addView(elElement);
                                }
                            } catch (Exception e) {
                                Log.v("DebugPoint", e.getMessage());

                            }

                        } else {
                            Spinner spinner = createSpinner(FormStructureModalReview);
                            binding.layout.addView(spinner);

                            if (spinner.getVisibility() == VISIBLE) {
                                label.setVisibility(VISIBLE);
                            }
                            View elElement = createLabelEditElement(FormStructureModalReview, spinner);
                            if (!FormStructureModalReview.getFeedback().isEmpty()) {
                                binding.layout.addView(elElement);
                            }
                        }


                    } else if (FormStructureModalReview.getElement_type().equalsIgnoreCase("radio")) {
                        TextView tv = createLabelRadio(FormStructureModalReview);
                        binding.layout.addView(tv);
                        RadioGroup radioGroup = createRadioButton(FormStructureModalReview);
                        binding.layout.addView(radioGroup);

                        View elElement = createLabelEditElement(FormStructureModalReview, radioGroup);
                        if (!FormStructureModalReview.getFeedback().isEmpty()) {
                            binding.layout.addView(elElement);
                        }
                    } else if (FormStructureModalReview.getElement_type().equalsIgnoreCase("checkbox")) {
                        TextView tv = createLabelCheckbox(FormStructureModalReview);
                        binding.layout.addView(tv);
                        createMultiCheckbox(FormStructureModalReview, -1);

                        View elElement = createLabelEditElement(FormStructureModalReview, tv);
                        if (!FormStructureModalReview.getFeedback().isEmpty()) {
                            binding.layout.addView(elElement);
                        }

                    } else if (FormStructureModalReview.getElement_type().equalsIgnoreCase("calculated_field")) {

                        View etLabel = createLabelEditTextView(FormStructureModalReview);
                        binding.layout.addView(etLabel);

                        LinearLayout layout = createCalculatedField(FormStructureModalReview);
                        binding.layout.addView(layout);

                        View elElement = createLabelEditElement(FormStructureModalReview, layout);
                        if (!FormStructureModalReview.getFeedback().isEmpty()) {
                            binding.layout.addView(elElement);
                        }

                    } else if (FormStructureModalReview.getElement_type().equalsIgnoreCase("repeat")) {
                        //It means before Enter in this  addMoreList  should be filled (it promiss that before Addmore element repeat element will be there)
                        AddMoreListReview addMoreListdata = new AddMoreListReview();
                        addMoreListdata.setAddMoreList(addMoreList);
                        addMoreListdata.setId(FormStructureModalReview.getId());
                        addMoreListList.add(addMoreListdata);
                        createButton(FormStructureModalReview);
                    }
                } else {
                    if (!FormStructureModalReview.getRepeat().equalsIgnoreCase(repeatValue)) {
                        if (FormStructureModalReviewList1.size() > 0) {
                            addMoreList.add(new ArrayList<>(FormStructureModalReviewList1));
                        }
                        FormStructureModalReviewList1 = new ArrayList<>();
                        FormStructureModalReviewList1.add(FormStructureModalReview);
                        repeatValue = FormStructureModalReview.getRepeat();
                    } else {
                        FormStructureModalReviewList1.add(FormStructureModalReview);
                        repeatValue = FormStructureModalReview.getRepeat();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.v("AddmoreSize", addMoreListList.size() + "");

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    binding.loadingAnim.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 1000);

        if (index == list.size() - 1) {
            binding.finalSubmitButton.setVisibility(VISIBLE);
            binding.nextButton.setText("SAVE AND SUBMIT");
        }
    }

    private ImageView createImageElement(FormStructureModalReview formStructureModal) {
        file_name = "";
        // Create ImageView dynamically
        elementImage = new ImageView(getContext());
        elementImage.setId(Integer.parseInt(formStructureModal.getId()));

        elementImage.setTag(Utils.generateFileName(uuid));

        elementImage.setPadding(10, 10, 10, 10);


        String pData = getPrefilledData(formStructureModal.getId());

        // Set the image from local storage
        File b = Utils.getSavedImageFile(getContext(), pData);

        if (b != null) {

            Bitmap bit = Utils.convertFileToBitmap(b);
            if (bit != null && pData != null && !pData.equalsIgnoreCase("")) {
                Utils.setLoadedLayoutStructure(elementImage, getContext());
                elementImage.setImageBitmap(bit);

            } else if (formStructureModal.getAnswers() != null && !formStructureModal.getAnswers().equalsIgnoreCase("")) {
                Utils.setIMAGELayoutStructure(elementImage, getContext());
                String imgUri = "";

                if (formStructureModal.getAnswers().contains("jpg") || formStructureModal.getAnswers().contains("png")) {
                    imgUri = Api.PICTURE_BASE_URL + formStructureModal.getAnswers();
                } else {
                    imgUri = Api.PICTURE_BASE_URL + formStructureModal.getAnswers() + ".jpg";
                }

                Glide.with(getContext())
                        .load(imgUri)
                        .signature(new ObjectKey(System.currentTimeMillis()))  // Unique key to bust the cache
                        .error(getContext().getDrawable(R.mipmap.image))
                        .placeholder(getContext().getDrawable(R.mipmap.image))
                        .into(elementImage);


            }
        } else {
            Utils.setDummyLayoutStructure(elementImage, getContext());
            elementImage.setImageDrawable(getContext().getDrawable(R.mipmap.image));
        }

        elementImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageZoomDialog();
            }
        });

        return elementImage; // Return the dynamically created ImageView
    }

    private void showImageZoomDialog() {
        Dialog dialog = new Dialog(getContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.dialog_image_zoom);
        PhotoView photoView = dialog.findViewById(R.id.photoView);
        photoView.setImageDrawable(elementImage.getDrawable()); // Use the image currently shown
        dialog.show();
    }


    private Button createButtonForImage(FormStructureModalReview formStructureModal) {
        Button button = new Button(getContext());
        button.setText("Upload Picture");
        button.setId(Integer.valueOf(formStructureModal.getId()));
        button.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                file_name = Utils.generateFileName(uuid);

                Log.d("UploadFile", file_name + " created");

                requestCameraPermission();
            }
        });

        return button;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (photoFile != null) {
                Bitmap bitmap = Utils.correctImageOrientation(photoFile);
                elementImage.setImageBitmap(bitmap); // Display the high-resolution image
                Log.d("UploadFile", "Image saved at: " + photoFile.getAbsolutePath());
            }
        } else if (requestCode == SELECT_FILE && resultCode == RESULT_OK) {
            Bitmap bitmap = handleGalleryResult(data);
            File savedFile = Utils.saveBitmapToLocalStorage(getContext(), bitmap, file_name);
            if (savedFile != null) {
                Bitmap correctedBitmap = Utils.correctImageOrientation(savedFile);
                elementImage.setImageBitmap(correctedBitmap);

            }
        }
    }

    private Bitmap handleGalleryResult(Intent data) {
        if (data != null) {
            try {
                Uri selectedImageUri = data.getData();
                InputStream imageStream = getContext().getContentResolver().openInputStream(selectedImageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                return bitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to select image from gallery", Toast.LENGTH_SHORT).show();
            }
        }
        return null;
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            showImagePickerDialog(FormStructureFragmentReview.this);
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the file where the photo should go
            try {
                photoFile = Utils.createImageFile(getContext()); // You need to implement this method to create the file
                if (photoFile != null) {
                    photoURI = FileProvider.getUriForFile(getContext(), "com.android.gids.fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
        }
    }


    public void showImagePickerDialog(final Fragment fragment) {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    dispatchTakePictureIntent();
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    fragment.startActivityForResult(intent, SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public ArrayList<AddMoreListReview> getObjectsWithId(String targetId) {
        ArrayList<AddMoreListReview> objectsWithId = new ArrayList<>();
        for (AddMoreListReview obj : addMoreListList) {
            if (obj.getId().equalsIgnoreCase(targetId)) {
                objectsWithId.add(obj);
            }
        }
        return objectsWithId;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImagePickerDialog(FormStructureFragmentReview.this);
            } else {
                Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void createButton(FormStructureModalReview FormStructureModalReview) {

        stackIds = new Stack<>();
        stackRepeatVal = new Stack<>();

        ArrayList<AddMoreListReview> addMoreLists = getObjectsWithId(FormStructureModalReview.getId());
        Backcounter = addMoreLists.get(0).getAddMoreList().size();
        counter = 0;

        TextView labelTextView = new TextView(getContext());
        labelTextView.setText(FormStructureModalReview.getElement_label());
        labelTextView.setId(0);
        labelTextView.setTextColor(getContext().getResources().getColor(R.color.black));
        labelTextView.setTypeface(null, Typeface.BOLD);
        labelTextView.setLayoutParams(layoutParams);
        labelTextView.setVisibility(View.GONE);


        LinearLayout buttonLayout = new LinearLayout(getContext());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setLayoutParams(layoutParams);
        buttonLayout.setId(Integer.valueOf(FormStructureModalReview.getId()));


        Button addButton = new Button(getContext());
        addButton.setText("Add More");
        addButton.setId(Integer.valueOf(FormStructureModalReview.getId()));
        addButton.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));


        handleEffectBranchingLogic(FormStructureModalReview, buttonLayout);


        Button removeButton = new Button(getContext());
        removeButton.setText("Remove");
        removeButton.setId(0);
        removeButton.setVisibility(View.INVISIBLE);
        removeButton.setTag(FormStructureModalReview.getId());
        removeButton.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        buttonLayout.addView(addButton);
        buttonLayout.addView(removeButton);


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                removeButton.setVisibility(VISIBLE);

                // Calculate the position to add the layout
                int indexToAdd = binding.layout.indexOfChild(buttonLayout);

                // set section line

                stackIds = new Stack<>();
                if (counter >= addMoreLists.get(0).getAddMoreList().size()) {
                    return;
                }

                View line = new View(getContext());
                line.setId(0);
                line.setTag("-1");
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5 // Height of the line in pixels
                );
                params.setMargins(0, 15, 0, 0);
                line.setBackgroundColor(getContext().getResources().getColor(R.color.darkgrey));
                line.setLayoutParams(params);
                binding.layout.addView(line, indexToAdd++);

                for (AddMoreListReview obj : addMoreLists) {
                    try {
                        List<FormStructureModalReview> FormStructureModalReviews = obj.getAddMoreList().get(counter);
                        counter++;
                        for (int i = 0; i < FormStructureModalReviews.size(); i++) {

                            //Store All the ids which is to be remove
                            stackIds.push(FormStructureModalReviews.get(i).getId());
                            stackRepeatVal.push(FormStructureModalReviews.get(i).getRepeat());
                            Log.v("IDS=>", FormStructureModalReviews.get(i).getId() + " " + FormStructureModalReviews.get(i).getElement_type());
                            fromAdd = true;

                            if (FormStructureModalReviews.get(i).getElement_type().equalsIgnoreCase("section")) {
                                View viewSection = createLabelTextView(FormStructureModalReviews.get(i));
                                binding.layout.addView(viewSection, indexToAdd++);
                            } else if (FormStructureModalReviews.get(i).getElement_type().equalsIgnoreCase("label")) {
                                View viewLabel = createLabelTextView(FormStructureModalReviews.get(i));
                                binding.layout.addView(viewLabel, indexToAdd++);
                            } else if (FormStructureModalReviews.get(i).getElement_type().equalsIgnoreCase("text") || FormStructureModalReviews.get(i).getElement_type().equalsIgnoreCase("textarea")) {
                                View etLabel = createLabelEditTextView(FormStructureModalReviews.get(i));
                                binding.layout.addView(etLabel, indexToAdd++);
                                EditText view = createEditText(FormStructureModalReviews.get(i));
                                binding.layout.addView(view, indexToAdd++);

                                View elElement = createLabelEditElement(FormStructureModalReviews.get(i), view);
                                if (!FormStructureModalReviews.get(i).getFeedback().isEmpty()) {
                                    binding.layout.addView(elElement, indexToAdd++);
                                }

                            } else if (FormStructureModalReviews.get(i).getElement_type().equalsIgnoreCase("select")) {
                                View label = createLabelEditTextView(FormStructureModalReviews.get(i));
                                binding.layout.addView(label, indexToAdd++);
                                if (FormStructureModalReviews.get(i).getSelect_type().equalsIgnoreCase("global")) {
                                    Spinner spinner = createSpinnerForGlobal(FormStructureModalReviews.get(i));
                                    binding.layout.addView(spinner, indexToAdd++);

                                    View elElement = createLabelEditElement(FormStructureModalReviews.get(i), spinner);
                                    if (!FormStructureModalReviews.get(i).getFeedback().isEmpty()) {
                                        binding.layout.addView(elElement, indexToAdd++);
                                    }

                                } else {
                                    Spinner spinner = createSpinner(FormStructureModalReviews.get(i));
                                    binding.layout.addView(spinner, indexToAdd++);
                                }
                            } else if (FormStructureModalReviews.get(i).getElement_type().equalsIgnoreCase("radio")) {
                                TextView tv = createLabelRadio(FormStructureModalReviews.get(i));
                                binding.layout.addView(tv, indexToAdd++);
                                RadioGroup radioGroup = createRadioButton(FormStructureModalReviews.get(i));
                                binding.layout.addView(radioGroup, indexToAdd++);

                                View elElement = createLabelEditElement(FormStructureModalReviews.get(i), radioGroup);
                                if (!FormStructureModalReviews.get(i).getFeedback().isEmpty()) {
                                    binding.layout.addView(elElement, indexToAdd++);
                                }

                            } else if (FormStructureModalReviews.get(i).getElement_type().equalsIgnoreCase("checkbox")) {
                                TextView tv = createLabelCheckbox(FormStructureModalReviews.get(i));
                                binding.layout.addView(tv, indexToAdd++);

                                indexToAdd = createMultiCheckbox(FormStructureModalReviews.get(i), indexToAdd);

                                for (int k = 0; k < FormStructureModalReviews.get(i).getElement_choices().size() - 1; k++) {
                                    stackIds.push(FormStructureModalReviews.get(i).getId());
                                }
                            } else if (FormStructureModalReviews.get(i).getElement_type().equalsIgnoreCase("calculated_field")) {
                                View etLabel = createLabelEditTextView(FormStructureModalReviews.get(i));
                                binding.layout.addView(etLabel, indexToAdd++);
                                LinearLayout layout = createCalculatedField(FormStructureModalReviews.get(i));
                                binding.layout.addView(layout, indexToAdd++);

                                View elElement = createLabelEditElement(FormStructureModalReviews.get(i), layout);

                                if (!FormStructureModalReviews.get(i).getFeedback().isEmpty()) {
                                    Log.v("FeedbackComingFrom", FormStructureModalReviews.get(i).getFeedback());
                                    binding.layout.addView(elElement, indexToAdd++);
                                }
                            }


                            binding.layout.removeView(buttonLayout);

                            binding.layout.addView(buttonLayout, indexToAdd);


                        }
                        layoutAddedList.push(stackIds);

                        System.out.println("Object with ID " + FormStructureModalReview.getId() + ": " + obj.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Log.v("asfddsafdsfds", counter + " Counter");
                    Log.v("asfddsafdsfds", layoutAddedList.size() + " size");
                    if (counter == 1) {
                        removeButton.setVisibility(View.GONE);
                    }

                    removeFromID();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        int initialIndex = binding.layout.indexOfChild(addButton);
        binding.layout.addView(labelTextView);
        binding.layout.addView(buttonLayout);

        long itrCount = clickAddmoreIfAnyAnsFilled(addMoreLists);


        String noCount = getPrefilledData(FormStructureModalReview.getInterlink_question_id());

        Log.v("dsafdfdfdsf", noCount + "   qid=> " + FormStructureModalReview.getInterlink_question_id());
        Log.v("dsafdfdfdsf", itrCount + "   qid=> " + FormStructureModalReview.getInterlink_question_id());
        Log.v("dsafdfdfdsf", flag_for_disable_addmore + "   qid=> " + FormStructureModalReview.getInterlink_question_id());


        if (noCount != null && !noCount.equalsIgnoreCase("") && Integer.parseInt(noCount) != 0) {

            for (int j = 1; j < Integer.parseInt(noCount); j++) {
                addButton.performClick();
            }

            addButton.setClickable(false);
            removeButton.setClickable(false);

            addButton.setVisibility(View.GONE);
            removeButton.setVisibility(View.GONE);

        } else if (itrCount != 0) {
            for (int i = 0; i < itrCount; i++) {
                addButton.performClick();
            }

            addButton.setClickable(true);
            removeButton.setClickable(true);

            addButton.setVisibility(VISIBLE);
            removeButton.setVisibility(VISIBLE);

            if (flag_for_disable_addmore == 1) {
                flag_for_disable_addmore = 0;
                addButton.setClickable(false);
                removeButton.setClickable(false);

                addButton.setVisibility(View.GONE);
                removeButton.setVisibility(View.GONE);
            }
        } else {
            long n = clickAddmoreIfAnyAnsFilledItself(addMoreLists);
            Log.v("dsafdfdfdsf", n + "   qid=> " + FormStructureModalReview.getInterlink_question_id());

            for (int i = 0; i < n; i++) {
                addButton.performClick();
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private long clickAddmoreIfAnyAnsFilled(ArrayList<AddMoreListReview> addMoreLists) {

        long iterationCount = addMoreLists.stream()
                .flatMap(addMoreList -> addMoreList.getAddMoreList().stream())
                .filter(FormStructureModalReviews -> FormStructureModalReviews.stream()
                        .map(formStructureModal -> {
                            String ans = formStructureModal.getAnswers();
                            Log.v("dfdsfdsf Ans", ans);
                            if (ans == null || ans.trim().isEmpty() || ans.equals("0") || ans.equals("N/A")) {

                                String interlinkQuestionId = formStructureModal.getInterlink_question_id();

                                Log.v("dfdsfdsf interlinkId", interlinkQuestionId);


                                ans = getPrefilledData(interlinkQuestionId);

                                Log.v("dfdsfdsf interlinkans", ans);

                                if (ans.equalsIgnoreCase("N/A")) {
                                    ans = "";
                                }

                                if (ans != null && !ans.trim().isEmpty() && !ans.equals("0")) {
                                    flag_for_disable_addmore = 1;
                                }

                            } else {
                                String interlinkQuestionId = formStructureModal.getInterlink_question_id();
                                String newans = getPrefilledData(interlinkQuestionId);
                                if (newans.equalsIgnoreCase("N/A")) {
                                    newans = "";
                                }
                                if (newans != null && !newans.trim().isEmpty() && !newans.equals("0")) {
                                    flag_for_disable_addmore = 1;
                                }
                            }
                            return ans;
                        })
                        .anyMatch(ans -> ans != null && !ans.trim().isEmpty() && !ans.equals("0"))
                )
                .count();
        return iterationCount;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private long clickAddmoreIfAnyAnsFilledItself(ArrayList<AddMoreListReview> addMoreLists) {

        long iterationCount = addMoreLists.stream()
                .flatMap(addMoreList -> addMoreList.getAddMoreList().stream())
                .filter(FormStructureModalReviews -> FormStructureModalReviews.stream()
                        .map(formStructureModal -> {
                            String ans = getPrefilledData(formStructureModal.getId());
                            return ans;
                        })
                        .anyMatch(ans -> ans != null && !ans.trim().isEmpty() && !ans.equals("0"))
                )
                .count();
        return iterationCount;
    }

    private void removeFromID() {
        counter--;
        Log.v("Stack", "StackListSize" + layoutAddedList.size() + "");
        Log.v("Stack", "Layout Count" + binding.layout.getChildCount() + "");
        try {

            SurveyDao surveyDao = myDatabase.surveyDao();

            for (int i = 0; i < layoutAddedList.peek().size(); i++) {

                surveyDao.deletebyFormQuestionIdReview(layoutAddedList.peek().get(i), recid, formId);

                for (int j = 0; j < binding.layout.getChildCount(); j++) {

                    Log.v("Stack", "ID In STACK" + layoutAddedList.peek().get(i) + "");
                    Log.v("Stack", "ID In LAYOUT" + binding.layout.getChildAt(j).getId() + "");

                    if (Integer.valueOf(layoutAddedList.peek().get(i)) == binding.layout.getChildAt(j).getId()) {
                        Log.v("Stack", "Match");
                        binding.layout.removeView(binding.layout.getChildAt(j));

                    }


                }
            }

            int p = -1;

            try {
                for (int i = 0; i < layoutAddedList.peek().size(); i++) {
                    for (int j = 0; j < binding.layout.getChildCount(); j++) {
                        if (String.valueOf(binding.layout.getChildAt(j).getTag()).equalsIgnoreCase(layoutAddedList.peek().get(i))) {
                            binding.layout.removeView(binding.layout.getChildAt(j));
                        }

                        if (binding.layout.getChildAt(j) instanceof View && String.valueOf(binding.layout.getChildAt(j).getTag()).equalsIgnoreCase("-1")) {
                            p = j;
                        }

                    }
                }
                binding.layout.removeView(binding.layout.getChildAt(p));


            } catch (Exception e) {
                e.printStackTrace();
            }


            layoutAddedList.pop();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    //While Adding new Element checking if already visible the skip
    public boolean isVisibleField(String id) {
        try {
            for (int j = 0; j < binding.layout.getChildCount(); j++) {
                if (String.valueOf(binding.layout.getChildAt(j).getTag()).equalsIgnoreCase(id) || String.valueOf(binding.layout.getChildAt(j).getId()).equalsIgnoreCase(id)) {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private Spinner createSpinner(FormStructureModalReview FormStructureModalReview) {


        List<Item> choiceList = new ArrayList<>();
        Item item = new Item("0", "Select");
        choiceList.add(item);

        for (int i = 0; i < FormStructureModalReview.getElement_choices().size(); i++) {
            try {
                Item items = new Item(FormStructureModalReview.getElement_choices().get(i).getId(), FormStructureModalReview.getElement_choices().get(i).getName());
                choiceList.add(items);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Spinner spinner = new Spinner(getContext());
        spinner.setBackground(getResources().getDrawable(R.drawable.border));
        spinner.setId(Integer.valueOf(FormStructureModalReview.getId()));
        spinner.setTag(FormStructureModalReview.getVlookup_qustion_id());

        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getContext(), choiceList);
        spinner.setAdapter(spinnerAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long index) {

                handleCauseLogicForSpinner(FormStructureModalReview, position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        handleEffectBranchingLogic(FormStructureModalReview, spinner);


        try {
            if (choiceList.size() >= 2) {

                String pData = getPrefilledData(FormStructureModalReview.getId());

                if (!pData.isEmpty()) {
                    int pos = getSpinnerPosition(pData, choiceList);
                    spinner.setSelection(pos);
                } else {
                    int pos = getSpinnerPosition(FormStructureModalReview.getAnswers(), choiceList);
                    spinner.setSelection(pos);
                }


                Log.v("dgfvhjjvgf", pData + " Data");
            }
        } catch (Exception e) {
            String pData = FormStructureModalReview.getAnswers();
            Log.v("pData", pData);
            Log.v("pDataInstanceId", pData);
            Log.v("pDataqid", pData);
            Log.v("pDataformId", pData);

        }


        spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select Item");

                View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_dropdown_with_search, null);
                SearchView searchView = view.findViewById(R.id.searchView);
                updateSearchColor(searchView);
                ListView listView = view.findViewById(android.R.id.list);
                listView.setAdapter(spinnerAdapter);


                searchView.setQuery(lastSearchQuery, false);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        lastSearchQuery = newText;  // Save the search query
                        spinnerAdapter.getFilter().filter(newText);
                        return false;
                    }
                });

                builder.setView(view);
                AlertDialog dialog = builder.create();
                listView.setOnItemClickListener((parent, view1, position, id) -> {
                    spinner.setSelection(position);
                    dialog.dismiss();  // Dismiss the dialog on item click
                });


                dialog.show();
            }
            return true;
        });


        return spinner;

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Spinner createSpinnerForGlobal(FormStructureModalReview FormStructureModalReview) {


        List<Item> choiceList = new ArrayList<>();
        Item item = new Item("0", "Select");
        choiceList.add(item);

        if (FormStructureModalReview.getVlookup().equalsIgnoreCase("")) {
            Log.v("Testing", "vlookupId: " + FormStructureModalReview.getVlookup());
            GlobalDataSetValueDao globalDataSetValueDao = myDatabase.globalDataSetValueDao();
            List<GlobalDataSetValue> globalDataSetValues = globalDataSetValueDao.getByGlobalDataSetId(Integer.parseInt(FormStructureModalReview.getSelect_global_data_set_id()));
            for (GlobalDataSetValue globalDataSetValue : globalDataSetValues) {
                Item items = new Item(String.valueOf(globalDataSetValue.getId()), globalDataSetValue.getValue());
                choiceList.add(items);
            }

        } else {

            MapDependencyFieldDao mapDependencyFieldDao = myDatabase.mapDependencyFieldDao();
            MapDependencyField mapDependencyField = mapDependencyFieldDao.getDependencyByValue(Integer.parseInt(FormStructureModalReview.getVlookup()), Integer.parseInt(FormStructureModalReview.getSelect_global_data_set_id()));

            MapDependencyFieldValueDao mapDependencyFieldValueDao = myDatabase.mapDependencyFieldValueDao();
            List<MapDependencyFieldValue> mapDependencyFieldValue = mapDependencyFieldValueDao.getIdForMainTable(mapDependencyField.getId(), getParentSelectedId(FormStructureModalReview.getVlookup_qustion_id()));

            GlobalDataSetValueDao globalDataSetValueDao = myDatabase.globalDataSetValueDao();

            for (MapDependencyFieldValue mapDependencyFieldValue1 : mapDependencyFieldValue) {
                int secondary = mapDependencyFieldValue1.getGlobalDataSetValueIdSecondry();
                GlobalDataSetValue globalDataSetValues = globalDataSetValueDao.getById(secondary);

                Item items = new Item(String.valueOf(globalDataSetValues.getId()), globalDataSetValues.getValue());
                choiceList.add(items);

                Log.v("ssfsdsfd", items.getId() + " " + items.getName());

            }
        }


        Spinner spinner = new Spinner(getContext());
        spinner.setBackground(getResources().getDrawable(R.drawable.border));
        spinner.setId(Integer.valueOf(FormStructureModalReview.getId()));
        spinner.setTag(FormStructureModalReview.getVlookup_qustion_id());


        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getContext(), choiceList);
        spinner.setAdapter(spinnerAdapter);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long index) {

                handleCauseLogicForSpinner(FormStructureModalReview, position);

                try {
                    updatechildLayout(FormStructureModalReview.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        handleEffectBranchingLogic(FormStructureModalReview, spinner);


        spinner.post(() -> {
            try {
                String pData = getPrefilledData(FormStructureModalReview.getId());
                if (!pData.isEmpty()) {
                    int pos = getSpinnerPosition(pData, choiceList);
                    // Check if position is valid
                    if (pos >= 0 && pos < choiceList.size()) {
                        spinner.setSelection(pos);
                    } else {

                        Log.v("setSelectionError", "Invalid position: " + pos);
                    }
                } else {

                    int pos = getSpinnerPosition(FormStructureModalReview.getAnswers(), choiceList);

                    if (pos >= 0 && pos < choiceList.size()) {
                        Log.v("MyDebugPoint", "Set Pos: " + pos);

                        spinner.setSelection(pos);
                    } else {
                        Log.v("setSelectionError", "Invalid position: " + pos);
                    }


                }
                Log.v("dfdsgdsfgsd", choiceList.size() + "  Size");
                Log.v("dfdsgdsfgsd ", "Selected Data:  " + pData);

            } catch (Exception e) {

                String pData = FormStructureModalReview.getAnswers();
                Log.v("pData", pData);
                Log.v("pDataInstanceId", pData);
                Log.v("pDataqid", pData);
                Log.v("pDataformId", pData);
            }
        });

        // Setup the searchView
        spinner.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Select Item");

                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_dropdown_with_search, null);
                SearchView searchView = dialogView.findViewById(R.id.searchView);

                updateSearchColor(searchView);
                ListView listView = dialogView.findViewById(android.R.id.list);
                searchView.setQuery(lastSearchQueryGlobal, false);

                listView.setAdapter(spinnerAdapter);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        lastSearchQueryGlobal = newText;  // Save the search query
                        spinnerAdapter.getFilter().filter(newText);
                        return false;
                    }
                });

                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                listView.setOnItemClickListener((parent, view1, position, id) -> {
                    updatechildLayout(FormStructureModalReview.getId());
                    spinner.setSelection(position);
                    dialog.dismiss();  // Dismiss the dialog on item click
                });

                dialog.setOnDismissListener(dialogInterface -> {
                    // When the dialog is dismissed, save the search query
                    lastSearchQueryGlobal = searchView.getQuery().toString();
                });

                dialog.show();
            }
            return true;
        });

        return spinner;


    }

    private int getParentSelectedId(String qid) {

        for (int j = 0; j < binding.layout.getChildCount(); j++) {
            if (qid.equalsIgnoreCase(String.valueOf(binding.layout.getChildAt(j).getTag())) ||
                    qid.equalsIgnoreCase(String.valueOf(binding.layout.getChildAt(j).getId()))) {
                View view = binding.layout.getChildAt(j);
                if (view instanceof Spinner) {
                    Item selectedItem = (Item) ((Spinner) view).getSelectedItem();
                    if (selectedItem != null) {
                        Log.v("GloaldataSetLogic", "Value find in layout Spinner ID:  " + selectedItem);
                        return Integer.parseInt(selectedItem.getId());
                    }
                }
            }
        }


        return -1;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updatechildLayout(String pqid) {

        for (int j = 0; j < binding.layout.getChildCount(); j++) {
            View view = binding.layout.getChildAt(j);
            if (view instanceof Spinner) {

                List<FormStructureModalReview> FormStructureModalReviewListt = FormStructureModalReviewList.stream().filter(e -> e.getVlookup_qustion_id().equalsIgnoreCase(pqid)).collect(Collectors.toList());

                if (FormStructureModalReviewListt.size() > 0) {
                    String lookupId = String.valueOf(binding.layout.getChildAt(j).getTag());


                    if (pqid.equalsIgnoreCase(lookupId)) {

                        Log.v("MyDataList", FormStructureModalReviewListt.get(0).getElement_label() + " LookupId:  " + lookupId + " ParentQuestionID:  " + pqid);


                        List<Item> choiceList = new ArrayList<>();
                        Item item = new Item("0", "Select");
                        choiceList.add(item);

                        MapDependencyFieldDao mapDependencyFieldDao = myDatabase.mapDependencyFieldDao();
                        MapDependencyField mapDependencyField = mapDependencyFieldDao.getDependencyByValue(Integer.parseInt(FormStructureModalReviewListt.get(0).getVlookup()), Integer.parseInt(FormStructureModalReviewListt.get(0).getSelect_global_data_set_id()));

                        MapDependencyFieldValueDao mapDependencyFieldValueDao = myDatabase.mapDependencyFieldValueDao();

                        List<MapDependencyFieldValue> mapDependencyFieldValue = mapDependencyFieldValueDao.getIdForMainTable(mapDependencyField.getId(), getParentSelectedId(FormStructureModalReviewListt.get(0).getVlookup_qustion_id()));

                        GlobalDataSetValueDao globalDataSetValueDao = myDatabase.globalDataSetValueDao();

                        for (MapDependencyFieldValue mapDependencyFieldValue1 : mapDependencyFieldValue) {
                            int secondary = mapDependencyFieldValue1.getGlobalDataSetValueIdSecondry();
                            GlobalDataSetValue globalDataSetValues = globalDataSetValueDao.getById(secondary);

                            Item items = new Item(String.valueOf(globalDataSetValues.getId()), globalDataSetValues.getValue());
                            choiceList.add(items);
                        }


                        Log.v("dssdsdfds", choiceList.size() + "");

                        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getContext(), choiceList);
                        ((Spinner) view).setAdapter(spinnerAdapter);


                        ((Spinner) view).post(() -> {
                            try {
                                if (choiceList.size() > 2) {


                                    String pData = getPrefilledData(FormStructureModalReviewListt.get(0).getId());

                                    if (!pData.isEmpty()) {
                                        int pos = getSpinnerPosition(pData, choiceList);
                                        // Check if position is valid
                                        if (pos >= 0 && pos < choiceList.size()) {
                                            ((Spinner) view).setSelection(pos);
                                        } else {

                                            Log.v("setSelectionError", "Invalid position: " + pos);
                                        }
                                    } else {

                                        int pos = getSpinnerPosition(FormStructureModalReviewListt.get(0).getAnswers(), choiceList);

                                        if (pos >= 0 && pos < choiceList.size()) {
                                            Log.v("MyDebugPoint", "Set Pos: " + pos);

                                            ((Spinner) view).setSelection(pos);
                                        } else {
                                            Log.v("setSelectionError", "Invalid position: " + pos);
                                        }
                                    }
                                    Log.v("dfdsgdsfgsd", choiceList.size() + "  Size");
                                    Log.v("dfdsgdsfgsd ", "Selected Data:  " + pData);
                                }
                            } catch (Exception e) {
                                String pData = getPrefilledData(FormStructureModalReviewListt.get(0).getId());
                                Log.v("pData", pData);
                                Log.v("pDataInstanceId", pData);
                                Log.v("pDataqid", pData);
                                Log.v("pDataformId", pData);
                            }
                        });

                        ((Spinner) view).setOnTouchListener((v, event) -> {
                            if (event.getAction() == MotionEvent.ACTION_UP) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Select Item");

                                View vss = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_dropdown_with_search, null);
                                SearchView searchView = vss.findViewById(R.id.searchView);


                                updateSearchColor(searchView);


                                ListView listView = vss.findViewById(android.R.id.list);
                                listView.setAdapter(spinnerAdapter);

                                searchView.setQuery(lastSearchQueryGlobalReview, false);

                                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String query) {
                                        return false;
                                    }


                                    @Override
                                    public boolean onQueryTextChange(String newText) {
                                        lastSearchQueryGlobalReview = newText;  // Save the search query
                                        spinnerAdapter.getFilter().filter(newText);
                                        return false;
                                    }
                                });

                                builder.setView(vss);
                                AlertDialog dialog = builder.create();
                                listView.setOnItemClickListener((parent, view1, position, id) -> {
                                    updatechildLayout(FormStructureModalReviewListt.get(0).getId());

                                    ((Spinner) view).setSelection(position);
                                    dialog.dismiss();  // Dismiss the dialog on item click
                                });


                                dialog.show();
                            }
                            return true;
                        });

                    } else {

                        Log.v("OotOfBlock", "Out of Block:  " + pqid + "  :" + lookupId);
                    }
                }


            }
        }
    }


    private void updateSearchColor(SearchView searchView) {

        // Change the text color to black
        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        if (searchEditText != null) {
            searchEditText.setTextColor(Color.BLACK);
            searchEditText.setHintTextColor(Color.BLACK);
            // Optionally change the hint color
        } else {
            Log.e("SearchViewError", "Failed to find search_src_text EditText.");
        }

        // Change the search icon color
        ImageView searchIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        if (searchIcon != null) {
            searchIcon.setColorFilter(Color.BLACK);
        } else {
            Log.e("SearchViewError", "Failed to find search_mag_icon ImageView.");
        }

        ImageView closeButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        if (closeButton != null) {
            closeButton.setColorFilter(Color.BLACK);
        } else {
            Log.e("SearchViewError", "Failed to find search_close_btn ImageView.");
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private RadioGroup createRadioButton(FormStructureModalReview FormStructureModalReview) {
        List<Item> choiceList = new ArrayList<>();

        for (int i = 0; i < FormStructureModalReview.getElement_choices().size(); i++) {
            try {
                Item items = new Item(FormStructureModalReview.getElement_choices().get(i).getId(), FormStructureModalReview.getElement_choices().get(i).getName());
                choiceList.add(items);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        RadioGroup radioGroup = new RadioGroup(getContext());
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        radioGroup.setId(Integer.valueOf(FormStructureModalReview.getId()));

        for (int i = 0; i < choiceList.size(); i++) {
            Item choice = choiceList.get(i);
            RadioButton radioButton = new RadioButton(getContext());
            String input = choice.getName();


            if (input.contains("**") || input.contains("$$")) {
                radioButton.setGravity(TOP);
                List<OptionSplitter> options = splitOptions(input);
                StringBuilder builder = new StringBuilder();
                int k = 0;

                for (OptionSplitter option : options) {
                    if (k == 0) {
                        builder.append(option.getMainOption()).append("\n");
                    } else {
                        builder.append(k).append(". ").append(option.getMainOption()).append("\n");
                    }

                    for (int j = 0; j < option.getSubOptions().size(); j++) {
                        builder.append(k + 1).append(". ").append(option.getSubOptions().get(j)).append("\n");
                        k++;
                    }

                    // Increment `k` only after processing main option and its sub-options
                    k++;
                }

                radioButton.setText(builder.toString());
            } else {
                radioButton.setText(input);
            }


            radioButton.setId(Integer.parseInt(choice.getId()));
            radioButton.setTag(FormStructureModalReview.getId());
            radioButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black)); // Set text color
            radioButton.setButtonTintList(ColorStateList.valueOf(Color.BLACK)); // Set button color
            radioGroup.addView(radioButton);
        }


        handleEffectBranchingLogic(FormStructureModalReview, radioGroup);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Find the radio button by returned id
                RadioButton selectedRadioButton = group.findViewById(checkedId);
                if (selectedRadioButton != null) {
                    // Get the text associated with the selected radio button
                    String selectedText = selectedRadioButton.getText().toString();
                    Log.v("SelectedRadioButton", "Selected: " + selectedText);

                    // You can also get the ID or perform any other actions based on the selection
                    int selectedId = selectedRadioButton.getId();
                    Log.v("SelectedRadioButtonID", "Selected ID: " + selectedId);

                    handleCauseBranchingLogicRadio(FormStructureModalReview, selectedId, selectedText);


                }
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    String pData = getPrefilledData(FormStructureModalReview.getId());
                    if (pData != null && !pData.isEmpty()) {
                        int selectedId = Integer.parseInt(pData); // Convert pData to int
                        radioGroup.check(selectedId);
                        // Pre-select the radio button with the ID from pData
                    } else {
                        int selectedId = Integer.parseInt(FormStructureModalReview.getAnswers()); // Convert pData to int
                        radioGroup.check(selectedId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 100);


        return radioGroup;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showHideLaoyoutbyTag(String tag, boolean isShow) {
        int childCount = binding.layout.getChildCount(); // Cache child count
        List<FormStructureModalReview> newList = FormStructureModalReviewList.stream()
                .filter(e -> e.getId().equalsIgnoreCase(tag))
                .collect(Collectors.toList());

        if (newList.isEmpty()) {
            return; // Exit early if no matching FormStructureModalReview is found
        }

        FormStructureModalReview FormStructureModalReview = newList.get(0);

        for (int i = 0; i < childCount; i++) {
            View child = binding.layout.getChildAt(i);
            String childTag = String.valueOf(child.getTag());
            String childId = String.valueOf(child.getId());

            if (childTag.equalsIgnoreCase(tag) || childId.equalsIgnoreCase(tag)) {
                Log.v("Branching:IsShow", "Show Item " + childTag);

                if (isShow) {
                    if (child instanceof TextView && ((TextView) child).getText().toString().equalsIgnoreCase("Note* :")) {
                        child.setVisibility(View.GONE);
                        return; // Exit early if the TextView needs to be hidden
                    }
                    child.setVisibility(VISIBLE);
                } else {
                    child.setVisibility(View.GONE);
                    //resetViews(child);


                    if (checkEdtableViews(child)) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SurveyDao surveyDao = myDatabase.surveyDao();
                                surveyDao.deletebyFormQuestionIdReview(childId, recid, formId);
                            }
                        }).start();
                    }

                    try {
                        // Handle branching logic
                        List<BranchinglogicModal> branchingLogic = FormStructureModalReview.getCause_branching_logic();
                        if (!branchingLogic.isEmpty()) {
                            List<String> effectQuestionIdsToShow = new ArrayList<>();
                            List<String> effectQuestionIdsToHide = new ArrayList<>();

                            // Separate logic for showing and hiding
                            for (BranchinglogicModal logic : branchingLogic) {
                                String causeId = logic.getBranching().split("=")[1].trim();
                                if (causeId.equalsIgnoreCase(tag)) {
                                    effectQuestionIdsToShow.add(logic.getEffect_question_id());
                                } else {
                                    effectQuestionIdsToHide.add(logic.getEffect_question_id());
                                    // Perform database operation outside the loop
                                }
                            }

                            // Apply show/hide operations in batch
                            effectQuestionIdsToShow.forEach(effectId -> showHideLaoyoutbyTag(effectId, true));
                            effectQuestionIdsToHide.forEach(effectId -> showHideLaoyoutbyTag(effectId, false));
                        }
                    } catch (Exception e) {
                        Log.v("Exception:Cond", e.getMessage() + "  :" + e.getCause());
                    }
                }
            }
        }
    }


    private boolean checkEdtableViews(View child) {
        if (child instanceof RadioGroup) {
            return ((RadioGroup) child).isEnabled();
        }
        if (child instanceof Spinner) {
            return ((Spinner) child).isEnabled();
        }
        if (child instanceof EditText) {
            return ((EditText) child).isEnabled();
        }
        if (child instanceof CheckBox) {
            return ((CheckBox) child).isEnabled();
        }

        return true;

    }


    private void resetViews(View child) {
        if (child instanceof RadioGroup) {
            ((RadioGroup) child).clearCheck();
        }
        if (child instanceof Spinner) {
            ((Spinner) child).setSelection(0);
        }
        if (child instanceof EditText) {
            ((EditText) child).setText("");
        }
        if (child instanceof CheckBox) {
            ((CheckBox) child).setChecked(false);
        }

    }


    @SuppressLint("ResourceType")
    private int createMultiCheckbox(FormStructureModalReview FormStructureModalReview, int index) {
        try {

            for (int i = 0; i < FormStructureModalReview.getElement_choices().size(); i++) {
                String label = FormStructureModalReview.getElement_choices().get(i).getName();
                String id = FormStructureModalReview.getElement_choices().get(i).getId();
                boolean isChecked = false;
                CheckBox checkBox = createCheckBox(label, isChecked);
                checkBox.setId(Integer.valueOf(id));
                checkBox.setTag(FormStructureModalReview.getId());


                handleEffectBranchingLogic(FormStructureModalReview, checkBox);


                checkBox.setOnCheckedChangeListener((buttonView, isCheckeds) -> {

                    if (checkBox.getId() == 99) {

                        checkBoxOnCheckChanged(FormStructureModalReview, checkBox, isCheckeds);
                    }


                });


                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String pData = getPrefilledData(FormStructureModalReview.getId());
                            Log.v("fdsfdsfdsf", pData);
                            if (pData != null && !pData.isEmpty()) {
                                // Convert pData to int

                                pData = pData.replace("[","");
                                pData = pData.replace("]","");
                                pData = pData.replace("\"", "");


                                Log.v("fdsfdsfdsf", "hi"+pData);


                                String[] newData = pData.split(",");

                                Log.v("fdsfdsfdsf",  newData.length+"  size");
                                Log.v("fdsfdsfdsf",  String.valueOf(checkBox.getId()));

                                Boolean b =isElementPresent(newData, String.valueOf(checkBox.getId()));

                                Log.v("fdsfdsfdsf",  b+"  Status");



                                if (b) {

                                    Log.v("fdsfdsfdsf", "hii"+pData);

                                    checkBox.setChecked(true);

                                    if (checkBox.getId() == 99) {
                                        checkBoxOnCheckChanged(FormStructureModalReview, checkBox, true);
                                    }


                                }
                                // Pre-select the radio button with the ID from pData
                            } else {
                            }
                        } catch (Exception e) {
                            Log.v("sdsdgds", e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }, 100);


                if (index != -1) {
                    binding.layout.addView(checkBox, index++);
                } else {
                    binding.layout.addView(checkBox);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return index;
    }

    public static boolean isElementPresent(String[] array, String target) {
        if (array == null || target == null) {
            return false; // Handle null inputs safely
        }

        for(int i = 0; i<array.length; i++){

            if(Integer.parseInt(array[i]) == Integer.parseInt(target) ){
                return  true;
            }
        }

        // Use Arrays.asList to convert the array to a List for easier checking
        return false;
    }



    private void checkBoxOnCheckChanged(FormStructureModalReview FormStructureModalReview, CheckBox checkBox, Boolean isCheckeds) {
        try {
            if (FormStructureModalReview.getCause_branching_logic().size() > 0) {
                Log.v("Branching:data", FormStructureModalReview.getCause_branching_logic().size() + "");
                for (int j = 0; j < FormStructureModalReview.getCause_branching_logic().size(); j++) {
//                                    String cause_id = FormStructureModalReview.getCause_branching_logic().get(j).getBranching().split("=")[1].trim();

                    String[] cause_ids = extractCauseIds(FormStructureModalReview.getCause_branching_logic().get(j).getBranching());

                    if (Arrays.asList(cause_ids).contains(String.valueOf(checkBox.getId())) && isCheckeds) {

                        Log.v("Branching:Cond", "Show Item");
                        showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(j).getEffect_question_id(), true);
                    } else {
                        Log.v("Branching:Cond", "Hide Item");
                        showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(j).getEffect_question_id(), false);
                    }
                }
            }
        } catch (Exception e) {
            Log.v("Exception:Cond", e.getMessage() + "  :" + e.getCause());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private CheckBox createCheckBox(String label, boolean isChecked) {
        CheckBox checkBox = new CheckBox(getContext());
        checkBox.setText(label);
        checkBox.setChecked(isChecked);
        checkBox.setTextColor(getContext().getResources().getColor(R.color.black));
        int color = getContext().getResources().getColor(R.color.black); // Replace my_color with your color resource
        checkBox.getButtonDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        checkBox.setPadding(16, 25, 16, 25); // Padding in pixels (can convert to dp)

        return checkBox;
    }

    private View createLabelTextView(FormStructureModalReview FormStructureModalReview) {
        TextView textView = new TextView(getContext());
        textView.setTextSize(18);
        textView.setId(0);
        textView.setTag(FormStructureModalReview.getId());
        textView.setText(FormStructureModalReview.getElement_label());
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(layoutParams);
        textView.setTextColor(getContext().getResources().getColor(R.color.black));
        handleEffectBranchingLogic(FormStructureModalReview, textView);


        return textView;
    }


    private TextView createLabelCheckbox(FormStructureModalReview FormStructureModalReview) {

        String label = FormStructureModalReview.getElement_label();
        String req = "";
        int colorRed = ContextCompat.getColor(getContext(), R.color.red); // Define or use the actual color resource
        if (FormStructureModalReview.getElement_required().equalsIgnoreCase("1")) {
            req = "*";
        }
        String fullText = label + req;
        SpannableString spannableString = new SpannableString(fullText);
        if (!req.isEmpty()) {
            int start = fullText.indexOf(req);
            int end = start + req.length();
            spannableString.setSpan(new ForegroundColorSpan(colorRed), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        TextView headingTextView = new TextView(getContext());
        headingTextView.setText(spannableString);
        headingTextView.setId(0);
        headingTextView.setTag(FormStructureModalReview.getId());
        headingTextView.setTextColor(getContext().getResources().getColor(R.color.black));
        headingTextView.setTypeface(null, Typeface.BOLD);

        headingTextView.setLayoutParams(layoutParams);

        handleEffectBranchingLogic(FormStructureModalReview, headingTextView);


        return headingTextView;
    }


    private TextView createLabelRadio(FormStructureModalReview FormStructureModalReview) {

        String label = FormStructureModalReview.getElement_label();
        String req = "";
        int colorRed = ContextCompat.getColor(getContext(), R.color.red); // Define or use the actual color resource
        if (FormStructureModalReview.getElement_required().equalsIgnoreCase("1")) {
            req = "*";
        }
        String fullText = label + req;
        SpannableString spannableString = new SpannableString(fullText);
        if (!req.isEmpty()) {
            int start = fullText.indexOf(req);
            int end = start + req.length();
            spannableString.setSpan(new ForegroundColorSpan(colorRed), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        TextView labelTextView = new TextView(getContext());
        labelTextView.setText(spannableString);
        labelTextView.setId(0);
        labelTextView.setTag(FormStructureModalReview.getId());
        labelTextView.setTextColor(getContext().getResources().getColor(R.color.black));
        labelTextView.setTypeface(null, Typeface.BOLD);
        labelTextView.setLayoutParams(layoutParams);

        handleEffectBranchingLogic(FormStructureModalReview, labelTextView);


        return labelTextView;
    }


    private View createLabelEditTextView(FormStructureModalReview FormStructureModalReview) {
        String label = FormStructureModalReview.getElement_label();
        String req = "";
        int colorRed = ContextCompat.getColor(getContext(), R.color.red); // Define or use the actual color resource
        if (FormStructureModalReview.getElement_required().equalsIgnoreCase("1")) {
            req = "*";
        }
        String fullText = label + req;
        SpannableString spannableString = new SpannableString(fullText);
        if (!req.isEmpty()) {
            int start = fullText.indexOf(req);
            int end = start + req.length();
            spannableString.setSpan(new ForegroundColorSpan(colorRed), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        TextView labelTextView = new TextView(getContext());
        labelTextView.setText(spannableString);
        labelTextView.setId(0);
        labelTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        labelTextView.setTypeface(null, Typeface.BOLD);
        labelTextView.setLayoutParams(layoutParams);
        labelTextView.setTag(FormStructureModalReview.getId());

        handleEffectBranchingLogic(FormStructureModalReview, labelTextView);


        return labelTextView;
    }

    private View createLabelEditElement(FormStructureModalReview FormStructureModalReview, View editText) {

        TextView elementNote = new TextView(getContext());
        elementNote.setId(0);
        elementNote.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        elementNote.setTypeface(null, Typeface.BOLD);
        elementNote.setLayoutParams(layoutParams);
        elementNote.setTag(FormStructureModalReview.getId());
        elementNote.setTextSize(13);
        elementNote.setText("Feedback* :" + FormStructureModalReview.getFeedback());
        elementNote.setTextColor(getContext().getResources().getColor(R.color.red));
        handleEffectBranchingLogic(FormStructureModalReview, elementNote);
        return elementNote;
    }


    public void validateChar(EditText editText) {
        editText.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        // Loop through each character in the source text
                        for (int i = start; i < end; i++) {
                            // Check if the character is not a letter or space
                            if (!Character.isLetter(source.charAt(i)) && !Character.isSpaceChar(source.charAt(i))) {
                                // If not, return an empty string to discard the character
                                return "";
                            }
                        }
                        // If all characters are letters or spaces, return null to accept the input
                        return null;
                    }
                }
        });
    }


    public void validateInteger(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
    }

    public void validateIntegerDecimal(EditText editText) {
        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    }


    public void validateDigits(EditText editText, int digit) {
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        editText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(digit)
        });
    }


    private void showDatePickerDialog(EditText editText) {

        SimpleDateFormat dateFormatter = new SimpleDateFormat("d-M-yyyy", Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, monthOfYear, dayOfMonth);
            editText.setText(dateFormatter.format(selectedDate.getTime()));
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }


    private void showDateTimePickerDialog(EditText editText) {
        editText.setEnabled(false);
        editText.setFocusable(false);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("d-M-yyyy HH:mm", Locale.US);
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, monthOfYear, dayOfMonth) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, monthOfYear, dayOfMonth);
            editText.setText(dateFormatter.format(selectedDate.getTime()));
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }


    private LinearLayout createCalculatedField(FormStructureModalReview FormStructureModalReview) {
        // Create a LinearLayout to hold EditText and buttons


        LinearLayout layout = new LinearLayout(getContext());
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setId(Integer.valueOf(FormStructureModalReview.getId()));
        layout.setTag(FormStructureModalReview.getId());

        // Create EditText
        EditText editText = new EditText(getContext());
        editText.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1)); // Take remaining space
        editText.setTextColor(getContext().getResources().getColor(R.color.black));
        editText.setBackground(getResources().getDrawable(R.drawable.border));
        editText.setTextSize(14);
        editText.setHeight(70);
        editText.setTag(FormStructureModalReview.getId());
        editText.setId(Integer.valueOf(FormStructureModalReview.getId()));
        editText.setPadding(7, 0, 0, 0);


        if (FormStructureModalReview.getReadonly().equalsIgnoreCase("1")) {
            editText.setEnabled(false);
        } else {
            editText.setEnabled(true);

        }

        // Create Button 1
        Button button1 = new Button(getContext());
        button1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        button1.setText("CALCULATE");
        button1.setId(0);
        button1.setTag(FormStructureModalReview.getId());


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    float data = calculateLogic(FormStructureModalReview.getCalculation_logic());
                    editText.setText(data + "");
                } catch (Exception e) {
//                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        button1.performClick();

        // Add EditText and buttons to the layout
        layout.addView(editText);
        layout.addView(button1);

        handleEffectBranchingLogic(FormStructureModalReview, layout);


        return layout;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private EditText createEditText(FormStructureModalReview FormStructureModalReview) {
        EditText editText = new EditText(getContext());

        int validationType = Integer.valueOf(FormStructureModalReview.getElement_validation());
        int var = Integer.valueOf(FormStructureModalReview.getElement_validation() + FormStructureModalReview.getElement_required());
        String formattedNumber = String.format("%02d", var);
        Log.v("validation", FormStructureModalReview.getElement_validation() + " " + FormStructureModalReview.getElement_required() + " => " + formattedNumber);

        editText.setId(Integer.valueOf(FormStructureModalReview.getId()));

        editText.setTag(Integer.valueOf(formattedNumber));

        switch (validationType) {
            case 0:
                editText.setHint("Please Enter Details...");
                editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.darkgrey));
                break;
            case 1:
                editText.setFocusable(false);
                editText.setOnClickListener(v -> showDatePickerDialog(editText));
                editText.setHint("*Validation: Date");
                break;
            case 2:
                editText.setFocusable(false);
                editText.setOnClickListener(v -> showDateTimePickerDialog(editText));
                editText.setHint("*Validation: DateTime");

                break;
            case 3:
                editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.darkgrey));
                editText.setHint("*Validation: Email Address");
                break;
            case 4:
                if (Integer.valueOf(FormStructureModalReview.getMaximum()) == 0 && Integer.valueOf(FormStructureModalReview.getMinimum()) == 0) {
                    editText.setHint("*Validation: Integer");
                    editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.darkgrey));
                    validateInteger(editText);
                } else {
                    editText.setHint("*Validation: Integer b/w " + FormStructureModalReview.getMinimum() + " To " + FormStructureModalReview.getMaximum());
                    editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.darkgrey));
                    validateInteger(editText);

                }
                break;
            case 5:
                if (Integer.valueOf(FormStructureModalReview.getMaximum()) == 0 && Integer.valueOf(FormStructureModalReview.getMinimum()) == 0) {
                    editText.setHint("*Validation: Integer||Decimal");
                    editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.darkgrey));
                    validateIntegerDecimal(editText);
                } else {
                    editText.setFilters(new InputFilter[]{new FormStructureFragmentReview.InputFilterMinMax(Integer.valueOf(FormStructureModalReview.getMinimum()), Integer.valueOf(FormStructureModalReview.getMaximum()))});
                    editText.setHint("*Validation: Integer || Decimal b/w " + FormStructureModalReview.getMinimum() + " To " + FormStructureModalReview.getMaximum());
                    editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.darkgrey));
                    validateIntegerDecimal(editText);

                }
                break;
            case 6:
                editText.setHint("*Validation: Integer 10 Digits");
                editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.darkgrey));
                validateDigits(editText, 10);

                break;
            case 7:
                editText.setHint("*Validation: Only Characters");
                editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.darkgrey));
                validateChar(editText);

                break;
            case 8:
                if (Integer.valueOf(FormStructureModalReview.getMaximum()) == 0) {
                    editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.darkgrey));
                    editText.setHint("*Validation: Integer");
                    validateInteger(editText);
                } else {
                    editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.darkgrey));
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(FormStructureModalReview.getMaximum()))});
                    validateInteger(editText);

                    if (!FormStructureModalReview.getMaximum().equalsIgnoreCase("0")) {
                        editText.setHint("*Validation: Integer digits b/w " + FormStructureModalReview.getMinimum() + " To " + FormStructureModalReview.getMaximum());
                    }


                }
                break;
            default:
                break;
        }

        String pData = getPrefilledData(FormStructureModalReview.getId());
        Log.v("pDataSetValue", "fillData: " + pData);
        Log.v("pDataSetValue", FormStructureModalReview.getElement_label());
        Log.v("pDataSetValue", FormStructureModalReview.getId());
        if (!pData.isEmpty()) {
            Log.v("pDataSetValue", "Set value: " + FormStructureModalReview.getId());
            editText.setText(pData);
        } else {
            editText.setText(FormStructureModalReview.getAnswers());

        }


        editText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        editText.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border));
        editText.setTextSize(14);
        if (FormStructureModalReview.getElement_type().equalsIgnoreCase("textarea")) {
            editText.setHeight(190);
        } else {
            int height = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._35sdp);
            editText.setMinHeight(height);
        }
        editText.setPadding(7, 0, 0, 0);

        handleEffectBranchingLogic(FormStructureModalReview, editText);


        //Interlink logic is implemented here

        if (!FormStructureModalReview.getInterlink_question_id().equalsIgnoreCase("")) {
            editText.setEnabled(false);
            String data = getPrefilledData(FormStructureModalReview.getInterlink_question_id());
            String elementType = getViewInstanceByQuestionId(FormStructureModalReview.getInterlink_question_id());
            if (elementType.equalsIgnoreCase("text")) {
                if (data.equalsIgnoreCase("")) {
                    String value = getValueFromLayoutByQuestionId(FormStructureModalReview.getInterlink_question_id());
                    if (!value.equalsIgnoreCase("")) {
                        editText.setText(value);
                    }
                } else {
                    Log.v("MyDebuggingData", data + "  getValueFromDB");
                    editText.setText(data);
                }
            } else if (elementType.equalsIgnoreCase("select")) {
                if (data.equalsIgnoreCase("0") || data.equalsIgnoreCase("")) {
                    String value = getValueFromLayoutByQuestionIdSpinner(FormStructureModalReview.getInterlink_question_id());
                    if (!value.equalsIgnoreCase("") && !value.equalsIgnoreCase("0")) {
                        editText.setText(value);
                    } else {
                        editText.setText("N/A");
                    }
                } else {
                    Log.v("MyDebuggingData", data + "  getValueFromDB");
                    String res = getSpinnerNameFromQidFromValue(FormStructureModalReview.getInterlink_question_id(), data);
                    editText.setText(res);
                }
            } else if (elementType.equalsIgnoreCase("radio")) {
                if (data.equalsIgnoreCase("0") || data.equalsIgnoreCase("")) {
                    String value = getValueFromLayoutByQuestionIdSpinner(FormStructureModalReview.getInterlink_question_id());
                    if (!value.equalsIgnoreCase("") && !value.equalsIgnoreCase("0")) {
                        editText.setText(Utils.getSubstringBeforeDollar(value));
                    } else {
                        editText.setText("N/A");
                    }
                } else {
                    Log.v("MyDebuggingData", data + "  getValueFromDB");
                    String res = getSpinnerNameFromQidFromValue(FormStructureModalReview.getInterlink_question_id(), data);
                    editText.setText(Utils.getSubstringBeforeDollar(res));
                }
            }
        } else {
            editText.setEnabled(true);
        }


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called to notify you that the text is about to be changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                handleCauseLogicForEditText(FormStructureModalReview, editText);

            }
        });


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    handleCauseLogicForEditText(FormStructureModalReview, editText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 100);

        return editText;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private boolean validateEmpty() {
        boolean res = true;

        for (int i = 0; i < binding.layout.getChildCount(); i++) {

            if (binding.layout.getChildAt(i).getVisibility() == VISIBLE) {

                if (binding.layout.getChildAt(i) instanceof EditText) {
                    EditText editText = ((EditText) binding.layout.getChildAt(i));

                    String formattedNumber = String.format("%02d", binding.layout.getChildAt(i).getTag());
                    Log.v("validationmatch", formattedNumber + "");

                    String[] validationCondition = formattedNumber.split("");

                    Log.v("validationmatcharray", validationCondition[0] + " " + validationCondition[1]);

                    int validation;
                    int required;

                    if (!validationCondition[0].isEmpty()) {
                        validation = Integer.valueOf(validationCondition[0]);
                    } else {
                        // Handle the case where validationCondition[0] is empty
                        validation = 0; // or some other default value
                    }

                    if (!validationCondition[1].isEmpty()) {
                        required = Integer.valueOf(validationCondition[1]);
                    } else {
                        // Handle the case where validationCondition[1] is empty
                        required = 0; // or some other default value
                    }


                    if (required == 1) {

                        Log.v("Flow_1", validationCondition[1]);

                        if (editText.getText().toString().equalsIgnoreCase("")) {
                            // required true edittext can not be empty
                            if (editText.getVisibility() == VISIBLE) {
                                Log.v("Flow_2", editText.getText().toString());
                                Log.v("Flow_2", editText.getText().toString());
                                editText.setError("Please Enter The Value..");
                                res = false;
                            }
                        } else {
                            Log.v("Flow_3", editText.getText().toString());

                            if (validation == 0) {
                                Log.v("Flow_4", validation + "");
                            } else {
                                Log.v("Flow_5", validation + "");
                                switch (validation) {
                                    case 3: {
                                        if (!isValidEmail(editText.getText().toString()) && editText.getVisibility() == VISIBLE) {
                                            res = false;
                                        }
                                    }
                                    case 6: {
                                        if (editText.getText().toString().length() != 10 && editText.getVisibility() == VISIBLE) {
                                            Log.v("Flow_6", editText.getText().toString().length() + "");
                                            editText.setError("Please Enter The 10 digit Value...");
                                            res = false;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (editText.getText().toString().equalsIgnoreCase("")) {
                            Log.v("Flow_7", editText.getText().toString() + "");
                        } else {
                            Log.v("Flow_8", editText.getText().toString() + "");
                            if (validation == 0) {
                                Log.v("Flow_9", validation + "");
                            } else {
                                Log.v("Flow_10", validation + "");
                                switch (validation) {
                                    case 3: {
                                        if (!isValidEmail(editText.getText().toString()) && editText.getVisibility() == VISIBLE) {
                                            res = false;
                                        }
                                    }
                                    case 6: {
                                        if (editText.getText().toString().length() != 10 && editText.getVisibility() == VISIBLE) {
                                            editText.setError("Please Enter The 10 digit Value..");
                                            res = false;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //check for range
                    try {
                        int id = binding.layout.getChildAt(i).getId();
                        List<FormStructureModalReview> formStructure = FormStructureModalReviewList.stream()
                                .filter(e -> e.getId().equalsIgnoreCase(String.valueOf(id)))
                                .collect(Collectors.toList());
                        if (!formStructure.isEmpty() &&
                                !editText.getText().toString().equalsIgnoreCase("")) {

                            FormStructureModalReview structureModal = formStructure.get(0);
                            int min = Integer.parseInt(structureModal.getMinimum());
                            int max = Integer.parseInt(structureModal.getMaximum());
                            int filledData = Integer.parseInt(editText.getText().toString());


                            if (structureModal.getElement_validation().equalsIgnoreCase("4")) {
                                if (max != 0 && (filledData < min || filledData > max)) {
                                    editText.setError("Please enter value between " + min + " and " + max);
                                    res = false;
                                }
                            }

                            if (structureModal.getElement_validation().equalsIgnoreCase("8")) {
                                String filledDataString = editText.getText().toString();

                                if (max != 0 && (filledDataString.length() < min || filledDataString.length() > max)) {
                                    editText.setError("Please enter a value with digits between " + min + " and " + max);
                                    res = false;
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                if (binding.layout.getChildAt(i) instanceof Spinner) {
                    Spinner spinner = (Spinner) binding.layout.getChildAt(i);
                    Item selectedItem = (Item) spinner.getSelectedItem();
                    String id = String.valueOf(binding.layout.getChildAt(i).getId());

                    if (selectedItem != null) {
                        List<FormStructureModalReview> form = FormStructureModalReviewList.stream()
                                .filter(e -> e.getId().equalsIgnoreCase(id))
                                .collect(Collectors.toList());

                        if (!form.isEmpty() && form.get(0).getElement_required().equalsIgnoreCase("1") && selectedItem.getId().equalsIgnoreCase("0")) {
                            spinner.requestFocus();
                            TextView errorText = (TextView) spinner.getSelectedView();
                            if (errorText != null) {
                                errorText.setError("Please select an option");
                            }
                            res = false;
                        }
                    }
                }


                if (binding.layout.getChildAt(i) instanceof RadioGroup) {
                    RadioGroup radioGroup = (RadioGroup) binding.layout.getChildAt(i);
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    String id = String.valueOf(radioGroup.getId());

                    // Filter the list to find the form element based on id
                    List<FormStructureModalReview> form = FormStructureModalReviewList.stream()
                            .filter(e -> e.getId().equalsIgnoreCase(id))
                            .collect(Collectors.toList());

                    // Check if the form element is required and no radio button is selected
                    if (!form.isEmpty() && form.get(0).getElement_required().equalsIgnoreCase("1") && selectedId == -1) {
                        radioGroup.requestFocus(); // Set focus to the RadioGroup
                        Toast.makeText(getContext(), "Please select an option", Toast.LENGTH_SHORT).show(); // Show error message

                        // Optional: Highlight the RadioGroup to indicate error
                        for (int j = 0; j < radioGroup.getChildCount(); j++) {
                            View radioButton = radioGroup.getChildAt(j);
                            if (radioButton instanceof RadioButton) {
                                ((RadioButton) radioButton).setError("Please select an option");
                            }
                        }

                        res = false; // Set the result as false indicating a validation failure
                    }
                }





            }
        }




        return res;
    }


    private boolean validateEmptyCheckBox() {

        boolean res = true;

        boolean isGroupValid = false; // Flag to validate the current CheckBox group
        boolean isProcessingCheckBoxGroup = false; // Flag to track if we're inside a CheckBox group

        for (int i = 0; i < binding.layout.getChildCount(); i++) {
            if (binding.layout.getChildAt(i).getVisibility() == VISIBLE) {

                // Check if the current child is a CheckBox
                if (binding.layout.getChildAt(i) instanceof CheckBox) {


                  String  preQid = String.valueOf(binding.layout.getChildAt(i).getTag());

                    List<FormStructureModalReview> formStructure = FormStructureModalReviewList.stream()
                            .filter(e -> e.getId().equalsIgnoreCase(String.valueOf(preQid)))
                            .collect(Collectors.toList());

                    if(formStructure.get(0).getElement_required().equalsIgnoreCase("1")) {
                        isProcessingCheckBoxGroup = true; // We're now inside a CheckBox group
                        CheckBox checkBox = (CheckBox) binding.layout.getChildAt(i);

                        if (checkBox.isChecked()) {
                            isGroupValid = true; // At least one CheckBox in this group is checked
                        }
                    }
                } else {
                    // If we encounter a non-CheckBox element, it marks the end of the current CheckBox group
                    if (isProcessingCheckBoxGroup) {
                        // Validate the CheckBox group
                        if (!isGroupValid) {
                            Toast.makeText(getContext(), "Please select at least one option in the CheckBox group", Toast.LENGTH_SHORT).show();
                            res = false; // Validation failed
                        }
                        // Reset flags for the next CheckBox group
                        isGroupValid = false;
                        isProcessingCheckBoxGroup = false;
                    }
                }

                // (Include your existing EditText, Spinner, and RadioGroup validation logic here...)
            }
        }

        // Final validation for the last CheckBox group (if the loop ends inside a group)
        if (isProcessingCheckBoxGroup && !isGroupValid) {
            Toast.makeText(getContext(), "Please select at least one option in the CheckBox group", Toast.LENGTH_SHORT).show();
            res = false;
        }

        return res;
    }



    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    // Custom InputFilter to restrict input to a range of integers
    public class InputFilterMinMax implements InputFilter {

        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                // Concatenate the current text with the new input
                String input = dest.toString().substring(0, dstart) + source.subSequence(start, end) + dest.toString().substring(dend);

                // Parse the input to check if it's within the range
                int value = Integer.parseInt(input);

                if (isInRange(min, max, value)) {
                    return null; // Accept the input
                }
            } catch (NumberFormatException ignored) {
                // Ignore NumberFormatException
            }
            // Reject the input if it's outside the valid range
            return "";
        }

        // Helper method to check if a value is within a specified range
        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }


    public String getPrefilledData(String qid) {
        SurveyDao surveyDao = myDatabase.surveyDao();
        SurveyData row = surveyDao.getPredefinedAnswerReview(formId, recid, qid);
        if (row != null && !row.getField_value().isEmpty()) {
            return row.getField_value();
        }
        return "";
    }

    public String getPrefilledQData(String qid) {
        SurveyDao surveyDao = myDatabase.surveyDao();
        SurveyData row = surveyDao.getPredefinedAnswerReview(formId, recid, qid);
        if (row != null && !row.getQuestion_id().isEmpty()) {
            return row.getQuestion_id();
        }
        return "";
    }

    public String getPrefilledDatas(FormStructureModalReview formStructureModalReview) {
        return formStructureModalReview.getAnswers();
    }

    public int getSpinnerPosition(String val, List<Item> choiceList) {
        for (int i = 0; i < choiceList.size(); i++) {
            if (val.equalsIgnoreCase(choiceList.get(i).getId())) {
                return i;
            }
        }
        return 0;
    }

    private ArrayList<Float> getAnswerList(String calculationLogic) {
        String functionName = calculationLogic.substring(1, calculationLogic.indexOf('('));

        String parametersString = calculationLogic.substring(calculationLogic.indexOf('(') + 1, calculationLogic.indexOf(')'));

        String[] parameters = parametersString.split(",");

        Log.v("Function Name: ", functionName);

        Log.v("Function Name: ", Arrays.toString(parameters));

        List<String> parameterList = new ArrayList<>(Arrays.asList(parameters));

        Log.v("Function Name: ", parameterList.size() + "  parameter List Size");


        List<String> allQestionId = findQuestionIdFromElementVariable(parameterList);

        Log.v("Function Name: ", allQestionId.size() + "  Question List Size");

        ArrayList<Float> answerList = findAllValueFromLayoutAndDb(allQestionId);

        Log.v("Function Name: ", answerList.size() + "  Answerlist Size");

        return answerList;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private float calculateLogic(String calculationLogic) {

        String functionName = calculationLogic.substring(1, calculationLogic.indexOf('('));

        ArrayList<Float> answerList = getAnswerList(calculationLogic);

        String parametersString = calculationLogic.substring(calculationLogic.indexOf('(') + 1, calculationLogic.indexOf(')'));

        String[] parameters = parametersString.split(",");


        if (functionName.equalsIgnoreCase("MISS_SUM")) {
            float sum = answerList.stream()
                    .reduce(0.0f, Float::sum);
            Log.v("Function Name: ", sum + "  Total Sum");
            return sum;
        }

        if (functionName.equalsIgnoreCase("MISS_MULTIPLY")) {
            float mul = answerList.stream()
                    .filter(Objects::nonNull) // Filters out null values
                    .filter(a -> a != 0) // Optional: Filters out zero values if needed
                    .reduce(1.0f, (a, b) -> a * b);

            Log.v("Function Name: ", mul + "  Total Multiply");
            return answerList.isEmpty() ? 0 : mul; // Return 0 if the list is empty
        }

        if (functionName.equalsIgnoreCase("MISS_MINUS")) {
            float minus = answerList.stream()
                    .reduce((a, b) -> a - b)
                    .orElse(0.0f);

            Log.v("Function Name: ", minus + "  Total Minus");
            return minus;
        }

        if (functionName.equalsIgnoreCase("MISS_DIVIDE")) {
            Optional<Float> result = answerList.stream()
                    .reduce((a, b) -> a / b);

            float divideResult = result.orElse(0.0f);

            Log.v("Function Name: ", divideResult + "  Total Divide");
            return divideResult;
        }

        // Handle MISS_PERCENTAGE function
        if (functionName.equalsIgnoreCase("MISS_PERCENTAGE")) {
            if (parameters.length < 3) {
                throw new IllegalArgumentException("MISS_PERCENTAGE requires three parameters.");
            }

            float totalValue;
            float percentageValue;
            int returnType;

            try {
                totalValue = answerList.get(0);
                percentageValue = answerList.get(1);
                returnType = Integer.parseInt(parameters[2].trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format in MISS_PERCENTAGE parameters.", e);
            }

            float result = (totalValue * percentageValue) / 100;

            if (returnType == 0) {
                // Return integer value
                return (int) result;
            } else {
                // Return decimal value
                return result;
            }
        }

        if (functionName.equalsIgnoreCase("MISS_PER_INC")) {
            if (answerList.size() >= 2) {
                float variable1 = answerList.get(0);
                float variable2 = answerList.get(1);

                float perIncResult = (variable1 / variable2) * 100;
                Log.v("Function Name: ", perIncResult + "  Total Percentage Increase");
                return perIncResult;
            } else {
                Log.v("Function Name: ", "Insufficient data for MISS_PER_INC");
                return 0.0f;
            }
        }

        if (functionName.equalsIgnoreCase("MISS_GAP_PER")) {
            if (answerList.size() >= 2) {
                float variable1 = answerList.get(0);
                float variable2 = answerList.get(1);

                float gapPerResult = ((variable1 - variable2) / variable1) * 100;
                Log.v("Function Name: ", gapPerResult + "  Total Gap Percentage");
                return gapPerResult;
            } else {
                Log.v("Function Name: ", "Insufficient data for MISS_GAP_PER");
                return 0.0f;
            }
        }


        if (functionName.equalsIgnoreCase("MISS_INTDEC")) {


            try {
                FunctionProcessor functionProcessor = new FunctionProcessor();
                String val = functionProcessor.processMISSFunction(calculationLogic, answerList);
                return Float.valueOf(val);
            } catch (Exception e) {
                Log.v("Function Name: ", e.getMessage() + "  " + e.getCause());
            }
        }

        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<String> findQuestionIdFromElementVariable(List<String> parameterList) {
        List<String> allQestionId = new ArrayList<>();
        for (String elementVariable : parameterList) {
            if (!(elementVariable.equalsIgnoreCase("0") || elementVariable.equalsIgnoreCase("1"))) {
                try {
                    String str = FormStructureModalReviewList.stream()
                            .filter(e -> e.getElement_variable().trim().equalsIgnoreCase(elementVariable.trim()))
                            .map(FormStructureModalReview::getId)
                            .findFirst()
                            .get();

                    allQestionId.add(str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return allQestionId;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<Float> findAllValueFromLayoutAndDb(List<String> allQestionId) {

        ArrayList<Float> answerList = new ArrayList<>();

        for (String qid : allQestionId) {

            Log.v("MyDebuggingData", qid + "  Question Id");

            String data = getPrefilledData(qid);

            Log.v("MyDebuggingData", data + "  PrefilledData");

            if (data.equalsIgnoreCase("")) {

                String value = getValueFromLayoutByQuestionId(qid);

                Log.v("MyDebuggingData", value + "  getValueFromPage");


                if (!value.equalsIgnoreCase("")) {
                    answerList.add(Float.parseFloat(value));
                }
            } else {

                Log.v("MyDebuggingData", data + "  getValueFromDB");

                answerList.add(Float.parseFloat(data));
            }


        }
        return answerList;

    }


    private String getValueFromLayoutByQuestionId(String qid) {
        try {
            for (int j = 0; j < binding.layout.getChildCount(); j++) {
                View view = binding.layout.getChildAt(j);

                if (String.valueOf(view.getId()).equalsIgnoreCase(qid)) {
                    Log.v("MyDebuggingData", qid + "=" + view.getId() + " found");

                    if (view instanceof LinearLayout) {
                        LinearLayout linearLayout = (LinearLayout) view;
                        for (int k = 0; k < linearLayout.getChildCount(); k++) {
                            View childView = linearLayout.getChildAt(k);
                            if (childView instanceof EditText) {
                                Log.v("MyDebuggingData", ((EditText) childView).getText().toString() + " EditText found");
                                return ((EditText) childView).getText().toString();
                            }
                        }
                    } else if (view instanceof EditText) {
                        Log.v("MyDebuggingData", ((EditText) view).getText().toString() + " EditText found");
                        return ((EditText) view).getText().toString();
                    }
                }
            }
        } catch (Exception e) {
            Log.v("FormStructureFragment", e.getMessage());
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return "";
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getViewInstanceByQuestionId(String qid) {
        List<FormStructureModalReview> FormStructureModalReviews = FormStructureModalReviewList.stream().filter(e -> e.getId().equalsIgnoreCase(qid)).collect(Collectors.toList());
        return FormStructureModalReviews.get(0).getElement_type();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private String getValueFromLayoutByQuestionIdSpinner(String qid) {
        try {
            for (int j = 0; j < binding.layout.getChildCount(); j++) {

                View view = binding.layout.getChildAt(j);

                if (String.valueOf(binding.layout.getChildAt(j).getId()).equalsIgnoreCase(qid)) {

                    if (view instanceof Spinner) {

                        Item selectedItem = (Item) ((Spinner) view).getSelectedItem();
                        if (selectedItem != null) {
                            String name = selectedItem.getName();
                            return name;
                        }
                    }


                    if (view instanceof RadioGroup) {
                        // Get the ID of the selected RadioButton
                        int selectedId = ((RadioGroup) view).getCheckedRadioButtonId();
                        // Find the RadioButton by its ID
                        RadioButton selectedRadioButton = ((RadioGroup) view).findViewById(selectedId);
                        if (selectedRadioButton != null) {
                            String selectedText = selectedRadioButton.getText().toString();
                            return selectedText;
                        }
                    }


                }
            }
        } catch (Exception e) {
            Log.v("FormStructureFragment", e.getMessage());
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return "";
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private String getSpinnerNameFromQidFromValue(String qid, String itemId) {

        try {
            if (itemId.equalsIgnoreCase("99")) {
                List<FormStructureModalReview> FormStructureModalReviews = FormStructureModalReviewList.stream().filter(e -> e.getId().equalsIgnoreCase(qid)).collect(Collectors.toList());
                List<ElementChoice> items = FormStructureModalReviews.get(0).getElement_choices();
                List<ElementChoice> elementChoices = items.stream().filter(e -> e.getId().equalsIgnoreCase(itemId)).collect(Collectors.toList());
                List<BranchinglogicModal> causeLogic = FormStructureModalReviews.get(0).getCause_branching_logic().stream().filter(e -> e.getBranching().contains(itemId)).collect(Collectors.toList());
                String efId = causeLogic.get(0).getEffect_question_id();
                String ans = getPrefilledData(efId);
                return elementChoices.get(0).getName() + " - " + ans;

            } else {
                List<FormStructureModalReview> FormStructureModalReviews = FormStructureModalReviewList.stream().filter(e -> e.getId().equalsIgnoreCase(qid)).collect(Collectors.toList());
                List<ElementChoice> items = FormStructureModalReviews.get(0).getElement_choices();
                List<ElementChoice> elementChoices = items.stream().filter(e -> e.getId().equalsIgnoreCase(itemId)).collect(Collectors.toList());
                return elementChoices.get(0).getName();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getCurrentIndex() {
        try {
            SurveyDao surveyDao = myDatabase.surveyDao();
            SurveyData s = surveyDao.getLastEntryByRecId(formId, recid);


            if (s != null && s.getQuestion_id() != null) {

                String lastQid = s.getQuestion_id();

                OptionalInt sectionIndex = IntStream.range(0, list.size())
                        .filter(i -> list.get(i).stream().anyMatch(form -> form.getId().equals(lastQid)))
                        .findFirst();
                if (sectionIndex.isPresent()) {
                    System.out.println("QID found in section index: " + sectionIndex.getAsInt());
                    currentPageIndex = 0;
                    parseData(0);
                } else {
                    System.out.println("QID found in section index: 0");
                    currentPageIndex = 0;
                    parseData(0);
                }
            } else {

                inserAllQuestionIdToSync();

                parseData(0);
            }
        } catch (Exception e) {
            currentPageIndex = 0;
            parseData(0);
        }
    }


    private void updateProgressBar(int currentSection) {
        try {
            int totalSections = list.size();
            int progress = (int) (((double) currentSection / totalSections) * 100);
            binding.progressBar.setProgress(progress);
            binding.progressText.setText("Progress: " + progress + "%");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<OptionSplitter> splitOptions(String input) {
        return Arrays.stream(input.split("\\$\\$"))
                .filter(part -> !part.trim().isEmpty()) // Filter out empty or whitespace-only parts
                .map(part -> {
                    String[] subParts = part.split("\\*\\*");
                    String mainOption = subParts[0].trim();
                    List<String> subOptions = Arrays.stream(subParts)
                            .skip(1)
                            .filter(subPart -> !subPart.trim().isEmpty()) // Filter out empty sub-options
                            .map(String::trim)
                            .collect(Collectors.toList());
                    return new OptionSplitter(mainOption, subOptions);
                })
                .collect(Collectors.toList());
    }


    private String[] extractCauseIds(String branching) {
        // Initialize an array to hold the parts
        String[] parts = null;

        // Check if the string contains '=', '>', or '<' and split accordingly
        if (branching.contains("=")) {
            parts = branching.split("=");
        } else if (branching.contains(">")) {
            parts = branching.split(">");
        } else if (branching.contains("<")) {
            parts = branching.split("<");
        }

        // If a split occurred, process the second part
        if (parts != null && parts.length > 1) {


            return parts[1].trim().split("\\|");
        }

        // Return an empty array if no valid split occurred
        return new String[0];
    }


    public void handleEffectBranchingLogic(FormStructureModalReview FormStructureModalReview, View v) {
        try {
            if (FormStructureModalReview.getEffect_branching_logic().isEmpty()) {
                v.setVisibility(View.VISIBLE);
                return;
            }

            v.setVisibility(View.GONE);
            String branch = FormStructureModalReview.getEffect_branching_logic().get(0).getBranching();
            String[] causeIds = extractCauseIds(branch);

            for (String causeId : causeIds) {
                int numericCauseId = Integer.parseInt(causeId.replaceAll("\\D", ""));
                String selectedId;

                if (branch.contains(">")) {
                    selectedId = getSpinnerNameFromQidFromValue(
                            FormStructureModalReview.getCause_branching_logic().get(0).getEffect_question_id(), causeId);
                    if (Integer.parseInt(selectedId) > numericCauseId) {
                        v.setVisibility(View.VISIBLE);
                        break;
                    }
                } else if (branch.contains("<")) {
                    selectedId = getSpinnerNameFromQidFromValue(
                            FormStructureModalReview.getCause_branching_logic().get(0).getEffect_question_id(), causeId);
                    if (Integer.parseInt(selectedId) < numericCauseId) {
                        v.setVisibility(View.VISIBLE);
                        break;
                    }
                } else if (branch.contains("|")) {
                    String id = getPrefilledData(FormStructureModalReview.getEffect_branching_logic().get(0).getCause_question_id());
                    if (Arrays.asList(causeIds).contains(id)) {
                        v.setVisibility(View.VISIBLE);
                        break;
                    }
                } else {
                    selectedId = getPrefilledData(FormStructureModalReview.getEffect_branching_logic().get(0).getCause_question_id());
                    if (Integer.parseInt(selectedId) == numericCauseId) {
                        v.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Log.v("FoundData", e.getMessage(), e);
        }
    }


    public void handleCauseLogicForEditText(FormStructureModalReview FormStructureModalReview, EditText editText) {
        try {

            if (FormStructureModalReview.getCause_branching_logic().size() > 0) {

                Log.v("Branching:data", FormStructureModalReview.getCause_branching_logic().size() + "");
                for (int i = 0; i < FormStructureModalReview.getCause_branching_logic().size(); i++) {
                    //String cause_id = FormStructureModalReview.getCause_branching_logic().get(i).getBranching().split("=")[1].trim();


                    String[] cause_ids = extractCauseIds(FormStructureModalReview.getCause_branching_logic().get(i).getBranching());
                    String branch = FormStructureModalReview.getCause_branching_logic().get(i).getBranching();


                    for (String cause_id : cause_ids) {

                        Log.v("FoundData", cause_id);


                        int numericSelectedId = Integer.parseInt(editText.getText().toString());
                        int numericCauseId = Integer.parseInt(cause_id.replaceAll("[^\\d]", "")); // Remove non-numeric characters

                        Log.v("FoundData", numericSelectedId + "");
                        Log.v("FoundData", numericCauseId + "");

                        if (branch.contains(">")) {
                            Log.v("FoundData", numericSelectedId + "  >" + numericCauseId);
                            if (numericSelectedId > numericCauseId) {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), false);
                                break;
                            }
                        } else if (branch.contains("<")) {
                            if (numericSelectedId < numericCauseId) {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), false);
                                break;
                            }
                        } else {
                            if (numericSelectedId == numericCauseId) {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), false);
                                break;
                            }
                        }
                    }


                }

            }
        } catch (Exception e) {
            Log.v("Exception:Cond", e.getMessage() + "  :" + e.getCause());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void handleCauseLogicForSpinner(FormStructureModalReview FormStructureModalReview, int position) {
        try {

            if (FormStructureModalReview.getCause_branching_logic().size() > 0) {

                Log.v("Branching:data", FormStructureModalReview.getCause_branching_logic().size() + "");
                for (int i = 0; i < FormStructureModalReview.getCause_branching_logic().size(); i++) {
                    //String cause_id = FormStructureModalReview.getCause_branching_logic().get(i).getBranching().split("=")[1].trim();


                    String[] cause_ids = extractCauseIds(FormStructureModalReview.getCause_branching_logic().get(i).getBranching());
                    String branch = FormStructureModalReview.getCause_branching_logic().get(i).getBranching();


                    for (String cause_id : cause_ids) {

                        Log.v("FoundDataCauseID", cause_id);

                        if (position == 0) {
                            showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), false);
                            continue;
                        }


                        int numericSelectedId = Integer.parseInt(FormStructureModalReview.getElement_choices().get(position - 1).getId());
                        int numericCauseId = Integer.parseInt(cause_id.replaceAll("[^\\d]", "")); // Remove non-numeric characters

                        Log.v("FoundData", numericSelectedId + "");
                        Log.v("FoundData", numericCauseId + "");

                        if (branch.contains(">")) {
                            Log.v("FoundData", numericSelectedId + "  >" + numericCauseId);
                            numericSelectedId = Integer.parseInt(FormStructureModalReview.getElement_choices().get(position - 1).getName());

                            if (numericSelectedId > numericCauseId) {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), false);
                                break;
                            }
                        } else if (branch.contains("<")) {
                            numericSelectedId = Integer.parseInt(FormStructureModalReview.getElement_choices().get(position - 1).getName());

                            if (numericSelectedId < numericCauseId) {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), false);
                                break;
                            }
                        } else if (branch.contains("|")) {
                            if (position != 0 && Arrays.asList(cause_ids).contains(FormStructureModalReview.getElement_choices().get(position - 1).getId())) {
                                Log.v("Branching:Cond", "Show Item");
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), true);
                            } else {
                                Log.v("Branching:Cond", "Hide Item");
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), false);
                            }
                        } else {
                            numericSelectedId = Integer.parseInt(FormStructureModalReview.getElement_choices().get(position - 1).getId());
                            if (numericSelectedId == numericCauseId) {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), false);
                                break;
                            }
                        }
                    }


                }

            }
        } catch (Exception e) {
            Log.v("Exception:Cond", e.getMessage() + "  :" + e.getCause());
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void handleCauseBranchingLogicRadio(FormStructureModalReview FormStructureModalReview, int selectedId, String selectedText) {
        try {
            if (FormStructureModalReview.getCause_branching_logic().size() > 0) {
                Log.v("Branching:data", FormStructureModalReview.getCause_branching_logic().size() + "");
                for (int i = 0; i < FormStructureModalReview.getCause_branching_logic().size(); i++) {
                    String[] cause_ids = extractCauseIds(FormStructureModalReview.getCause_branching_logic().get(i).getBranching());
                    String branch = FormStructureModalReview.getCause_branching_logic().get(i).getBranching();
                    for (String cause_id : cause_ids) {
                        Log.v("FoundData", cause_id);
                        int numericSelectedId = 0;
                        int numericCauseId = Integer.parseInt(cause_id.replaceAll("[^\\d]", "")); // Remove non-numeric characters

                        Log.v("FoundData", numericSelectedId + "");
                        Log.v("FoundData", numericCauseId + "");

                        if (branch.contains(">")) {
                            Log.v("FoundData", numericSelectedId + "  >" + numericCauseId);
                            numericSelectedId = Integer.parseInt(selectedText);

                            if (numericSelectedId > numericCauseId) {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), false);
                                break;
                            }
                        } else if (branch.contains("<")) {
                            numericSelectedId = Integer.parseInt(selectedText);

                            if (numericSelectedId < numericCauseId) {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), false);
                                break;
                            }
                        } else if (branch.contains("|")) {
                            if (Arrays.asList(cause_ids).contains(String.valueOf(selectedId))) {
                                Log.v("Branching:Cond", "Show Item");
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), true);
                            } else {
                                Log.v("Branching:Cond", "Hide Item");
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), false);
                            }
                        } else {
                            numericSelectedId = selectedId;
                            if (numericSelectedId == numericCauseId) {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(FormStructureModalReview.getCause_branching_logic().get(i).getEffect_question_id(), false);
                                break;
                            }
                        }
                    }

                }

            }
        } catch (Exception e) {
            Log.v("Exception:Cond", e.getMessage() + "  :" + e.getCause());
        }


    }


}