package com.android.gids.RandomModule;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.android.gids.GlobalDataSetValue;

import java.util.List;

@Dao
public interface DistrictDao {


    @Query("SELECT * FROM district_table")
    List<District> getAllDistrict();


}
