package com.max.notificacionesfcmbis.data;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "checklist_items",
        foreignKeys = @ForeignKey(entity = Checklist.class,
                parentColumns = "id",
                childColumns = "checklistId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("checklistId")})
public class ChecklistItem {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int checklistId;
    public String text;
    public boolean isCompleted;

    public ChecklistItem(int checklistId, String text, boolean isCompleted) {
        this.checklistId = checklistId;
        this.text = text;
        this.isCompleted = isCompleted;
    }
}
