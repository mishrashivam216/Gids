package com.android.gids;

import java.util.List;

public class DataListModal {

    public String id ;
    public String name ;
    public String description ;
    public String project_name ;
    public String status ;
    public String created_at ;
    public String updated_at ;
    public int pending_records_count;
    public int underreview_records_count;
    public int feedback_records_count;
    public int complete_records_count;
    public int total_records_count;

    public List<FormStructureModal> FormStructure;

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

    public int getPending_records_count() {
        return pending_records_count;
    }

    public void setPending_records_count(int pending_records_count) {
        this.pending_records_count = pending_records_count;
    }

    public int getUnderreview_records_count() {
        return underreview_records_count;
    }

    public void setUnderreview_records_count(int underreview_records_count) {
        this.underreview_records_count = underreview_records_count;
    }

    public int getFeedback_records_count() {
        return feedback_records_count;
    }

    public void setFeedback_records_count(int feedback_records_count) {
        this.feedback_records_count = feedback_records_count;
    }

    public int getComplete_records_count() {
        return complete_records_count;
    }

    public void setComplete_records_count(int complete_records_count) {
        this.complete_records_count = complete_records_count;
    }

    public int getTotal_records_count() {
        return total_records_count;
    }

    public void setTotal_records_count(int total_records_count) {
        this.total_records_count = total_records_count;
    }

    public List<FormStructureModal> getFormStructure() {
        return FormStructure;
    }

    public void setFormStructure(List<FormStructureModal> formStructure) {
        FormStructure = formStructure;
    }
}
