package com.example.dreaminterpreterai;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface DreamDao {
    @Insert
    void insert(Dream dream);

    @Query("SELECT * FROM Dream ORDER BY id DESC")
    List<Dream> getAllDreams();

    @Delete
    void delete(Dream dream);
}
