package com.mehul.woons.local

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mehul.woons.entities.Chapter
import com.mehul.woons.entities.Webtoon

@Database(entities = [Chapter::class, Webtoon::class], version = 3, exportSchema = false)
abstract class LibraryDatabase : RoomDatabase() {

    abstract fun readChaptersDao(): LibraryDao

    companion object {
        @Volatile
        private var instance: LibraryDatabase? = null

        fun getDatabase(context: Application): LibraryDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }

        private fun buildDatabase(appContext: Application) =
            Room.databaseBuilder(appContext, LibraryDatabase::class.java, "chapters")
                .fallbackToDestructiveMigration()
                .build()
    }

}