package com.android.gids;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MapDependencyFieldDao {

    @Insert
    void insert(MapDependencyField mapDependencyField);

    @Update
    void update(MapDependencyField mapDependencyField);

    @Delete
    void delete(MapDependencyField mapDependencyField);

    @Query("SELECT * FROM tbl_map_dependency_fields WHERE id = :id")
    MapDependencyField getById(int id);

    @Query("SELECT * FROM tbl_map_dependency_fields")
    List<MapDependencyField> getAll();


    @Query("select * from tbl_map_dependency_fields where parent_global_set_id= :vlookupId and child_global_set_id= :globaldatasetid")
    MapDependencyField getDependencyByValue(int vlookupId, int globaldatasetid);

}
