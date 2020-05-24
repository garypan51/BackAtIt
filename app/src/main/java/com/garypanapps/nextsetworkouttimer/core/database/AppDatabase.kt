package com.garypanapps.nextsetworkouttimer.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.TypeConverters
import androidx.room.Room
import androidx.room.RoomDatabase
import com.garypanapps.nextsetworkouttimer.core.database.daos.TimerDao
import com.garypanapps.nextsetworkouttimer.core.database.typeConverters.TimerTypeConverter
import com.garypanapps.nextsetworkouttimer.core.entities.Timer

@Database(entities = [Timer::class], version = 1)
@TypeConverters(TimerTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun timerDao(): TimerDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Timer_database"
                    ).build()
                INSTANCE = instance
                instance
            }
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}