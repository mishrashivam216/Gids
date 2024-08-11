package com.android.gids;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GlobalDataSetValueDao {

    @Insert
    void insert(GlobalDataSetValue globalDataSetValue);

    @Update
    void update(GlobalDataSetValue globalDataSetValue);

    @Delete
    void delete(GlobalDataSetValue globalDataSetValue);

    @Query("SELECT * FROM tbl_global_data_set_values WHERE id = :id")
    GlobalDataSetValue getById(int id);

    @Query("SELECT * FROM tbl_global_data_set_values WHERE tbl_global_data_set_id = :globalDataSetId")
    List<GlobalDataSetValue> getByGlobalDataSetId(int globalDataSetId);

    @Query("SELECT * FROM tbl_global_data_set_values")
    List<GlobalDataSetValue> getAll();
}
