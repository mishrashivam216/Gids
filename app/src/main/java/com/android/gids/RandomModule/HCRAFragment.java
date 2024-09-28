package com.android.gids.RandomModule;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.gids.R;
import com.android.gids.SurveyRoomDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class HCRAFragment extends Fragment {

    private Spinner districtSpinner;
    private EditText maxLengthEditText;
    private Button submitButton;

    private List<String> remainingValues;
    private List<Integer> districtColumnValues;  // This represents the numbers in the District-1, District-2 columns

    SurveyRoomDatabase myDatabase;

    public int districtId;

    TextView tvH1, tvH2, tvH3, tvH4, tvH5, tvH6, tvH7, tvH8;

    TextView tvHcrt1, tvHcrt2, tvHcrt3, tvHcrt4, tvHcrt5, tvHcrt6, tvHcrt7, tvHcrt8;

    TextView tvH9, tvH10, tvH11, tvH12, tvH13, tvH14, tvH15, tvH16;

    TextView tvHcrt9, tvHcrt10, tvHcrt11, tvHcrt12;

    LinearLayout li_hcra9, li_hcra10, li_hcra11, li_hcra12, li_hcra13, li_hcra14, li_hcra15, li_hcra16;

    LinearLayout li_hcrt6, li_hcrt7, li_hcrt8, li_hcrt9, li_hcrt10, li_hcrt11, li_hcrt12;


    LinearLayout liHcraParent, liHcrtParent;

    private RadioGroup radioGroup, radioGroupType;

    TextView tvHeading, tvGroup2, tvHCRTGroup2;

    TextView hcra1, hcra2, hcra3, hcra4, hcra5, hcra6, hcra7, hcra8, hcra9, hcra10, hcra11, hcra12, hcra13, hcra14, hcra15, hcra16;
    TextView hcrt1, hcrt2, hcrt3, hcrt4, hcrt5, hcrt6, hcrt7, hcrt8, hcrt9, hcrt10, hcrt11, hcrt12;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_h_c_r_a, container, false);

        myDatabase = SurveyRoomDatabase.getInstance(getContext());

        districtSpinner = v.findViewById(R.id.districtSpinner);
        maxLengthEditText = v.findViewById(R.id.maxLengthEditText);
        submitButton = v.findViewById(R.id.submitButton);
        radioGroup = v.findViewById(R.id.radioGroup);
        radioGroupType = v.findViewById(R.id.radioGroupType);
        liHcraParent = v.findViewById(R.id.liHcraParent);
        liHcrtParent = v.findViewById(R.id.liHcrtParent);
        tvHeading = v.findViewById(R.id.tvHeading);
        tvGroup2 = v.findViewById(R.id.tvGroup2);
        tvHCRTGroup2 = v.findViewById(R.id.tvHCRTGroup2);


        hcra1 = v.findViewById(R.id.hcra1);
        hcra2 = v.findViewById(R.id.hcra2);
        hcra3 = v.findViewById(R.id.hcra3);
        hcra4 = v.findViewById(R.id.hcra4);
        hcra5 = v.findViewById(R.id.hcra5);
        hcra6 = v.findViewById(R.id.hcra6);
        hcra7 = v.findViewById(R.id.hcra7);
        hcra8 = v.findViewById(R.id.hcra8);
        hcra9 = v.findViewById(R.id.hcra9);
        hcra10 = v.findViewById(R.id.hcra10);
        hcra11 = v.findViewById(R.id.hcra11);
        hcra12 = v.findViewById(R.id.hcra12);
        hcra13 = v.findViewById(R.id.hcra13);
        hcra14 = v.findViewById(R.id.hcra14);
        hcra15 = v.findViewById(R.id.hcra15);
        hcra16 = v.findViewById(R.id.hcra16);


        hcrt1 = v.findViewById(R.id.hcrt1);
        hcrt2 = v.findViewById(R.id.hcrt2);
        hcrt3 = v.findViewById(R.id.hcrt3);
        hcrt4 = v.findViewById(R.id.hcrt4);
        hcrt5 = v.findViewById(R.id.hcrt5);
        hcrt6 = v.findViewById(R.id.hcrt6);
        hcrt7 = v.findViewById(R.id.hcrt7);
        hcrt8 = v.findViewById(R.id.hcrt8);
        hcrt9 = v.findViewById(R.id.hcrt9);
        hcrt10 = v.findViewById(R.id.hcrt10);
        hcrt11 = v.findViewById(R.id.hcrt11);
        hcrt12 = v.findViewById(R.id.hcrt12);


        tvH1 = v.findViewById(R.id.tvH1);
        tvH2 = v.findViewById(R.id.tvH2);
        tvH3 = v.findViewById(R.id.tvH3);
        tvH4 = v.findViewById(R.id.tvH4);
        tvH5 = v.findViewById(R.id.tvH5);
        tvH6 = v.findViewById(R.id.tvH6);
        tvH7 = v.findViewById(R.id.tvH7);
        tvH8 = v.findViewById(R.id.tvH8);

        tvH9 = v.findViewById(R.id.tvH9);
        tvH10 = v.findViewById(R.id.tvH10);
        tvH11 = v.findViewById(R.id.tvH11);
        tvH12 = v.findViewById(R.id.tvH12);
        tvH13 = v.findViewById(R.id.tvH13);
        tvH14 = v.findViewById(R.id.tvH14);
        tvH15 = v.findViewById(R.id.tvH15);
        tvH16 = v.findViewById(R.id.tvH16);


        //hcrt

        tvHcrt1 = v.findViewById(R.id.tvHcrt1);
        tvHcrt2 = v.findViewById(R.id.tvHcrt2);
        tvHcrt3 = v.findViewById(R.id.tvHcrt3);
        tvHcrt4 = v.findViewById(R.id.tvHcrt4);
        tvHcrt5 = v.findViewById(R.id.tvHcrt5);
        tvHcrt6 = v.findViewById(R.id.tvHcrt6);

        // 1 grouup
        tvHcrt7 = v.findViewById(R.id.tvHcrt7);
        tvHcrt8 = v.findViewById(R.id.tvHcrt8);
        tvHcrt9 = v.findViewById(R.id.tvHcrt9);
        tvHcrt10 = v.findViewById(R.id.tvHcrt10);
        tvHcrt11 = v.findViewById(R.id.tvHcrt11);
        tvHcrt12 = v.findViewById(R.id.tvHcrt12);


        li_hcra9 = v.findViewById(R.id.li_hcra9);
        li_hcra10 = v.findViewById(R.id.li_hcra10);
        li_hcra11 = v.findViewById(R.id.li_hcra11);
        li_hcra12 = v.findViewById(R.id.li_hcra12);
        li_hcra13 = v.findViewById(R.id.li_hcra13);
        li_hcra14 = v.findViewById(R.id.li_hcra14);
        li_hcra15 = v.findViewById(R.id.li_hcra15);
        li_hcra16 = v.findViewById(R.id.li_hcra16);


        li_hcrt6 = v.findViewById(R.id.li_hcrt6);
        li_hcrt7 = v.findViewById(R.id.li_hcrt7);
        li_hcrt8 = v.findViewById(R.id.li_hcrt8);
        li_hcrt9 = v.findViewById(R.id.li_hcrt9);
        li_hcrt10 = v.findViewById(R.id.li_hcrt10);
        li_hcrt11 = v.findViewById(R.id.li_hcrt11);
        li_hcrt12 = v.findViewById(R.id.li_hcrt12);


        populateDistrictSpinner();
        submitButton.setOnClickListener(t -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            int radioGroupTypeId = radioGroupType.getCheckedRadioButtonId();

            if (selectedId != -1) {
                RadioButton selectedRadioButton = getActivity().findViewById(selectedId);
                RadioButton selectedTypeButton = getActivity().findViewById(radioGroupTypeId);

                String type = selectedRadioButton.getText().toString().toUpperCase();
                if (type.equals("HCRA") || type.equals("HCRA RT")) {
                    triggerHcra(Integer.parseInt(selectedTypeButton.getText().toString()), type);
                }
            } else {
                Toast.makeText(getContext(), "No option selected", Toast.LENGTH_SHORT).show();
            }
        });


        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isHCRASelected = isRadioButtonChecked(radioGroup, "HCRA");
            boolean isTypeOneSelected = isRadioButtonChecked(radioGroupType, "1");
            updateUI(isHCRASelected, isTypeOneSelected);
        });

        radioGroupType.setOnCheckedChangeListener((group, checkedId) -> {
            boolean isHCRASelected = isRadioButtonChecked(radioGroup, "HCRA");
            boolean isTypeOneSelected = isRadioButtonChecked(radioGroupType, "1");
            updateUI(isHCRASelected, isTypeOneSelected);
        });


        liHcraParent.setVisibility(View.VISIBLE);
        liHcrtParent.setVisibility(View.GONE);
        tvHeading.setText("Household Group 1");

        return v;
    }

    private boolean isRadioButtonChecked(RadioGroup group, String text) {
        int checkedId = group.getCheckedRadioButtonId();
        if (checkedId != -1) {
            RadioButton selectedRadioButton = getActivity().findViewById(checkedId);
            return selectedRadioButton.getText().toString().equalsIgnoreCase(text);
        }
        return false;
    }


    private void updateUI(boolean isHCRASelected, boolean isTypeOneSelected) {
        liHcraParent.setVisibility(isHCRASelected ? View.VISIBLE : View.GONE);
        liHcrtParent.setVisibility(isHCRASelected ? View.GONE : View.VISIBLE);
//        tvHeading.setText(isHCRASelected ? "HCRA" : "HCRT");

        if (isHCRASelected) {
            if (isTypeOneSelected) {
                renameVarHcra(1);
                hideinTwoParts();
                showHcraGroupWise();
            } else {
                renameVarHcra(2);
                hideHcraGroup2();
            }
        } else {
            if (isTypeOneSelected) {
                renameVar(1);
                showHcrtGroup2();
            } else {
                renameVar(2);
                hideHcrtGroup2();

            }
        }
    }


    private void renameVarHcra(int group) {
        if (group == 1) {
            hcra9.setText("H9");
            hcra10.setText("H10");
            hcra11.setText("H11");
            hcra12.setText("H12");
            hcra13.setText("H13");
            hcra14.setText("H14");
            hcra15.setText("H15");
            hcra16.setText("H16");
        }
    }

    private void renameVar(int group) {
        if (group == 1) {
            hcrt7.setText("H7");
            hcrt8.setText("H8");
            hcrt9.setText("H9");
            hcrt10.setText("H10");
            hcrt11.setText("H11");
            hcrt12.setText("H12");
        }
    }


    private void hideHcrtGroup2() {
        li_hcrt7.setVisibility(View.GONE);
        li_hcrt8.setVisibility(View.GONE);
        li_hcrt9.setVisibility(View.GONE);
        li_hcrt10.setVisibility(View.GONE);
        li_hcrt11.setVisibility(View.GONE);
        li_hcrt12.setVisibility(View.GONE);
    }


    private void showHcrtGroup2() {
        li_hcrt7.setVisibility(View.VISIBLE);
        li_hcrt8.setVisibility(View.VISIBLE);
        li_hcrt9.setVisibility(View.VISIBLE);
        li_hcrt10.setVisibility(View.VISIBLE);
        li_hcrt11.setVisibility(View.VISIBLE);
        li_hcrt12.setVisibility(View.VISIBLE);
    }


    private void hideHcraGroup2() {
        li_hcra9.setVisibility(View.GONE);
        li_hcra10.setVisibility(View.GONE);
        li_hcra11.setVisibility(View.GONE);
        li_hcra12.setVisibility(View.GONE);
        li_hcra13.setVisibility(View.GONE);
        li_hcra14.setVisibility(View.GONE);
        li_hcra15.setVisibility(View.GONE);
        li_hcra16.setVisibility(View.GONE);

    }

    private void showHcraGroup2() {
        li_hcra9.setVisibility(View.VISIBLE);
        li_hcra10.setVisibility(View.VISIBLE);
        li_hcra11.setVisibility(View.VISIBLE);
        li_hcra12.setVisibility(View.VISIBLE);
        li_hcra13.setVisibility(View.VISIBLE);
        li_hcra14.setVisibility(View.VISIBLE);
        li_hcra15.setVisibility(View.VISIBLE);
        li_hcra16.setVisibility(View.VISIBLE);

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public List<Integer> matchNumbers(List<Integer> numbers, int selectedNumber) {
        List<Integer> modifiableNumbers = new ArrayList<>(numbers);

        // Collect district data for districtId + 1 to districtId + 5
        for (int i = 1; i <= 5; i++) {
            List<Integer> districtData = DistrctDataUtils.getDistrictData(districtId + i);
            if (districtData != null && !districtData.isEmpty()) {
                modifiableNumbers.addAll(districtData);
            }
        }

        System.out.println("Combined List: " + modifiableNumbers);

        // Determine the divisor based on the selectedNumber
        int divisor;
        if (selectedNumber <= 10) {
            divisor = 10;    // 1-digit comparison
        } else if (selectedNumber <= 100) {
            divisor = 100;   // 2-digit comparison
        } else {
            divisor = 1000;  // 3-digit comparison
        }

        // Find matched numbers
        return modifiableNumbers.stream()
                .map(number -> number % divisor)
                .filter(checkValue -> checkValue <= selectedNumber && checkValue != 0)
                .distinct()
                .collect(Collectors.toList());
    }


    private void showHcraGroupWise() {
        li_hcra9.setVisibility(View.VISIBLE);
        li_hcra10.setVisibility(View.VISIBLE);
        li_hcra11.setVisibility(View.VISIBLE);
        li_hcra12.setVisibility(View.VISIBLE);
        li_hcra13.setVisibility(View.VISIBLE);
        li_hcra14.setVisibility(View.VISIBLE);
        li_hcra15.setVisibility(View.VISIBLE);
        li_hcra16.setVisibility(View.VISIBLE);

    }

    private void divideinTwoParts() {
        tvGroup2.setVisibility(View.VISIBLE);

    }

    private void hideinTwoParts() {
        tvGroup2.setVisibility(View.GONE);

    }

    private void showHcraGroupHCRT() {
        tvHCRTGroup2.setVisibility(View.VISIBLE);
    }

    private void hideHcrtGroupWise() {
        tvHCRTGroup2.setVisibility(View.GONE);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void triggerHcra(int group, String type) {
        try {
            List<Integer> result = matchNumbers(districtColumnValues, Integer.valueOf(maxLengthEditText.getText().toString()));

            TextView[] textViews;
            if (!result.isEmpty()) {

                if (group == 1) {
                    textViews = new TextView[]{tvH1, tvH2, tvH3, tvH4, tvH5, tvH6, tvH7, tvH8, tvH9, tvH10, tvH11, tvH12, tvH13, tvH14, tvH15, tvH16};

                } else {
                    textViews = new TextView[]{tvH1, tvH2, tvH3, tvH4, tvH5, tvH6, tvH7, tvH8};
                }


                int hcrtIndex = 0;
                for (int i = 0; i < result.size() && i < textViews.length; i++) {
                    textViews[i].setText(String.valueOf(result.get(i)));
                    hcrtIndex++;
                }

                //tvGroup2.setVisibility(group == 2 ? View.VISIBLE : View.GONE);

                if ("HCRA RT".equalsIgnoreCase(type)) {
                    triggerHcrt(group, result, hcrtIndex);
                }
            } else {
                Toast.makeText(getContext(), "Answer Not Found in current list: " + result.size(), Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void triggerHcrt(int group, List<Integer> result, int index) {
        try {
            Log.v("vbfdhgfd", result.size() + "");
            TextView[] hcrtTextViews;
            if (!result.isEmpty()) {

                if (group == 1) {
                    hcrtTextViews = new TextView[]{tvHcrt1, tvHcrt2, tvHcrt3, tvHcrt4, tvHcrt5, tvHcrt6, tvHcrt7, tvHcrt8, tvHcrt9, tvHcrt10, tvHcrt11, tvHcrt12};
                } else {
                    hcrtTextViews = new TextView[]{tvHcrt1, tvHcrt2, tvHcrt3, tvHcrt4, tvHcrt5, tvHcrt6};
                }

                for (int i = 0; i < hcrtTextViews.length && index + i < result.size(); i++) {
                    hcrtTextViews[i].setText(String.valueOf(result.get(index + i)));
                }

               // tvGroup2.setVisibility(group == 2 ? View.VISIBLE : View.GONE);


            } else {
                Toast.makeText(getContext(), "Answer Not Found in current list: " + result.size(), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setBlankTextHcrt() {
        tvHcrt9.setText("");
        tvHcrt10.setText("");
        tvHcrt11.setText("");
        tvHcrt12.setText("");
    }


    private void setBlankTextHcra() {

        tvH9.setText("");
        tvH10.setText("");
        tvH11.setText("");
        tvH12.setText("");
        tvH13.setText("");
        tvH14.setText("");
        tvH15.setText("");
        tvH16.setText("");

    }


    private void populateDistrictSpinner() {
        List<District> districts = myDatabase.districtDao().getAllDistrict();

        List<String> districtNames = new ArrayList<>();
        for (District district : districts) {
            districtNames.add(district.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, districtNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        districtSpinner.setAdapter(adapter);

        // Set OnItemSelectedListener
        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDistrict = districtNames.get(position);
                List<District> districtData = districts.stream().filter(e -> e.getName().trim().equalsIgnoreCase(selectedDistrict.trim())).collect(Collectors.toList());
                districtId = districtData.get(0).getId();
                //Toast.makeText(getContext(), "Selected: " + districtId + " " + districtData.get(0).getName(), Toast.LENGTH_SHORT).show();
                districtColumnValues = DistrctDataUtils.getDistrictData(districtId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getContext(), "No district selected", Toast.LENGTH_SHORT).show();
            }
        });


    }

}