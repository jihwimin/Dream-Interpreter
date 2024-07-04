package com.example.dreaminterpreterai;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Dream {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String date;
    public String dream;
    public String interpretation;
    public int userId; // Add userId field
    public int groupId;

    public Dream(String date, String dream, String interpretation, int userId, int groupId) {
        this.date = date;
        this.dream = dream;
        this.interpretation = interpretation;
        this.userId = userId;
        this.groupId = groupId;
    }
}
