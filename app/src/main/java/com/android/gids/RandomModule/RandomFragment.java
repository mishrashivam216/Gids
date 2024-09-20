package com.android.gids.RandomModule;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.gids.R;
import com.android.gids.SurveyRoomDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RandomFragment extends Fragment {

    private Spinner districtSpinner;
    private EditText maxLengthEditText, selectedNodeEditText, randomNodeEditText;
    private Button submitButton;

    private List<String> remainingValues;
    private List<Integer> districtColumnValues;  // This represents the numbers in the District-1, District-2 columns

    SurveyRoomDatabase myDatabase;

    public int districtId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_random, container, false);

        myDatabase = SurveyRoomDatabase.getInstance(getContext());

        districtSpinner = v.findViewById(R.id.districtSpinner);
        maxLengthEditText = v.findViewById(R.id.maxLengthEditText);
        selectedNodeEditText = v.findViewById(R.id.selectedNodeEditText);
        randomNodeEditText = v.findViewById(R.id.randomNodeEditText);
        submitButton = v.findViewById(R.id.submitButton);


        populateDistrictSpinner();

        // Sample data for District-1 column values (replace with actual DB query)

        // Handle submit button click
        submitButton.setOnClickListener(t -> {
            String maxLengthInput = maxLengthEditText.getText().toString();
            if (TextUtils.isEmpty(maxLengthInput)) {
                Toast.makeText(getContext(), "Please enter max length", Toast.LENGTH_SHORT).show();
                return;
            }

            int maxLength = Integer.parseInt(maxLengthInput);

            // Determine remaining values based on max length
            if (maxLength <= 10) {
                remainingValues = new ArrayList<>();
                for (int i = 1; i <= maxLength; i++) {
                    remainingValues.add("H" + i);
                }
                calculateRandomNode(remainingValues, districtColumnValues, maxLength);
            } else if (maxLength <= 25) {
                remainingValues = new ArrayList<>();
                for (int i = 1; i <= maxLength; i++) {
                    remainingValues.add("H" + i);
                }
                calculateRandomNode(remainingValues, districtColumnValues, maxLength);
            } else {
                Toast.makeText(getContext(), "Max length should be less than or equal to 25", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    // Method to populate District Spinner
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


    // Method to calculate random node based on maxLength
    private void calculateRandomNode(List<String> remainingValues, List<Integer> districtColumn, int maxLength) {
        int maxNodeValue = Integer.parseInt(remainingValues.get(maxLength - 1).substring(1));  // Get the max value like H1, H2, H10

        int comparisonDigits;
        if (maxLength <= 10) {
            comparisonDigits = maxNodeValue % 10;  // Take the last digit of max node value
        } else {
            comparisonDigits = maxNodeValue % 100;  // Take the last two digits of max node value
        }

        // Traverse the district column and select values based on the comparison
        List<String> selectedNodes = new ArrayList<>();
        for (int districtValue : districtColumn) {
            int lastDigits;
            if (maxLength <= 10) {
                lastDigits = districtValue % 10;  // Last digit for comparison
            } else {
                lastDigits = districtValue % 100;  // Last two digits for comparison
            }

            // Compare and select values
            if (lastDigits <= comparisonDigits && lastDigits!=0) {
                selectedNodes.add("H" + lastDigits);  // Select the corresponding node
                if (selectedNodes.size() == remainingValues.size()) {
                    break;  // Stop if we've filled the remaining values
                }
            }
        }

        // Output the selected nodes to Random Node field
        String randomNode = String.join(", ", selectedNodes.get(0));
        randomNodeEditText.setText(randomNode);  // Set the selected random node
    }

}