package com.android.gids;

import com.android.gids.ui.home.RecordRequestModal;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {


    @Multipart
    @POST("file_upload") // Replace with your actual endpoint
    Call<Void> uploadFile(
            @Part MultipartBody.Part file,
            @Part("uuid") RequestBody uuid
    );

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
