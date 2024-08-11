package com.android.gids;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GlobalDataSetDao {
    @Insert
    void insertAll(List<GlobalDataSet> globalDataSets);

    @Query("SELECT * FROM tbl_global_data_set")
    List<GlobalDataSet> getAll();
}
