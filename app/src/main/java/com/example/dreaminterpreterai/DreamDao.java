package com.example.dreaminterpreterai;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DreamDao {
    @Query("SELECT * FROM Dream WHERE userId = :userId ORDER BY id DESC")
    List<Dream> getDreamsForUser(int userId);

    @Insert
    void insert(Dream dream);

    @Delete
    void delete(Dream dream);
}
