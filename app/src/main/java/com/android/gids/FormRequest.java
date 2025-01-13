package com.android.gids;

import java.util.List;

public class FormRequest {

    private String record_id;

    private String uuid;
    private String form_id;
    private String user_id;

    private String app_version;

    private String latitude;
    private String longtitute;
    private String created_at;

    private String is_location_from_mock_apps;
    private String mock_app_package;

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

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitute() {
        return longtitute;
    }

    public void setLongtitute(String longtitute) {
        this.longtitute = longtitute;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public List<FormData> getForm_data() {
        return form_data;
    }

    public void setForm_data(List<FormData> form_data) {
        this.form_data = form_data;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getIs_location_from_mock_apps() {
        return is_location_from_mock_apps;
    }

    public void setIs_location_from_mock_apps(String is_location_from_mock_apps) {
        this.is_location_from_mock_apps = is_location_from_mock_apps;
    }

    public String getMock_app_package() {
        return mock_app_package;
    }

    public void setMock_app_package(String mock_app_package) {
        this.mock_app_package = mock_app_package;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
