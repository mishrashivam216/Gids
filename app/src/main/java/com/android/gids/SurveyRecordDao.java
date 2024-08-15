package com.android.gids;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SurveyRecordDao {

    @Insert
    void insert(List<SurveyRecord> surveyRecord);

    @Update
    void update(SurveyRecord surveyRecord);

    @Delete
    void delete(SurveyRecord surveyRecord);

    @Query("SELECT * FROM survey_records WHERE record_id = :recordId")
    SurveyRecord getSurveyRecordById(String recordId);

    @Query("SELECT * FROM survey_records WHERE status = :status and form_id = :form_id and surveyor_id = :sid")
    List<SurveyRecord> getSurveyRecordByStatus(String status, String form_id, String sid);

    @Query("SELECT * FROM survey_records")
    List<SurveyRecord> getAllSurveyRecords();

    @Query("DELETE FROM survey_records")
    void deleteAllSurveyRecords();
}
