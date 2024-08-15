package com.android.gids;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FormListEntity.class, SurveyData.class, InstanceStatus.class, GlobalDataSet.class, GlobalDataSetValue.class, MapDependencyFieldValue.class, MapDependencyField.class, SurveyLog.class, SurveyRecord.class}, version = 1, exportSchema = false)
public abstract class SurveyRoomDatabase extends RoomDatabase {

    public abstract FormListDao formListDao();

    public abstract SurveyDao surveyDao();

    public abstract InstanceStatusDao instanceStatusDao();

    public abstract GlobalDataSetDao globalDataSetDao();

    public abstract GlobalDataSetValueDao globalDataSetValueDao();

    public abstract MapDependencyFieldValueDao mapDependencyFieldValueDao();

    public abstract MapDependencyFieldDao mapDependencyFieldDao();

    public abstract SurveyLogDao surveyLogDao();
    public abstract SurveyRecordDao surveyRecordDao();

    private final static String DB_NAME = "SurveyRoomDatabase";

    private static volatile SurveyRoomDatabase INSTANCE;

    public static SurveyRoomDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (RoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, SurveyRoomDatabase.class, DB_NAME).allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }
}