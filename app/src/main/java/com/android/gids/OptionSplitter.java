package com.android.gids;

import java.util.List;

public class OptionSplitter {

    String mainOption;
    List<String> subOptions;

    public OptionSplitter(String mainOption, List<String> subOptions) {
        this.mainOption = mainOption;
        this.subOptions = subOptions;
    }

    public String getMainOption() {
        return mainOption;
    }

    public void setMainOption(String mainOption) {
        this.mainOption = mainOption;
    }

    public List<String> getSubOptions() {
        return subOptions;
    }

    public void setSubOptions(List<String> subOptions) {
        this.subOptions = subOptions;
    }
}
