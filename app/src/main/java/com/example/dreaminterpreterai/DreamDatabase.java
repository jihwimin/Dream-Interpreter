package com.example.dreaminterpreterai;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Dream.class, DreamPhoto.class}, version = 1)
public abstract class DreamDatabase extends RoomDatabase {
    private static DreamDatabase instance;

    public abstract DreamDao dreamDao();
    public abstract DreamPhotoDao dreamPhotoDao();

    public static synchronized DreamDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            DreamDatabase.class, "dream_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
