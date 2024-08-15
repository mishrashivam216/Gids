package com.android.gids;

public class FormListStatusRequest {

    public String user_id="";

    public String form_id="";
    public String form_status="";


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getForm_id() {
        return form_id;
    }

    public void setForm_id(String form_id) {
        this.form_id = form_id;
    }

    public String getForm_status() {
        return form_status;
    }

    public void setForm_status(String form_status) {
        this.form_status = form_status;
    }
}
