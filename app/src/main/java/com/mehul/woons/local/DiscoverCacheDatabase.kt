package com.mehul.woons.local

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mehul.woons.entities.WebtoonsPage

@Database(entities = [WebtoonsPage::class], version = 3, exportSchema = false)
@TypeConverters(DiscoverCacheConverter::class)
abstract class DiscoverCacheDatabase : RoomDatabase() {

    abstract fun discoverCacheDao(): DiscoverCacheDao

    companion object {
        @Volatile
        private var instance: DiscoverCacheDatabase? = null

        fun getDatabase(
            context: Application,
            discoverCacheConverter: DiscoverCacheConverter
        ): DiscoverCacheDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context, discoverCacheConverter).also {
                    instance = it
                }
            }

        private fun buildDatabase(
            appContext: Application,
            discoverCacheConverter: DiscoverCacheConverter
        ) =
            Room.databaseBuilder(appContext, DiscoverCacheDatabase::class.java, "discover")
                .addTypeConverter(discoverCacheConverter)
                .fallbackToDestructiveMigration()
                .build()
    }

}