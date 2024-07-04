package com.example.dreaminterpreterai;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {User.class, Dream.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract UserDao userDao();
    public abstract DreamDao dreamDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                            AppDatabase.class) // Switch back to Room.databaseBuilder for production
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
