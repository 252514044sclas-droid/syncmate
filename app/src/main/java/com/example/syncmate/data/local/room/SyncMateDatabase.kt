package com.example.syncmate.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [HousingListingEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class SyncMateDatabase : RoomDatabase() {

    abstract fun housingDao(): HousingDao

    companion object {
        @Volatile
        private var INSTANCE: SyncMateDatabase? = null

        fun getDatabase(context: Context): SyncMateDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SyncMateDatabase::class.java,
                    "syncmate_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}