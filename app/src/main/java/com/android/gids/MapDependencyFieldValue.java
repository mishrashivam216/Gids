package com.android.gids;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tbl_map_dependency_field_value")
public class MapDependencyFieldValue {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "tbl_map_dependency_fields_id")
    private int mapDependencyFieldsId;

    @ColumnInfo(name = "tbl_global_data_set_value_id_primary")
    private int globalDataSetValueIdPrimary;

    @ColumnInfo(name = "tbl_global_data_set_value_id_secondry")
    private int globalDataSetValueIdSecondry;

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

    public int getMapDependencyFieldsId() {
        return mapDependencyFieldsId;
    }

    public void setMapDependencyFieldsId(int mapDependencyFieldsId) {
        this.mapDependencyFieldsId = mapDependencyFieldsId;
    }

    public int getGlobalDataSetValueIdPrimary() {
        return globalDataSetValueIdPrimary;
    }

    public void setGlobalDataSetValueIdPrimary(int globalDataSetValueIdPrimary) {
        this.globalDataSetValueIdPrimary = globalDataSetValueIdPrimary;
    }

    public int getGlobalDataSetValueIdSecondry() {
        return globalDataSetValueIdSecondry;
    }

    public void setGlobalDataSetValueIdSecondry(int globalDataSetValueIdSecondry) {
        this.globalDataSetValueIdSecondry = globalDataSetValueIdSecondry;
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

