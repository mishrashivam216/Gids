package com.android.gids;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface InstanceStatusDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(InstanceStatus instanceStatus);


    @Query("delete FROM instance_status WHERE form_id= :formId and instance_id = :instanceId and isSubmitted = 1")
    void deletebyInstanceId(String formId, int instanceId);

    @Query("delete FROM instance_status WHERE form_id= :formId and uuid = :uuid and isSubmitted = 1")
    void deletebyRecordId(String formId, String  uuid);


    @Query("SELECT * FROM instance_status WHERE  form_id= :formId and instance_id = :instanceId and isSubmitted = 1")
    InstanceStatus getToSyncByFormInstanceId(String formId, int instanceId);

    @Query("SELECT * FROM instance_status WHERE  form_id= :formId and uuid = :uuid and isSubmitted = 1")
    InstanceStatus getToSyncByFormByUUID(String formId, String uuid);



}
