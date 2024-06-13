package com.example.dreaminterpreterai.dreamdiary;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.dreaminterpreterai.dreamdiary.Dream;

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
