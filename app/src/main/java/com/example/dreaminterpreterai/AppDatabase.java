package com.example.dreaminterpreterai;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, Dream.class}, version = 3) // Update version number
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract DreamDao dreamDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "dream_interpreter_db")
                    .fallbackToDestructiveMigration() // Handle migration
                    .build();
        }
        return instance;
    }
}
