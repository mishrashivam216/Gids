package com.android.gids.ReviewModal;

import com.android.gids.FormStructureModal;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DataListModalReview {

    @SerializedName("form_id")
    public String id ;

    @SerializedName("form_name")
    public String name ;

    @SerializedName("form_description")
    public String description ;
    public String project_name ;

    @SerializedName("form_status")
    public String status ;

    public String record_id;

    public String uuid;

    public String record_status;

    public String surveyor_id;
    public String latitude;
    public String longtitute;

    public String created_at ;

    public String updated_at ;


    public List<FormStructureModalReview> FormStructure;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRecord_id() {
        return record_id;
    }

    public void setRecord_id(String record_id) {
        this.record_id = record_id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getRecord_status() {
        return record_status;
    }

    public void setRecord_status(String record_status) {
        this.record_status = record_status;
    }

    public String getSurveyor_id() {
        return surveyor_id;
    }

    public void setSurveyor_id(String surveyor_id) {
        this.surveyor_id = surveyor_id;
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

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public List<FormStructureModalReview> getFormStructure() {
        return FormStructure;
    }

    public void setFormStructure(List<FormStructureModalReview> formStructure) {
        FormStructure = formStructure;
    }
}
