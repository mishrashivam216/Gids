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

    @Query("SELECT * FROM survey_data WHERE id IN (SELECT MIN(id) FROM survey_data WHERE form_id = :formId AND source = 1 AND user_id = :user_id GROUP BY instance_id) ORDER BY ID DESC")
    List<SurveyData> getUniqueInstanceIdsByFormId(String formId, String user_id);

    @Query("SELECT * FROM survey_data WHERE form_id = :formId and record_id = :uuid")
    SurveyData getInstanceID(String formId, String uuid);


    @Query("SELECT * FROM survey_data ORDER BY id ASC")
    List<SurveyData> getAll();


    @Query("SELECT * FROM survey_data where sync_status = '0' ORDER BY id ASC")
    List<SurveyData> getToSync();

    @Query("SELECT * FROM survey_data WHERE form_id = :formId and instance_id = :instanceId")
    List<SurveyData> getToSyncByFormInstanceId(String formId, int instanceId);


    @Query("SELECT * FROM survey_data WHERE form_id = :formId and record_id = :recordId")
    List<SurveyData> getToSyncByFormRecordId(String formId, String recordId);


    @Query("update survey_data set sync_status = '1' where sync_status = '0'")
    void updateSyncStatus();

    @Query("delete FROM survey_data")
    void delete();


    @Query("delete FROM survey_data WHERE form_id = :formId and instance_id = :instanceId")
    void deletebyFormIdInstanceId(String formId, int instanceId);

    @Query("delete FROM survey_data WHERE form_id = :formId and record_id = :recordId")
    void deletebyFormIdRecordId(String formId, String recordId);

    @Query("delete FROM survey_data WHERE form_id = :formId and record_id = :recId")
    void deletebyFormUUID(String formId, String recId);


    @Query("SELECT * FROM survey_data WHERE form_id = :formId and instance_id = :instanceId and question_id = :qid")
    SurveyData getPredefinedAnswer(String formId, int instanceId, String qid);

    @Query("SELECT * FROM survey_data WHERE form_id = :formId and record_id = :recId and question_id = :qid")
    SurveyData getPredefinedAnswerReview(String formId, String recId, String qid);

    @Query("SELECT * FROM survey_data WHERE form_id = :formId and record_id = :uuid and question_id = :qid")
    SurveyData getPredefinedAnswerByUUID(String formId, String uuid, String qid);

    @Query("SELECT * FROM survey_data WHERE form_id = :formId AND instance_id = :instanceId  ORDER BY id DESC LIMIT 1")
    SurveyData getLastEntryByForm(String formId, int instanceId);


    @Query("SELECT * FROM survey_data WHERE form_id = :formId AND record_id = :recId  ORDER BY id DESC LIMIT 1")
    SurveyData getLastEntryByRecId(String formId, String recId);

    @Query("SELECT * FROM survey_data WHERE form_id = :formId and instance_id = :instanceId")
    List<SurveyData> getPredefinedAnswerList(String formId, int instanceId);


    @Query("UPDATE survey_data SET field_value = :newFieldValue WHERE question_id = :questionId AND instance_id = :instanceId AND form_id = :formId")
    void updateByFields(String questionId, int instanceId, String formId, String newFieldValue);

    @Query("UPDATE survey_data SET field_value = :newFieldValue WHERE question_id = :questionId AND record_id = :recId AND form_id = :formId")
    void updateByFieldsReview(String questionId, String recId, String formId, String newFieldValue);


    @Query("delete FROM survey_data WHERE question_id = :qid AND instance_id = :instanceId AND form_id = :formId")
    void deletebyFormQuestionId(String qid, int instanceId, String formId );


    @Query("delete FROM survey_data WHERE question_id = :qid AND record_id = :record_id AND form_id = :formId")
    void deletebyFormQuestionIdReview(String qid, String record_id, String formId);

}
