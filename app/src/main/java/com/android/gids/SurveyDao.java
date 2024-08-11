package com.android.gids;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SurveyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SurveyData surveyData);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateList(List<SurveyData> surveyData);

    @Query("SELECT * FROM survey_data WHERE id IN (SELECT MIN(id) FROM survey_data WHERE form_id = :formId GROUP BY instance_id) ORDER BY ID DESC")
    List<SurveyData> getUniqueInstanceIdsByFormId(String formId);


    @Query("SELECT * FROM survey_data ORDER BY id ASC")
    List<SurveyData> getAll();


    @Query("SELECT * FROM survey_data where sync_status = '0' ORDER BY id ASC")
    List<SurveyData> getToSync();

    @Query("SELECT * FROM survey_data WHERE form_id = :formId and instance_id = :instanceId")
    List<SurveyData> getToSyncByFormInstanceId(String formId, int instanceId);


    @Query("update survey_data set sync_status = '1' where sync_status = '0'")
    void updateSyncStatus();

    @Query("delete FROM survey_data")
    void delete();


    @Query("delete FROM survey_data WHERE form_id = :formId and instance_id = :instanceId")
    void deletebyFormIdInstanceId(String formId, int instanceId);


    @Query("SELECT * FROM survey_data WHERE form_id = :formId and instance_id = :instanceId and question_id = :qid")
    SurveyData getPredefinedAnswer(String formId, int instanceId, String qid);

    @Query("SELECT * FROM survey_data WHERE form_id = :formId AND instance_id = :instanceId  ORDER BY id DESC LIMIT 1")
    SurveyData getLastEntryByForm(String formId, int instanceId);

    @Query("SELECT * FROM survey_data WHERE form_id = :formId and instance_id = :instanceId")
    List<SurveyData> getPredefinedAnswerList(String formId, int instanceId);


    @Query("UPDATE survey_data SET field_value = :newFieldValue WHERE question_id = :questionId AND instance_id = :instanceId AND form_id = :formId")
    void updateByFields(String questionId, int instanceId, String formId, String newFieldValue);


    @Query("delete FROM survey_data WHERE question_id = :qid")
    void deletebyFormQuestionId(String qid);


}
