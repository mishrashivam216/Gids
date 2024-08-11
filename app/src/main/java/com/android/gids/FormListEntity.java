package com.android.gids;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "form_list")
public class FormListEntity {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "id")
    private int id;




    @ColumnInfo(name = "filePath")
    private String filePath;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }




    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
