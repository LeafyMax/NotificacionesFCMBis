package com.max.notificacionesfcmbis.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ChecklistDao {
    @Query("SELECT * FROM checklists")
    List<Checklist> getAllChecklists();

    @Insert
    long insertChecklist(Checklist checklist);

    @Delete
    void deleteChecklist(Checklist checklist);

    @Query("SELECT * FROM checklist_items WHERE checklistId = :checklistId")
    List<ChecklistItem> getItemsForChecklist(int checklistId);

    @Insert
    void insertItem(ChecklistItem item);

    @Update
    void updateItem(ChecklistItem item);

    @Delete
    void deleteItem(ChecklistItem item);

    @Transaction
    default void updateItemAndCheckCompletion(ChecklistItem item) {
        updateItem(item);
        List<ChecklistItem> items = getItemsForChecklist(item.checklistId);
        boolean allDone = true;
        for (ChecklistItem i : items) {
            if (!i.isCompleted) {
                allDone = false;
                break;
            }
        }
        if (allDone && !items.isEmpty()) {
            deleteChecklistById(item.checklistId);
        }
    }

    @Query("DELETE FROM checklists WHERE id = :id")
    void deleteChecklistById(int id);
}
