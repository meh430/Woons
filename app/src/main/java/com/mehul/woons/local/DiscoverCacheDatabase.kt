package com.mehul.woons.local

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mehul.woons.entities.WebtoonsPage

@Database(entities = [WebtoonsPage::class], version = 1, exportSchema = false)
@TypeConverters(DiscoverCacheConverter::class)
abstract class DiscoverCacheDatabase : RoomDatabase() {

    abstract fun discoverCacheDao(): DiscoverCacheDao

    companion object {
        @Volatile
        private var instance: DiscoverCacheDatabase? = null

        fun getDatabase(context: Application): DiscoverCacheDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }

        private fun buildDatabase(appContext: Application) =
            Room.databaseBuilder(appContext, DiscoverCacheDatabase::class.java, "discover")
                .fallbackToDestructiveMigration()
                .build()
    }

}