package com.android.gids;

import com.android.gids.ui.home.RecordRequestModal;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiInterface {


    @Headers("Content-Type: application/json")
    @POST("login")
    Call<LoginModal> doLogin(@Body LoginParam loginParam);


    @Headers("Content-Type: application/json")
    @POST("logout")
    Call<LogoutRes> doLogout(@Body LogoutParam logoutParam);


    @Headers("Content-Type: application/json")
    @POST("form_list")
    Call<JsonObject> formList(@Body FormListRequest formListRequest);


    @Headers("Content-Type: application/json")
    @POST("form_submit")
    Call<JsonObject> sendFormData(@Body FormRequest formRequest);

    @Headers("Content-Type: application/json")
    @POST("form_list_status")
    Call<JsonObject> formListStatus(@Body FormListStatusRequest formListStatusRequest);


    @Headers("Content-Type: application/json")
    @POST("record")
    Call<JsonObject> getRecord(@Body RecordRequestModal formReq);


}
