package com.android.gids;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "survey_data")
public class SurveyData {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "record_id")
    public String record_id = "";

    @ColumnInfo(name = "instance_id")
    public int instance_id;

    @ColumnInfo(name = "form_id")
    public String form_id = "";

    @ColumnInfo(name = "user_id")
    public String user_id = "";

    @ColumnInfo(name = "question_id")
    public String question_id = "";

    @ColumnInfo(name = "field_name")
    public String field_name = "";
    @ColumnInfo(name = "field_value")
    public String field_value = "";

    @ColumnInfo(name = "section_id")
    public String section_id = "";
    @ColumnInfo(name = "lat")
    public String lat = "";

    @SerializedName("long")
    @ColumnInfo(name = "logitude")
    public String logitude = "";

    @ColumnInfo(name = "create_date_time")
    public String create_date_time = "";
    @ColumnInfo(name = "sync_status")
    public String sync_status = "";


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

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getField_name() {
        return field_name;
    }

    public void setField_name(String field_name) {
        this.field_name = field_name;
    }

    public String getField_value() {
        return field_value;
    }

    public void setField_value(String field_value) {
        this.field_value = field_value;
    }

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLogitude() {
        return logitude;
    }

    public void setLogitude(String logitude) {
        this.logitude = logitude;
    }

    public String getCreate_date_time() {
        return create_date_time;
    }

    public void setCreate_date_time(String create_date_time) {
        this.create_date_time = create_date_time;
    }

    public String getSync_status() {
        return sync_status;
    }

    public void setSync_status(String sync_status) {
        this.sync_status = sync_status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(int instance_id) {
        this.instance_id = instance_id;
    }
}
