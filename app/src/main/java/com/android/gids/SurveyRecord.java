package com.android.gids;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "survey_records")
public class SurveyRecord {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "record_id")
    private String recordId;

    @ColumnInfo(name = "form_id")
    private String formId;

    @ColumnInfo(name = "surveyor_id")
    private String surveyorId;

    @ColumnInfo(name = "surveyor_name")
    private String surveyorName;

    @ColumnInfo(name = "section_display_order")
    private String sectionDisplayOrder;

    @ColumnInfo(name = "form_step")
    private String formStep;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "created_at")
    private String createdAt;

    @ColumnInfo(name = "updated_at")
    private String updatedAt;

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getSurveyorId() {
        return surveyorId;
    }

    public void setSurveyorId(String surveyorId) {
        this.surveyorId = surveyorId;
    }

    public String getSurveyorName() {
        return surveyorName;
    }

    public void setSurveyorName(String surveyorName) {
        this.surveyorName = surveyorName;
    }

    public String getSectionDisplayOrder() {
        return sectionDisplayOrder;
    }

    public void setSectionDisplayOrder(String sectionDisplayOrder) {
        this.sectionDisplayOrder = sectionDisplayOrder;
    }

    public String getFormStep() {
        return formStep;
    }

    public void setFormStep(String formStep) {
        this.formStep = formStep;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
