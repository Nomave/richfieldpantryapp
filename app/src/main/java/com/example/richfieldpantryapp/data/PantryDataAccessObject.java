package com.example.richfieldpantryapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PantryDataAccessObject {

    //Read
    @Query("SELECT * FROM pantry_items ORDER BY LOWER(name) ASC")
    LiveData<List<PantryItem>>getAll();

    @Query("SELECT * FROM pantry_items ORDER BY LOWER(name) ASC")
    List<PantryItem> getAllSync();

    @Query("SELECT * FROM pantry_items WHERE id = :id LIMIT 1")
    PantryItem getByIdSync(long id);

    @Query("SELECT COUNT(*) FROM pantry_items")
    int countSync();
    //Create

    @Insert
    long insert(PantryItem item);
    //Update

    @Update
    void update(PantryItem item);

    //Delete
    @Delete
    void delete(PantryItem item);

    @Query("DELETE FROM pantry_items WHERE id = :id")
    void deleteById(long id);

    @Query("DELETE FROM pantry_items")
    void deleteAll();
}
