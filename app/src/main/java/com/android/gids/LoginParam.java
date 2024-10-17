package com.android.gids;

public class LoginParam {
    public String username = "";
    public String password = "";

    public String app_version = "";
    public String device_id = "";
    public String latitude = "";
    public String longtitude = "";

    public String mock_app_package = "";

    public String is_location_from_mock_apps = "";



    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }


    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getMock_app_package() {
        return mock_app_package;
    }

    public void setMock_app_package(String mock_app_package) {
        this.mock_app_package = mock_app_package;
    }

    public String getIs_location_from_mock_apps() {
        return is_location_from_mock_apps;
    }

    public void setIs_location_from_mock_apps(String is_location_from_mock_apps) {
        this.is_location_from_mock_apps = is_location_from_mock_apps;
    }
}
