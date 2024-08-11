package com.android.gids;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_map_dependency_fields")
public class MapDependencyField {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "parent_global_set_id")
    private int parentGlobalSetId;

    @ColumnInfo(name = "child_global_set_id")
    private int childGlobalSetId;

    @ColumnInfo(name = "created_by")
    private int createdBy;

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

    public int getParentGlobalSetId() {
        return parentGlobalSetId;
    }

    public void setParentGlobalSetId(int parentGlobalSetId) {
        this.parentGlobalSetId = parentGlobalSetId;
    }

    public int getChildGlobalSetId() {
        return childGlobalSetId;
    }

    public void setChildGlobalSetId(int childGlobalSetId) {
        this.childGlobalSetId = childGlobalSetId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
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
