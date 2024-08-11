package com.android.gids;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.w3c.dom.Text;

import java.util.List;

@Dao
public interface FormListDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(FormListEntity formList);

    @Query("DELETE FROM form_list")
    void deleteAll();

    @Query("SELECT * FROM form_list ORDER BY id ASC")
    List<FormListEntity> getAllFormList();


}
