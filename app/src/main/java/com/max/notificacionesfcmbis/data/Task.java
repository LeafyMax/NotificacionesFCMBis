package com.max.notificacionesfcmbis.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    public int id;
    
    public String title;
    public String description;
    public String date;
    public String category;
    public boolean isCompleted;

    public Task(String title, String description, String date, String category, boolean isCompleted) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.category = category;
        this.isCompleted = isCompleted;
    }
}
