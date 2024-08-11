package com.android.gids;

import java.util.List;

public class FormRequest {

    private String record_id;
    private String form_id;
    private String user_id;
    private List<FormData> form_data;


    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<FormData> getForm_data() {
        return form_data;
    }

    public void setForm_data(List<FormData> form_data) {
        this.form_data = form_data;
    }
}
