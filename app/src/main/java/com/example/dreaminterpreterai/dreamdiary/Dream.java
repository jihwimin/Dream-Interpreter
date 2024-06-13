package com.example.dreaminterpreterai.dreamdiary;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Dream {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String date;
    public String dream;
    public String interpretation;

    public Dream(String date, String dream, String interpretation) {
        this.date = date;
        this.dream = dream;
        this.interpretation = interpretation;
    }
}
