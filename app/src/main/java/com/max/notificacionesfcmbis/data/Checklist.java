package com.max.notificacionesfcmbis.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "checklists")
public class Checklist {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String title;

    public Checklist(String title) {
        this.title = title;
    }
}
