package com.android.gids.ui.home;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static android.view.Gravity.TOP;
import static android.view.View.VISIBLE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import androidx.fragment.app.Fragment;

import com.android.gids.AddMoreList;
import com.android.gids.CustomSpinnerAdapter;
import com.android.gids.ElementChoice;
import com.android.gids.FormListModal;
import com.android.gids.FormStructureModal;
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
import com.android.gids.databinding.FragmentFormStructureBinding;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FormStructureFragment extends Fragment {

    boolean fromAdd = false;
    FragmentFormStructureBinding binding;

    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final int SELECT_FILE = 2;

    int index;
    String json_data;

    LinearLayout.LayoutParams layoutParams;

    private int currentPageIndex = 0;

    List<List<FormStructureModal>> list;

    List<FormStructureModal> formStructureModalList;

    private String repeatValue = "";

    List<FormStructureModal> formStructureModalList1;

    int counter = 0;
    int Backcounter = 0;

    List<List<FormStructureModal>> addMoreList;


    List<AddMoreList> addMoreListList;

    Stack<String> stackIds;

    Stack<Stack<String>> layoutAddedList = new Stack<>();

    Stack<String> stackRepeatVal;


    SurveyRoomDatabase myDatabase;

    public String sectionId = "0";

    public String formId = "0";

    public String userId = "0";

    public String uuid = "0";

    public int instanceId = 0;


    SharedPreferences sharedPreferences;

    private String lastSearchQuery = "";

    private String lastSearchQueryGlobal = "";

    private String getLastSearchQueryChild = "";

    private ImageView imageView;


    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_STORAGE_PERMISSION = 101;

    int flag_for_disable_addmore = 0;

    ImageView elementImage;

    String file_name = "";


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFormStructureBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        try {
            sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
            userId = sharedPreferences.getString("id", "");
            myDatabase = SurveyRoomDatabase.getInstance(getContext());
            instanceId = getArguments().getInt("instanceId");
            uuid = getArguments().getString("uuid");
            index = getArguments().getInt("index");
            formId = getArguments().getString("from_id");
            json_data = Utils.getRawJSONFromDB(getContext());
            Log.v("FormStructureFragment:", "JSON String Recieved: " + json_data);
            Log.v("FormId:", "JFormId: " + formId);
            Gson gson = new Gson();
            FormListModal data = gson.fromJson(json_data.toString(), FormListModal.class);
            formStructureModalList = data.getGIDS_SURVEY_APP().getDataList().get(index).getFormStructure();
            Log.v("FormStructureFragment: ", formStructureModalList.size() + " Size");

            prepareData();

            binding.tvProjectName.setText(data.getGIDS_SURVEY_APP().getDataList().get(index).getName());
            layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 20, 0, 20);

            getCurrentIndex();

            binding.nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        try {
                            binding.loadingAnim.setVisibility(VISIBLE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void run() {
                                if (validateEmpty()) {
                                    try {
                                        createLayoutFromJson();
                                    } catch (Exception e) {
                                        Toast.makeText(getContext(), e.getMessage() + " " + e.getCause(), Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }

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
                        Toast.makeText(getContext(), e.getMessage() + " : " + e.getCause(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


            binding.finalSubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showConfirmDialog();

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
            Log.v("FormStructureFragment:", e.getMessage());
        }


        return root;
    }


    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("FINAL SUBMIT");
        builder.setMessage("FINAL SUBMIT?");
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            public void onClick(DialogInterface dialog, int which) {
                createLayoutFromJson();
                InstanceStatus instanceStatus = new InstanceStatus();
                instanceStatus.setInstance_id(instanceId);
                instanceStatus.setIsSubmitted(1);
                instanceStatus.setForm_id(formId);
                instanceStatus.setUuid(uuid);
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


    public void prepareData() {
        //list => List<List<FormStructureModal>> => [ sec1[{},{}] , sec2[{},{}]]
        list = new ArrayList<>();
        List<FormStructureModal> formStructureModalList1 = new ArrayList<>();

        //We store the data section wise in list

        for (int i = 0; i < formStructureModalList.size(); i++) {
            try {
                if (((formStructureModalList.get(i).getElement_type().equalsIgnoreCase("section") && i != 0)) || i == formStructureModalList.size() - 1) {
                    Log.v("Itr", i + "");

                    if (i == formStructureModalList.size() - 1) {
                        formStructureModalList1.add(formStructureModalList.get(i));
                    }

                    list.add(formStructureModalList1);
                    formStructureModalList1 = new ArrayList<>();

                }
                formStructureModalList1.add(formStructureModalList.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (list.size() == 0 && formStructureModalList1.size() > 0) {
            list.add(formStructureModalList1);
            binding.finalSubmitButton.setVisibility(VISIBLE);
            binding.nextButton.setText("SAVE AND SUBMIT");
            //handle only one section
        }
        Log.v("FormStructureFragment:", formStructureModalList.size() + "");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createLayoutFromJson() {
        String checkBoxData = "";
        String preQid = "";
        List<SurveyData> surveyDataList = new ArrayList<>();

        try {
            for (int j = 0; j < binding.layout.getChildCount(); j++) {
                View view = binding.layout.getChildAt(j);

                if (view instanceof CheckBox) {
                    if (((CheckBox) view).isChecked()) {
                        preQid = String.valueOf(view.getTag());
                        checkBoxData = checkBoxData.isEmpty() ? String.valueOf(view.getId()) : checkBoxData + "," + view.getId();
                    }
                    continue;
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

            }
            // Perform the bulk insert/update operation
            addInDb(surveyDataList);

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
        surveyData.setInstance_id(instanceId);
        surveyData.setRecord_id(uuid);
        surveyData.setSource(Utils.PENDING_RECORD);
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
                Log.v("InsertedDataInDB", s.getField_value());
                SurveyData insertedData = surveyDao.getPredefinedAnswer(formId, instanceId, s.getQuestion_id());
                if (insertedData != null && !insertedData.getQuestion_id().isEmpty()) {
                    surveyDao.updateByFields(s.getQuestion_id(), instanceId, formId, s.getField_value());
                } else {
                    surveyDao.insert(s);
                }
            }
            //surveyDao.insertOrUpdateList(surveyDataList);
            Log.v("FormStructureFragment:", "SuccessFully Inserted");
        } catch (Exception e) {
            Log.v("FormStructureFragment:", e.getMessage());
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

        //List<FormStructureModal> => [{},{}]
        formStructureModalList1 = new ArrayList<>();

        //List<List<FormStructureModal>> => [[{},{},{}], [{},{},{}]]
        addMoreList = new ArrayList<>();

        //List<AddMoreList> => [  [[{},{},{}], [{},{},{}]],  [[{},{},{}], [{},{},{}]],  [[{},{},{}], [{},{},{}]],  [[{},{},{}], [{},{},{}]]  ]
        addMoreListList = new ArrayList<>();

        repeatValue = "";

        // => list : List<List<FormStructureModal>> : [[{},{}], [{},{}]]

        //First time index is zero

        //populate First Section Data


        for (int j = 0; j < list.get(index).size(); j++) {
            try {
                FormStructureModal formStructureModal = list.get(index).get(j);

                if (formStructureModal.getRepeat().equalsIgnoreCase("")) {

                    if (formStructureModalList1.size() > 0) {
                        addMoreList.add(new ArrayList<>(formStructureModalList1));
                        formStructureModalList1 = new ArrayList<>();
                    }

                    if (formStructureModal.getElement_type().equalsIgnoreCase("section")) {
                        sectionId = formStructureModal.getId();
                        View v = createLabelTextView(formStructureModal);
                        binding.layout.addView(v);
                    } else if (formStructureModal.getElement_type().equalsIgnoreCase("image")) {
                        View v = createImageElement(formStructureModal);
                        binding.layout.addView(v);
                        Button button = createButtonForImage(formStructureModal);
                        binding.layout.addView(button);
                    } else if (formStructureModal.getElement_type().equalsIgnoreCase("label")) {
                        View v = createLabelTextView(formStructureModal);
                        binding.layout.addView(v);
                    } else if (formStructureModal.getElement_type().equalsIgnoreCase("text") || formStructureModal.getElement_type().equalsIgnoreCase("textarea")) {
                        View etLabel = createLabelEditTextView(formStructureModal);
                        binding.layout.addView(etLabel);
                        EditText view = createEditText(formStructureModal);
                        binding.layout.addView(view);
                        View elElement = createLabelEditElement(formStructureModal, view);
                        binding.layout.addView(elElement);
                    } else if (formStructureModal.getElement_type().equalsIgnoreCase("select")) {

                        View label = createLabelEditTextView(formStructureModal);
                        binding.layout.addView(label);
                        if (formStructureModal.getSelect_type().equalsIgnoreCase("global")) {
                            Spinner spinner = createSpinnerForGlobal(formStructureModal);
                            binding.layout.addView(spinner);
                        } else {
                            Spinner spinner = createSpinner(formStructureModal);
                            binding.layout.addView(spinner);

                            if (spinner.getVisibility() == VISIBLE) {
                                label.setVisibility(VISIBLE);
                            }
                        }
                    } else if (formStructureModal.getElement_type().equalsIgnoreCase("radio")) {
                        TextView tv = createLabelRadio(formStructureModal);
                        binding.layout.addView(tv);
                        RadioGroup radioGroup = createRadioButton(formStructureModal);
                        binding.layout.addView(radioGroup);
                    } else if (formStructureModal.getElement_type().equalsIgnoreCase("checkbox")) {
                        TextView tv = createLabelCheckbox(formStructureModal);
                        binding.layout.addView(tv);
                        createMultiCheckbox(formStructureModal, -1);
                    } else if (formStructureModal.getElement_type().equalsIgnoreCase("calculated_field")) {

                        View etLabel = createLabelEditTextView(formStructureModal);
                        binding.layout.addView(etLabel);

                        LinearLayout layout = createCalculatedField(formStructureModal);
                        binding.layout.addView(layout);

                    } else if (formStructureModal.getElement_type().equalsIgnoreCase("repeat")) {
                        //It means before Enter in this  addMoreList  should be filled (it promiss that before Addmore element repeat element will be there)
                        AddMoreList addMoreListdata = new AddMoreList();
                        addMoreListdata.setAddMoreList(addMoreList);
                        addMoreListdata.setId(formStructureModal.getId());
                        addMoreListList.add(addMoreListdata);
                        createButton(formStructureModal);
                    }
                } else {
                    if (!formStructureModal.getRepeat().equalsIgnoreCase(repeatValue)) {
                        if (formStructureModalList1.size() > 0) {
                            addMoreList.add(new ArrayList<>(formStructureModalList1));
                        }
                        formStructureModalList1 = new ArrayList<>();
                        formStructureModalList1.add(formStructureModal);
                        repeatValue = formStructureModal.getRepeat();
                    } else {
                        formStructureModalList1.add(formStructureModal);
                        repeatValue = formStructureModal.getRepeat();
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

    public ArrayList<AddMoreList> getObjectsWithId(String targetId) {
        ArrayList<AddMoreList> objectsWithId = new ArrayList<>();
        for (AddMoreList obj : addMoreListList) {
            if (obj.getId().equalsIgnoreCase(targetId)) {
                objectsWithId.add(obj);
            }
        }
        return objectsWithId;
    }

    private void createButton(FormStructureModal formStructureModal) {

        stackIds = new Stack<>();
        stackRepeatVal = new Stack<>();

        ArrayList<AddMoreList> addMoreLists = getObjectsWithId(formStructureModal.getId());
        Backcounter = addMoreLists.get(0).getAddMoreList().size();
        counter = 0;

        TextView labelTextView = new TextView(getContext());
        labelTextView.setText(formStructureModal.getElement_label());
        labelTextView.setId(0);
        labelTextView.setTextColor(getContext().getResources().getColor(R.color.black));
        labelTextView.setTypeface(null, Typeface.BOLD);
        labelTextView.setLayoutParams(layoutParams);
        labelTextView.setVisibility(View.GONE);


        LinearLayout buttonLayout = new LinearLayout(getContext());
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setLayoutParams(layoutParams);
        buttonLayout.setId(Integer.valueOf(formStructureModal.getId()));


        Button addButton = new Button(getContext());
        addButton.setText("Add More");
        addButton.setId(Integer.valueOf(formStructureModal.getId()));
        addButton.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));


        handleEffectBranchingLogic(formStructureModal, buttonLayout);


        Button removeButton = new Button(getContext());
        removeButton.setText("Remove");
        removeButton.setId(0);
        removeButton.setVisibility(View.INVISIBLE);
        removeButton.setTag(formStructureModal.getId());
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

                for (AddMoreList obj : addMoreLists) {
                    try {
                        List<FormStructureModal> formStructureModals = obj.getAddMoreList().get(counter);
                        counter++;
                        for (int i = 0; i < formStructureModals.size(); i++) {

                            //Store All the ids which is to be remove
                            stackIds.push(formStructureModals.get(i).getId());
                            stackRepeatVal.push(formStructureModals.get(i).getRepeat());
                            Log.v("IDS=>", formStructureModals.get(i).getId() + " " + formStructureModals.get(i).getElement_type());
                            fromAdd = true;

                            if (formStructureModals.get(i).getElement_type().equalsIgnoreCase("section")) {
                                View viewSection = createLabelTextView(formStructureModals.get(i));
                                binding.layout.addView(viewSection, indexToAdd++);
                            } else if (formStructureModals.get(i).getElement_type().equalsIgnoreCase("label")) {
                                View viewLabel = createLabelTextView(formStructureModals.get(i));
                                binding.layout.addView(viewLabel, indexToAdd++);
                            } else if (formStructureModals.get(i).getElement_type().equalsIgnoreCase("text") || formStructureModals.get(i).getElement_type().equalsIgnoreCase("textarea")) {
                                View etLabel = createLabelEditTextView(formStructureModals.get(i));
                                binding.layout.addView(etLabel, indexToAdd++);
                                EditText view = createEditText(formStructureModals.get(i));
                                binding.layout.addView(view, indexToAdd++);
                                View elElement = createLabelEditElement(formStructureModals.get(i), view);
                                binding.layout.addView(elElement, indexToAdd++);
                            } else if (formStructureModals.get(i).getElement_type().equalsIgnoreCase("select")) {
                                View label = createLabelEditTextView(formStructureModals.get(i));
                                binding.layout.addView(label, indexToAdd++);
                                if (formStructureModals.get(i).getSelect_type().equalsIgnoreCase("global")) {
                                    Spinner spinner = createSpinnerForGlobal(formStructureModals.get(i));
                                    binding.layout.addView(spinner, indexToAdd++);
                                } else {
                                    Spinner spinner = createSpinner(formStructureModals.get(i));
                                    binding.layout.addView(spinner, indexToAdd++);
                                }
                            } else if (formStructureModals.get(i).getElement_type().equalsIgnoreCase("radio")) {
                                TextView tv = createLabelRadio(formStructureModals.get(i));
                                binding.layout.addView(tv, indexToAdd++);
                                RadioGroup radioGroup = createRadioButton(formStructureModals.get(i));
                                binding.layout.addView(radioGroup, indexToAdd++);
                            } else if (formStructureModals.get(i).getElement_type().equalsIgnoreCase("checkbox")) {
                                TextView tv = createLabelCheckbox(formStructureModals.get(i));
                                binding.layout.addView(tv, indexToAdd++);

                                indexToAdd = createMultiCheckbox(formStructureModals.get(i), indexToAdd);

                                for (int k = 0; k < formStructureModals.get(i).getElement_choices().size() - 1; k++) {
                                    stackIds.push(formStructureModals.get(i).getId());
                                }
                            } else if (formStructureModals.get(i).getElement_type().equalsIgnoreCase("calculated_field")) {
                                View etLabel = createLabelEditTextView(formStructureModals.get(i));
                                binding.layout.addView(etLabel, indexToAdd++);
                                LinearLayout layout = createCalculatedField(formStructureModals.get(i));
                                binding.layout.addView(layout, indexToAdd++);
                            }


                            binding.layout.removeView(buttonLayout);

                            binding.layout.addView(buttonLayout, indexToAdd);


                        }
                        layoutAddedList.push(stackIds);

                        System.out.println("Object with ID " + formStructureModal.getId() + ": " + obj.toString());
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


        String noCount = getPrefilledData(formStructureModal.getInterlink_question_id());

        Log.v("dfdsfdsf", noCount + "   qid=> " + formStructureModal.getInterlink_question_id());


        if (noCount != null && !noCount.equalsIgnoreCase("") && Integer.parseInt(noCount) != 0) {

            for (int j = 1; j < Integer.parseInt(noCount); j++) {
                addButton.performClick();
            }

            addButton.setClickable(false);
            removeButton.setClickable(false);

        } else {
            for (int i = 0; i < itrCount; i++) {
                addButton.performClick();
            }

            addButton.setClickable(true);
            removeButton.setClickable(true);

            if (flag_for_disable_addmore == 1) {
                flag_for_disable_addmore = 0;
                addButton.setClickable(false);
                removeButton.setClickable(false);
            }
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private long clickAddmoreIfAnyAnsFilled(ArrayList<AddMoreList> addMoreLists) {

        long iterationCount = addMoreLists.stream()
                .flatMap(addMoreList -> addMoreList.getAddMoreList().stream())
                .filter(formStructureModals -> formStructureModals.stream()
                        .map(formStructureModal -> {
                            String ans = getPrefilledData(formStructureModal.getId());
                            if (ans == null || ans.trim().isEmpty() || ans.equals("0") || ans.equals("N/A")) {
                                String interlinkQuestionId = formStructureModal.getInterlink_question_id();
                                ans = getPrefilledData(interlinkQuestionId);
                                if (ans.equalsIgnoreCase("N/A")) {
                                    ans = "";
                                }

                                if (ans != null && !ans.trim().isEmpty() && !ans.equals("0")) {
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

    private void removeFromID() {
        counter--;
        Log.v("Stack", "StackListSize" + layoutAddedList.size() + "");
        Log.v("Stack", "Layout Count" + binding.layout.getChildCount() + "");
        try {

            SurveyDao surveyDao = myDatabase.surveyDao();

            for (int i = 0; i < layoutAddedList.peek().size(); i++) {

                surveyDao.deletebyFormQuestionId(layoutAddedList.peek().get(i));

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


    private Spinner createSpinner(FormStructureModal formStructureModal) {
        List<Item> choiceList = new ArrayList<>();
        Item item = new Item("0", "Select");
        choiceList.add(item);

        for (int i = 0; i < formStructureModal.getElement_choices().size(); i++) {
            try {
                Item items = new Item(formStructureModal.getElement_choices().get(i).getId(),
                        formStructureModal.getElement_choices().get(i).getName());
                choiceList.add(items);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Spinner spinner = new Spinner(getContext());
        spinner.setBackground(getResources().getDrawable(R.drawable.border));
        spinner.setId(Integer.valueOf(formStructureModal.getId()));
        spinner.setTag(formStructureModal.getVlookup_qustion_id());

        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getContext(), choiceList);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long index) {
                handleCauseLogicForSpinner(formStructureModal, position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        handleEffectBranchingLogic(formStructureModal, spinner);

        try {
            if (choiceList.size() >= 2) {
                String pData = getPrefilledData(formStructureModal.getId());
                int pos = getSpinnerPosition(pData, choiceList);
                spinner.setSelection(pos);
                Log.v("dgfvhjjvgf", pData + " Data");
                Log.v("dgfvhjjvgf", pos + " Position");
            }
        } catch (Exception e) {
            String pData = getPrefilledData(formStructureModal.getId());
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


//                searchView.setQuery(lastSearchQuery, false);

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


    private Spinner createSpinnerForGlobal(FormStructureModal formStructureModal) {
        List<Item> choiceList = new ArrayList<>();
        Item item = new Item("0", "Select");
        choiceList.add(item);

        if (formStructureModal.getVlookup().equalsIgnoreCase("")) {
            Log.v("Testing", "vlookupId: " + formStructureModal.getVlookup());
            GlobalDataSetValueDao globalDataSetValueDao = myDatabase.globalDataSetValueDao();
            List<GlobalDataSetValue> globalDataSetValues = globalDataSetValueDao.getByGlobalDataSetId(Integer.parseInt(formStructureModal.getSelect_global_data_set_id()));
            for (GlobalDataSetValue globalDataSetValue : globalDataSetValues) {
                Item items = new Item(String.valueOf(globalDataSetValue.getId()), globalDataSetValue.getValue());
                choiceList.add(items);
            }
        } else {
            Log.v("Testing", "vlookupId: " + formStructureModal.getVlookup());
            Log.v("Testing", "globaldatasetId: " + formStructureModal.getSelect_global_data_set_id());
            MapDependencyFieldDao mapDependencyFieldDao = myDatabase.mapDependencyFieldDao();
            MapDependencyField mapDependencyField = mapDependencyFieldDao.getDependencyByValue(Integer.parseInt(formStructureModal.getVlookup()), Integer.parseInt(formStructureModal.getSelect_global_data_set_id()));

            Log.v("Testing", "First Query Result: " + mapDependencyField.getId());
            Log.v("Testing", "PreDefined Ans: " + getParentSelectedId(formStructureModal.getVlookup_qustion_id()));

            MapDependencyFieldValueDao mapDependencyFieldValueDao = myDatabase.mapDependencyFieldValueDao();
            List<MapDependencyFieldValue> mapDependencyFieldValue = mapDependencyFieldValueDao.getIdForMainTable(mapDependencyField.getId(), getParentSelectedId(formStructureModal.getVlookup_qustion_id()));

            GlobalDataSetValueDao globalDataSetValueDao = myDatabase.globalDataSetValueDao();

            for (MapDependencyFieldValue mapDependencyFieldValue1 : mapDependencyFieldValue) {
                int secondary = mapDependencyFieldValue1.getGlobalDataSetValueIdSecondry();
                GlobalDataSetValue globalDataSetValues = globalDataSetValueDao.getById(secondary);

                Item items = new Item(String.valueOf(globalDataSetValues.getId()), globalDataSetValues.getValue().replace("\n", ""));
                choiceList.add(items);
            }
        }

        Spinner spinner = new Spinner(getContext());
        spinner.setBackground(getResources().getDrawable(R.drawable.border));
        spinner.setId(Integer.valueOf(formStructureModal.getId()));
        spinner.setTag(formStructureModal.getVlookup_qustion_id());

        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getContext(), choiceList);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long index) {
                handleCauseLogicForSpinner(formStructureModal, position);

                try {
                    updatechildLayout(formStructureModal.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        handleEffectBranchingLogic(formStructureModal, spinner);

        spinner.post(() -> {
            try {
                if (choiceList.size() > 2) {
                    String pData = getPrefilledData(formStructureModal.getId());
                    int pos = getSpinnerPosition(pData, choiceList);

                    // Check if position is valid
                    if (pos >= 0 && pos < choiceList.size()) {
                        spinner.setSelection(pos);
                    } else {
                        Log.v("setSelectionError", "Invalid position: " + pos);
                    }

                    Log.v("dfdsgdsfgsd", choiceList.size() + " Size");
                    Log.v("dfdsgdsfgsd", formStructureModal.getElement_label() + " Selected Position: " + pos);
                    Log.v("dfdsgdsfgsd", "Selected Data: " + pData);
                }
            } catch (Exception e) {
                String pData = getPrefilledData(formStructureModal.getId());
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
//                searchView.setQuery(lastSearchQueryGlobal, false);

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
                    updatechildLayout(formStructureModal.getId());
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
        getLastSearchQueryChild = "";
        for (int j = 0; j < binding.layout.getChildCount(); j++) {
            View view = binding.layout.getChildAt(j);
            if (view instanceof Spinner) {

                List<FormStructureModal> formStructureModalListt = formStructureModalList.stream().filter(e -> e.getVlookup_qustion_id().equalsIgnoreCase(pqid)).collect(Collectors.toList());

                if (formStructureModalListt.size() > 0) {
                    String lookupId = String.valueOf(binding.layout.getChildAt(j).getTag());


                    if (pqid.equalsIgnoreCase(lookupId)) {

                        Log.v("MyDataList", formStructureModalListt.get(0).getElement_label() + " LookupId:  " + lookupId + " ParentQuestionID:  " + pqid);


                        List<Item> choiceList = new ArrayList<>();
                        Item item = new Item("0", "Select");
                        choiceList.add(item);

                        MapDependencyFieldDao mapDependencyFieldDao = myDatabase.mapDependencyFieldDao();
                        MapDependencyField mapDependencyField = mapDependencyFieldDao.getDependencyByValue(Integer.parseInt(formStructureModalListt.get(0).getVlookup()), Integer.parseInt(formStructureModalListt.get(0).getSelect_global_data_set_id()));

                        MapDependencyFieldValueDao mapDependencyFieldValueDao = myDatabase.mapDependencyFieldValueDao();

                        List<MapDependencyFieldValue> mapDependencyFieldValue = mapDependencyFieldValueDao.getIdForMainTable(mapDependencyField.getId(), getParentSelectedId(formStructureModalListt.get(0).getVlookup_qustion_id()));

                        GlobalDataSetValueDao globalDataSetValueDao = myDatabase.globalDataSetValueDao();

                        for (MapDependencyFieldValue mapDependencyFieldValue1 : mapDependencyFieldValue) {
                            int secondary = mapDependencyFieldValue1.getGlobalDataSetValueIdSecondry();
                            GlobalDataSetValue globalDataSetValues = globalDataSetValueDao.getById(secondary);

                            Item items = new Item(String.valueOf(globalDataSetValues.getId()), globalDataSetValues.getValue());
                            choiceList.add(items);
                        }


                        Log.v("dssdsdfds", choiceList.size() + "");

//                        ArrayAdapter<Item> adapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, choiceList);
//                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                        ((Spinner) view).setAdapter(adapter);


                        CustomSpinnerAdapter spinnerAdapter = new CustomSpinnerAdapter(getContext(), choiceList);
                        ((Spinner) view).setAdapter(spinnerAdapter);


                        ((Spinner) view).post(() -> {
                            try {
                                if (choiceList.size() > 2) {
                                    String pData = getPrefilledData(formStructureModalListt.get(0).getId());
                                    int pos = getSpinnerPosition(pData, choiceList);

                                    // Check if position is valid
                                    if (pos >= 0 && pos < choiceList.size()) {
                                        ((Spinner) view).setSelection(pos);
                                    } else {
                                        Log.v("setSelectionError", "Invalid position: " + pos);
                                    }

                                    Log.v("dfdsgdsfgsd", choiceList.size() + "  Size");
                                    Log.v("dfdsgdsfgsd", formStructureModalListt.get(0).getElement_label() + "   Selected Position:  " + pos);
                                    Log.v("dfdsgdsfgsd ", "Selected Data:  " + pData);
                                }
                            } catch (Exception e) {
                                String pData = getPrefilledData(formStructureModalListt.get(0).getId());
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

//                                searchView.setQuery(getLastSearchQueryChild, false);

                                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                    @Override
                                    public boolean onQueryTextSubmit(String query) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onQueryTextChange(String newText) {
                                        getLastSearchQueryChild = newText;  // Save the search query
                                        spinnerAdapter.getFilter().filter(newText);
                                        return false;
                                    }
                                });

                                builder.setView(vss);
                                AlertDialog dialog = builder.create();
                                listView.setOnItemClickListener((parent, view1, position, id) -> {
                                    ((Spinner) view).setSelection(position);
                                    updatechildLayout(formStructureModalListt.get(0).getId());
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    private RadioGroup createRadioButton(FormStructureModal formStructureModal) {
        List<Item> choiceList = new ArrayList<>();

        for (int i = 0; i < formStructureModal.getElement_choices().size(); i++) {
            try {
                Item items = new Item(formStructureModal.getElement_choices().get(i).getId(), formStructureModal.getElement_choices().get(i).getName());
                choiceList.add(items);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        RadioGroup radioGroup = new RadioGroup(getContext());
        radioGroup.setOrientation(RadioGroup.VERTICAL);
        radioGroup.setId(Integer.valueOf(formStructureModal.getId()));

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
            radioButton.setTag(formStructureModal.getId());
            radioButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black)); // Set text color
            radioButton.setButtonTintList(ColorStateList.valueOf(Color.BLACK)); // Set button color
            radioGroup.addView(radioButton);
        }


        handleEffectBranchingLogic(formStructureModal, radioGroup);


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

                    handleCauseBranchingLogicRadio(formStructureModal, selectedId, selectedText);


                }
            }
        });

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    String pData = getPrefilledData(formStructureModal.getId());
                    if (pData != null && !pData.isEmpty()) {
                        int selectedId = Integer.parseInt(pData); // Convert pData to int
                        radioGroup.check(selectedId);
                        // Pre-select the radio button with the ID from pData
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
        List<FormStructureModal> newList = formStructureModalList.stream()
                .filter(e -> e.getId().equalsIgnoreCase(tag))
                .collect(Collectors.toList());

        if (newList.isEmpty()) {
            return; // Exit early if no matching FormStructureModal is found
        }

        FormStructureModal formStructureModal = newList.get(0);

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
                    resetViews(child);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            SurveyDao surveyDao = myDatabase.surveyDao();
                            surveyDao.deletebyFormQuestionId(childId);
                        }
                    }).start();

                    try {
                        // Handle branching logic
                        List<BranchinglogicModal> branchingLogic = formStructureModal.getCause_branching_logic();
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
    private int createMultiCheckbox(FormStructureModal formStructureModal, int index) {
        try {

            for (int i = 0; i < formStructureModal.getElement_choices().size(); i++) {
                String label = formStructureModal.getElement_choices().get(i).getName();
                String id = formStructureModal.getElement_choices().get(i).getId();
                boolean isChecked = false;
                CheckBox checkBox = createCheckBox(label, isChecked);
                checkBox.setId(Integer.valueOf(id));
                checkBox.setTag(formStructureModal.getId());


                handleEffectBranchingLogic(formStructureModal, checkBox);


                checkBox.setOnCheckedChangeListener((buttonView, isCheckeds) -> {

                    if (checkBox.getId() == 99) {

                        checkBoxOnCheckChanged(formStructureModal, checkBox, isCheckeds);
                    }
                });

                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String pData = getPrefilledData(formStructureModal.getId());
                            if (pData != null && !pData.isEmpty()) {
                                // Convert pData to int
                                if (pData.contains(String.valueOf(checkBox.getId()))) {
                                    checkBox.setChecked(true);

                                    if (checkBox.getId() == 99) {
                                        checkBoxOnCheckChanged(formStructureModal, checkBox, true);
                                    }

                                }
                                // Pre-select the radio button with the ID from pData
                            }
                        } catch (Exception e) {
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

    private void checkBoxOnCheckChanged(FormStructureModal formStructureModal, CheckBox checkBox, Boolean isCheckeds) {
        try {
            if (formStructureModal.getCause_branching_logic().size() > 0) {
                Log.v("Branching:data", formStructureModal.getCause_branching_logic().size() + "");
                for (int j = 0; j < formStructureModal.getCause_branching_logic().size(); j++) {
//                                    String cause_id = formStructureModal.getCause_branching_logic().get(j).getBranching().split("=")[1].trim();

                    String[] cause_ids = extractCauseIds(formStructureModal.getCause_branching_logic().get(j).getBranching());

                    if (Arrays.asList(cause_ids).contains(String.valueOf(checkBox.getId())) && isCheckeds) {

                        Log.v("Branching:Cond", "Show Item");
                        showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(j).getEffect_question_id(), true);
                    } else {
                        Log.v("Branching:Cond", "Hide Item");
                        showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(j).getEffect_question_id(), false);
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

        return checkBox;
    }

    private View createLabelTextView(FormStructureModal formStructureModal) {
        TextView textView = new TextView(getContext());
        textView.setTextSize(18);
        textView.setId(0);
        textView.setTag(formStructureModal.getId());
        textView.setText(formStructureModal.getElement_label());
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(layoutParams);
        textView.setTextColor(getContext().getResources().getColor(R.color.black));
        handleEffectBranchingLogic(formStructureModal, textView);


        return textView;
    }


    private TextView createLabelCheckbox(FormStructureModal formStructureModal) {

        TextView headingTextView = new TextView(getContext());
        headingTextView.setText(formStructureModal.getElement_label());
        headingTextView.setId(0);
        headingTextView.setTypeface(null, Typeface.BOLD);
        headingTextView.setTag(formStructureModal.getId());
        headingTextView.setTextColor(getContext().getResources().getColor(R.color.black));

        headingTextView.setLayoutParams(layoutParams);

        handleEffectBranchingLogic(formStructureModal, headingTextView);


        return headingTextView;
    }


    private TextView createLabelRadio(FormStructureModal formStructureModal) {
        TextView labelTextView = new TextView(getContext());
        labelTextView.setText(formStructureModal.getElement_label());
        labelTextView.setId(0);
        labelTextView.setTag(formStructureModal.getId());
        labelTextView.setTextColor(getContext().getResources().getColor(R.color.black));
        labelTextView.setTypeface(null, Typeface.BOLD);
        labelTextView.setLayoutParams(layoutParams);

        handleEffectBranchingLogic(formStructureModal, labelTextView);


        return labelTextView;
    }


    private View createLabelEditTextView(FormStructureModal formStructureModal) {
        TextView labelTextView = new TextView(getContext());
        labelTextView.setText(formStructureModal.getElement_label());
        labelTextView.setId(0);
        labelTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        labelTextView.setTypeface(null, Typeface.BOLD);
        labelTextView.setLayoutParams(layoutParams);
        labelTextView.setTag(formStructureModal.getId());

        handleEffectBranchingLogic(formStructureModal, labelTextView);


        return labelTextView;
    }


    private ImageView createImageElement(FormStructureModal formStructureModal) {
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
            if (bit != null) {
                Utils.setLoadedLayoutStructure(elementImage, getContext());
                elementImage.setImageBitmap(bit);
            } else {
                Utils.setDummyLayoutStructure(elementImage, getContext());

                elementImage.setImageDrawable(getContext().getDrawable(R.mipmap.image));
                Log.e("ImageHandler", "Failed to load image.");
            }
        } else {
            Utils.setDummyLayoutStructure(elementImage, getContext());
            elementImage.setImageDrawable(getContext().getDrawable(R.mipmap.image));
        }

        return elementImage; // Return the dynamically created ImageView
    }


    private Button createButtonForImage(FormStructureModal formStructureModal) {
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


    private View createLabelEditElement(FormStructureModal formStructureModal, EditText editText) {

        TextView elementNote = new TextView(getContext());
        elementNote.setId(0);
        elementNote.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        elementNote.setTypeface(null, Typeface.BOLD);
        elementNote.setLayoutParams(layoutParams);
        elementNote.setTag(formStructureModal.getId());
        elementNote.setTextSize(13);
        elementNote.setText("Note* :" + formStructureModal.getElement_note());

        handleEffectBranchingLogic(formStructureModal, elementNote);


        if (!formStructureModal.getElement_note().isEmpty() && editText.getVisibility() == VISIBLE) {
            elementNote.setVisibility(VISIBLE);
        } else {
            elementNote.setVisibility(View.GONE);
        }

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


    private LinearLayout createCalculatedField(FormStructureModal formStructureModal) {
        // Create a LinearLayout to hold EditText and buttons


        LinearLayout layout = new LinearLayout(getContext());
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        layout.setId(Integer.valueOf(formStructureModal.getId()));
        layout.setTag(formStructureModal.getId());

        // Create EditText
        EditText editText = new EditText(getContext());
        editText.setLayoutParams(new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1)); // Take remaining space
        editText.setTextColor(getContext().getResources().getColor(R.color.black));
        editText.setBackground(getResources().getDrawable(R.drawable.border));
        editText.setTextSize(14);
        editText.setHeight(70);
        editText.setTag(formStructureModal.getId());
        editText.setId(Integer.valueOf(formStructureModal.getId()));
        editText.setPadding(7, 0, 0, 0);


        if (formStructureModal.getReadonly().equalsIgnoreCase("1")) {
            editText.setEnabled(false);
        } else {
            editText.setEnabled(true);

        }

        // Create Button 1
        Button button1 = new Button(getContext());
        button1.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        button1.setText("CALCULATE");
        button1.setId(0);
        button1.setTag(formStructureModal.getId());


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    float data = calculateLogic(formStructureModal.getCalculation_logic());
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

        handleEffectBranchingLogic(formStructureModal, layout);


        return layout;
    }

    private EditText createEditText(FormStructureModal formStructureModal) {
        EditText editText = new EditText(getContext());

        int validationType = Integer.valueOf(formStructureModal.getElement_validation());
        int var = Integer.valueOf(formStructureModal.getElement_validation() + formStructureModal.getElement_required());
        String formattedNumber = String.format("%02d", var);
        Log.v("validation", formStructureModal.getElement_validation() + " " + formStructureModal.getElement_required() + " => " + formattedNumber);

        editText.setId(Integer.valueOf(formStructureModal.getId()));

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
                if (Integer.valueOf(formStructureModal.getMaximum()) == 0 && Integer.valueOf(formStructureModal.getMinimum()) == 0) {
                    editText.setHint("*Validation: Integer");
                    editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.darkgrey));
                    validateInteger(editText);
                } else {
                    editText.setHint("*Validation: Integer b/w " + formStructureModal.getMinimum() + " To " + formStructureModal.getMaximum());
                    editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.darkgrey));
                    validateInteger(editText);
                }
                break;
            case 5:
                if (Integer.valueOf(formStructureModal.getMaximum()) == 0 && Integer.valueOf(formStructureModal.getMinimum()) == 0) {
                    editText.setHint("*Validation: Integer||Decimal");
                    editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.darkgrey));
                    validateIntegerDecimal(editText);
                } else {
                    editText.setFilters(new InputFilter[]{new InputFilterMinMax(Integer.valueOf(formStructureModal.getMinimum()), Integer.valueOf(formStructureModal.getMaximum()))});
                    editText.setHint("*Validation: Integer || Decimal b/w " + formStructureModal.getMinimum() + " To " + formStructureModal.getMaximum());
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
                if (Integer.valueOf(formStructureModal.getMaximum()) == 0) {
                    editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.darkgrey));
                    editText.setHint("*Validation: Integer");
                    validateInteger(editText);
                } else {
                    editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.darkgrey));
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(Integer.parseInt(formStructureModal.getMaximum()))});
                    validateInteger(editText);

                    if (!formStructureModal.getMaximum().equalsIgnoreCase("0")) {
                        editText.setHint("*Validation: Integer digits b/w " + formStructureModal.getMinimum() + " To " + formStructureModal.getMaximum());
                    }


                }
                break;
            default:
                break;
        }

        String pData = getPrefilledData(formStructureModal.getId());
        Log.v("pDataSetValue", "fillData: " + pData);
        Log.v("pDataSetValue", formStructureModal.getElement_label());
        Log.v("pDataSetValue", formStructureModal.getId());
        if (!pData.isEmpty()) {
            Log.v("pDataSetValue", "Set value: " + pData);
            editText.setText(pData);
        }


        editText.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        editText.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.border));
        editText.setTextSize(14);
        if (formStructureModal.getElement_type().equalsIgnoreCase("textarea")) {
            editText.setHeight(190);
        } else {
            int height = getResources().getDimensionPixelSize(com.intuit.sdp.R.dimen._35sdp);
            editText.setMinHeight(height);
        }
        editText.setPadding(7, 0, 0, 0);

        handleEffectBranchingLogic(formStructureModal, editText);


        //Interlink logic is implemented here
        Log.v("pDataSetValue", "Interlink: " + formStructureModal.getInterlink_question_id());

        if (!formStructureModal.getInterlink_question_id().equalsIgnoreCase("")) {
            editText.setEnabled(false);
            String data = getPrefilledData(formStructureModal.getInterlink_question_id());
            String elementType = getViewInstanceByQuestionId(formStructureModal.getInterlink_question_id());
            if (elementType.equalsIgnoreCase("text")) {
                if (data.equalsIgnoreCase("")) {
                    String value = getValueFromLayoutByQuestionId(formStructureModal.getInterlink_question_id());
                    if (!value.equalsIgnoreCase("")) {
                        editText.setText(value);
                    }
                } else {
                    Log.v("MyDebuggingData", data + "  getValueFromDB");
                    editText.setText(data);
                }
            } else if (elementType.equalsIgnoreCase("select")) {
                if (data.equalsIgnoreCase("0") || data.equalsIgnoreCase("")) {
                    String value = getValueFromLayoutByQuestionIdSpinner(formStructureModal.getInterlink_question_id());
                    if (!value.equalsIgnoreCase("") && !value.equalsIgnoreCase("0")) {
                        editText.setText(value);
                    } else {
                        editText.setText("N/A");
                    }
                } else {
                    Log.v("MyDebuggingData", data + "  getValueFromDB");
                    String res = getSpinnerNameFromQidFromValue(formStructureModal.getInterlink_question_id(), data);
                    editText.setText(res);
                }
            } else if (elementType.equalsIgnoreCase("radio")) {
                if (data.equalsIgnoreCase("0") || data.equalsIgnoreCase("")) {
                    String value = getValueFromLayoutByQuestionIdSpinner(formStructureModal.getInterlink_question_id());
                    if (!value.equalsIgnoreCase("") && !value.equalsIgnoreCase("0")) {
                        editText.setText(Utils.getSubstringBeforeDollar(value));
                    } else {
                        editText.setText("N/A");
                    }
                } else {
                    Log.v("MyDebuggingData", data + "  getValueFromDB");
                    String res = getSpinnerNameFromQidFromValue(formStructureModal.getInterlink_question_id(), data);
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

                handleCauseLogicForEditText(formStructureModal, editText);

            }
        });


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    handleCauseLogicForEditText(formStructureModal, editText);
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
                        List<FormStructureModal> formStructure = formStructureModalList.stream()
                                .filter(e -> e.getId().equalsIgnoreCase(String.valueOf(id)))
                                .collect(Collectors.toList());
                        if (!formStructure.isEmpty() &&
                                !editText.getText().toString().equalsIgnoreCase("")) {

                            FormStructureModal structureModal = formStructure.get(0);
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
                        List<FormStructureModal> form = formStructureModalList.stream()
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

                    List<FormStructureModal> form = formStructureModalList.stream()
                            .filter(e -> e.getId().equalsIgnoreCase(id))
                            .collect(Collectors.toList());

                    if (!form.isEmpty() && form.get(0).getElement_required().equalsIgnoreCase("1") && selectedId == -1) {
                        radioGroup.requestFocus();
                        Toast.makeText(getContext(), "Please select an option", Toast.LENGTH_SHORT).show();
                        res = false;
                    }
                }


            }
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
        SurveyData row = surveyDao.getPredefinedAnswer(formId, instanceId, qid);
        if (row != null && !row.getField_value().isEmpty() && !row.getField_value().equalsIgnoreCase("N/A")) {
            return row.getField_value();
        }
        return "";
    }

    public int getSpinnerPosition(String val, List<Item> choiceList) {
        for (int i = 0; i < choiceList.size(); i++) {
            if (val.equalsIgnoreCase(choiceList.get(i).getId())) {
                return i;
            }
        }
        return 0;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private float calculateLogic(String calculationLogic) {

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


        if (functionName.equalsIgnoreCase("MISS_SUM")) {
            float sum = answerList.stream()
                    .reduce(0.0f, Float::sum);
            Log.v("Function Name: ", sum + "  Total Sum");
            return sum;
        }

        if (functionName.equalsIgnoreCase("MISS_MULTIPLY")) {
            float mul = answerList.stream()
                    .reduce(1.0f, (a, b) -> a * b);

            Log.v("Function Name: ", mul + "  Total Multiply");
            return mul;
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

        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<String> findQuestionIdFromElementVariable(List<String> parameterList) {
        List<String> allQestionId = new ArrayList<>();
        for (String elementVariable : parameterList) {


            String str = formStructureModalList.stream()
                    .filter(e -> e.getElement_variable().equalsIgnoreCase(elementVariable))
                    .map(FormStructureModal::getId)
                    .findFirst()
                    .get();

            allQestionId.add(str);
        }
        return allQestionId;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<Float> findAllValueFromLayoutAndDb(List<String> allQestionId) {

        ArrayList<Float> answerList = new ArrayList<>();

        for (String qid : allQestionId) {

            Log.v("MyDebuggingData", qid + "  Question Id");

            String data = getValueFromLayoutByQuestionId(qid);

            Log.v("MyDebuggingData", data + "  PrefilledData");


            if (data.equalsIgnoreCase("")) {

                String value = getPrefilledData(qid);

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

                if (String.valueOf(view.getId()).equalsIgnoreCase(qid) || String.valueOf(view.getTag()).equalsIgnoreCase(qid)) {
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
        List<FormStructureModal> formStructureModals = formStructureModalList.stream().filter(e -> e.getId().equalsIgnoreCase(qid)).collect(Collectors.toList());
        return formStructureModals.get(0).getElement_type();
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
            List<FormStructureModal> formStructureModals = formStructureModalList.stream().filter(e -> e.getId().equalsIgnoreCase(qid)).collect(Collectors.toList());
            List<ElementChoice> items = formStructureModals.get(0).getElement_choices();
            List<ElementChoice> elementChoices = items.stream().filter(e -> e.getId().equalsIgnoreCase(itemId)).collect(Collectors.toList());
            return elementChoices.get(0).getName();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void getCurrentIndex() {
        try {
            SurveyDao surveyDao = myDatabase.surveyDao();
            SurveyData s = surveyDao.getLastEntryByForm(formId, instanceId);

            if (s != null && s.getQuestion_id() != null) {

                String lastQid = s.getQuestion_id();

                OptionalInt sectionIndex = IntStream.range(0, list.size())
                        .filter(i -> list.get(i).stream().anyMatch(form -> form.getId().equals(lastQid)))
                        .findFirst();
                if (sectionIndex.isPresent()) {
                    System.out.println("QID found in section index: " + sectionIndex.getAsInt());
                    currentPageIndex = sectionIndex.getAsInt();
                    parseData(sectionIndex.getAsInt());
                } else {
                    System.out.println("QID found in section index: 0");
                    parseData(0);
                }
            } else {
                parseData(0);
            }
        } catch (Exception e) {
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


    public void handleEffectBranchingLogic(FormStructureModal formStructureModal, View v) {
        try {
            if (formStructureModal.getEffect_branching_logic().isEmpty()) {
                v.setVisibility(View.VISIBLE);
                return;
            }

            v.setVisibility(View.GONE);
            String branch = formStructureModal.getEffect_branching_logic().get(0).getBranching();
            String[] causeIds = extractCauseIds(branch);

            for (String causeId : causeIds) {
                int numericCauseId = Integer.parseInt(causeId.replaceAll("\\D", ""));
                String selectedId;

                if (branch.contains(">")) {
                    selectedId = getSpinnerNameFromQidFromValue(
                            formStructureModal.getCause_branching_logic().get(0).getEffect_question_id(), causeId);
                    if (Integer.parseInt(selectedId) > numericCauseId) {
                        v.setVisibility(View.VISIBLE);
                        break;
                    }
                } else if (branch.contains("<")) {
                    selectedId = getSpinnerNameFromQidFromValue(
                            formStructureModal.getCause_branching_logic().get(0).getEffect_question_id(), causeId);
                    if (Integer.parseInt(selectedId) < numericCauseId) {
                        v.setVisibility(View.VISIBLE);
                        break;
                    }
                } else if (branch.contains("|")) {
                    String id = getPrefilledData(formStructureModal.getEffect_branching_logic().get(0).getCause_question_id());
                    if (Arrays.asList(causeIds).contains(id)) {
                        v.setVisibility(View.VISIBLE);
                        break;
                    }
                } else {
                    selectedId = getPrefilledData(formStructureModal.getEffect_branching_logic().get(0).getCause_question_id());
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


    public void handleCauseLogicForEditText(FormStructureModal formStructureModal, EditText editText) {
        try {

            if (formStructureModal.getCause_branching_logic().size() > 0) {

                Log.v("Branching:data", formStructureModal.getCause_branching_logic().size() + "");
                for (int i = 0; i < formStructureModal.getCause_branching_logic().size(); i++) {
                    //String cause_id = formStructureModal.getCause_branching_logic().get(i).getBranching().split("=")[1].trim();


                    String[] cause_ids = extractCauseIds(formStructureModal.getCause_branching_logic().get(i).getBranching());
                    String branch = formStructureModal.getCause_branching_logic().get(i).getBranching();


                    for (String cause_id : cause_ids) {

                        Log.v("FoundData", cause_id);


                        int numericSelectedId = Integer.parseInt(editText.getText().toString());
                        int numericCauseId = Integer.parseInt(cause_id.replaceAll("[^\\d]", "")); // Remove non-numeric characters

                        Log.v("FoundData", numericSelectedId + "");
                        Log.v("FoundData", numericCauseId + "");

                        if (branch.contains(">")) {
                            Log.v("FoundData", numericSelectedId + "  >" + numericCauseId);
                            if (numericSelectedId > numericCauseId) {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), false);
                                break;
                            }
                        } else if (branch.contains("<")) {
                            if (numericSelectedId < numericCauseId) {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), false);
                                break;
                            }
                        } else {
                            if (numericSelectedId == numericCauseId) {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), false);
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


    public void handleCauseLogicForSpinner(FormStructureModal formStructureModal, int position) {
        try {

            if (formStructureModal.getCause_branching_logic().size() > 0) {

                Log.v("Branching:data", formStructureModal.getCause_branching_logic().size() + "");
                for (int i = 0; i < formStructureModal.getCause_branching_logic().size(); i++) {
                    //String cause_id = formStructureModal.getCause_branching_logic().get(i).getBranching().split("=")[1].trim();


                    String[] cause_ids = extractCauseIds(formStructureModal.getCause_branching_logic().get(i).getBranching());
                    String branch = formStructureModal.getCause_branching_logic().get(i).getBranching();


                    for (String cause_id : cause_ids) {

                        Log.v("FoundDataCauseID", cause_id);


                        if (position == 0) {
                            showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), false);
                            continue;
                        }


                        int numericSelectedId = Integer.parseInt(formStructureModal.getElement_choices().get(position - 1).getId());
                        int numericCauseId = Integer.parseInt(cause_id.replaceAll("[^\\d]", "")); // Remove non-numeric characters

                        Log.v("FoundData", numericSelectedId + "");
                        Log.v("FoundData", numericCauseId + "");

                        if (branch.contains(">")) {
                            Log.v("FoundData", numericSelectedId + "  >" + numericCauseId);
                            numericSelectedId = Integer.parseInt(formStructureModal.getElement_choices().get(position - 1).getName());

                            if (numericSelectedId > numericCauseId) {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), false);
                                break;
                            }
                        } else if (branch.contains("<")) {
                            numericSelectedId = Integer.parseInt(formStructureModal.getElement_choices().get(position - 1).getName());

                            if (numericSelectedId < numericCauseId) {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), false);
                                break;
                            }
                        } else if (branch.contains("|")) {
                            if (position != 0 && Arrays.asList(cause_ids).contains(formStructureModal.getElement_choices().get(position - 1).getId())) {
                                Log.v("Branching:Cond", "Show Item");
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), true);
                            } else {
                                Log.v("Branching:Cond", "Hide Item");
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), false);
                            }
                        } else {
                            numericSelectedId = Integer.parseInt(formStructureModal.getElement_choices().get(position - 1).getId());
                            if (numericSelectedId == numericCauseId) {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), false);
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
    public void handleCauseBranchingLogicRadio(FormStructureModal formStructureModal, int selectedId, String selectedText) {
        try {
            if (formStructureModal.getCause_branching_logic().size() > 0) {
                Log.v("Branching:data", formStructureModal.getCause_branching_logic().size() + "");
                for (int i = 0; i < formStructureModal.getCause_branching_logic().size(); i++) {
                    String[] cause_ids = extractCauseIds(formStructureModal.getCause_branching_logic().get(i).getBranching());
                    String branch = formStructureModal.getCause_branching_logic().get(i).getBranching();
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
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), false);
                                break;
                            }
                        } else if (branch.contains("<")) {
                            numericSelectedId = Integer.parseInt(selectedText);

                            if (numericSelectedId < numericCauseId) {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), false);
                                break;
                            }
                        } else if (branch.contains("|")) {
                            if (Arrays.asList(cause_ids).contains(String.valueOf(selectedId))) {
                                Log.v("Branching:Cond", "Show Item");
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), true);
                            } else {
                                Log.v("Branching:Cond", "Hide Item");
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), false);
                            }
                        } else {
                            numericSelectedId = selectedId;
                            if (numericSelectedId == numericCauseId) {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), true);
                                break;
                            } else {
                                showHideLaoyoutbyTag(formStructureModal.getCause_branching_logic().get(i).getEffect_question_id(), false);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap) extras.get("data");

            if (bitmap != null) {
                // Save and process the bitmap
                File savedFile = Utils.saveBitmapToLocalStorage(getContext(), bitmap, file_name);
                if (savedFile != null) {

                    Utils.setLoadedLayoutStructure(elementImage, getContext());
                    Bitmap correctedBitmap = Utils.correctImageOrientation(savedFile);
                    elementImage.setImageBitmap(correctedBitmap);

                    Log.d("UploadFile", savedFile.getAbsolutePath() + " saved");

                }
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
            showImagePickerDialog(FormStructureFragment.this);
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImagePickerDialog(FormStructureFragment.this);
            } else {
                Toast.makeText(getContext(), "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}