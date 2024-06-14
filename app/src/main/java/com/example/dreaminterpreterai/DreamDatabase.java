package com.example.dreaminterpreterai;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;

@Database(entities = {Dream.class}, version = 2) // Incremented version number
public abstract class DreamDatabase extends RoomDatabase {
    private static DreamDatabase instance;

    public abstract DreamDao dreamDao();

    // Define migration strategy from version 1 to version 2
    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Define migration strategy here
            // Example: Adding a new column to the Dream table
            // database.execSQL("ALTER TABLE Dream ADD COLUMN new_column_name TEXT");
        }
    };

    public static synchronized DreamDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            DreamDatabase.class, "dream_database")
                    .addMigrations(MIGRATION_1_2) // Apply the migration
                    .fallbackToDestructiveMigration() // Optional: Automatically recreate database if no migration is provided
                    .build();
        }
        return instance;
    }
}
