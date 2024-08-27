package com.android.gids;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<Item> {

    public CustomSpinnerAdapter(Context context, List<Item> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custome_spinner_item, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.text1);
        textView.setText(TextUtils.splitText(getContext(),getItem(position).getName()));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_dropdown_item, parent, false);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.spinnerDropdownTextView);

        String name = getItem(position).getName().replace("\n","");
        textView.setText(TextUtils.splitText(getContext(),name));
        return convertView;
    }


}