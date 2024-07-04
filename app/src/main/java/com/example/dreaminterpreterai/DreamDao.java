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

    @Delete
    void delete(Dream dream);

    @Query("SELECT * FROM Dream WHERE userId = :userId ORDER BY groupId DESC, date DESC")
    List<Dream> getAllDreamsByUser(int userId);

    @Query("SELECT * FROM Dream WHERE userId = :userId AND groupId = :groupId ORDER BY date DESC")
    List<Dream> getDreamsByGroupId(int userId, int groupId);
}
