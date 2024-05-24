package com.example.quicknote.data.notesOrderDB
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.quicknote.domain.model.NotesOrderEntity

@Database(
    entities = [NotesOrderEntity::class],
    version = 7,
//    autoMigrations = [
//        AutoMigration (from = 1, to = 2, )
//    ]
    exportSchema = false
    )
abstract class NotesOrderDatabase : RoomDatabase() {
    abstract fun dao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: NotesOrderDatabase? = null

        fun getDatabase(context: Context): NotesOrderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotesOrderDatabase::class.java,
                    "notes_order"
                ).fallbackToDestructiveMigration() // Use with caution
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}