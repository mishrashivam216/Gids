package com.android.gids;

public class FormattedFloat {
    private final float value;
    private final String formattedValue;

    public FormattedFloat(String formattedValue) {
        this.formattedValue = formattedValue;
        this.value = Float.parseFloat(formattedValue); // Convert to float for calculations
    }

    public float getValue() {
        return value;
    }

    public String getFormattedValue() {
        return formattedValue;
    }

    @Override
    public String toString() {
        return formattedValue; // Return the formatted value when needed
    }
}
