package com.android.gids;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MapDependencyFieldValueDao {

    @Insert
    void insert(MapDependencyFieldValue mapDependencyFieldValue);

    @Update
    void update(MapDependencyFieldValue mapDependencyFieldValue);

    @Delete
    void delete(MapDependencyFieldValue mapDependencyFieldValue);

    @Query("SELECT * FROM tbl_map_dependency_field_value WHERE id = :id")
    MapDependencyFieldValue getById(int id);

    @Query("SELECT * FROM tbl_map_dependency_field_value WHERE tbl_map_dependency_fields_id = :mapDependencyFieldsId")
    List<MapDependencyFieldValue> getByMapDependencyFieldsId(int mapDependencyFieldsId);

    @Query("SELECT * FROM tbl_map_dependency_field_value")
    List<MapDependencyFieldValue> getAll();

    @Query("select * from tbl_map_dependency_field_value where tbl_map_dependency_fields_id= :tbl_map_dependency_fields_id and tbl_global_data_set_value_id_primary= :SelectedId")
    List<MapDependencyFieldValue> getIdForMainTable(int tbl_map_dependency_fields_id, int SelectedId);
}

