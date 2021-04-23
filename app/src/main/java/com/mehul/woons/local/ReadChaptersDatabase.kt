package com.mehul.woons.local

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mehul.woons.entities.Chapter

@Database(entities = [Chapter::class], version = 1, exportSchema = false)
abstract class ReadChaptersDatabase : RoomDatabase() {

    abstract fun readChaptersDao(): ReadChaptersDao

    companion object {
        @Volatile
        private var instance: ReadChaptersDatabase? = null

        fun getDatabase(context: Application): ReadChaptersDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }

        private fun buildDatabase(appContext: Application) =
            Room.databaseBuilder(appContext, ReadChaptersDatabase::class.java, "chapters")
                .fallbackToDestructiveMigration()
                .build()
    }

}