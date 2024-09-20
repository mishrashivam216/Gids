package com.android.gids.RandomModule;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "district_table")
public class District {
    @PrimaryKey
    private int id;
    private String name;

    public District(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
