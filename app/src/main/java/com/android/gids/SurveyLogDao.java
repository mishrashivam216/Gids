package com.android.gids;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SurveyLogDao {

    @Insert
    void insert(SurveyLog surveyLog);

    @Update
    void update(SurveyLog surveyLog);

    @Query("DELETE FROM survey_log WHERE id = :id")
    void deleteById(int id);

    @Query("SELECT * FROM survey_log WHERE user_id = :id ORDER BY ID DESC")
    List<SurveyLog> getByUserId(String id);

    @Query("SELECT * FROM survey_log")
    List<SurveyLog> getAll();
}

