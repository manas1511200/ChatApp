package com.example.chatapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserProfile::class], version = 2, exportSchema = true)
abstract class UserDatabase:RoomDatabase() {
    abstract fun profileDao(): UserDao
    companion object{
        @Volatile
        private var Instance: UserDatabase? = null
        fun getDatabase(context: Context): UserDatabase{
            return this.Instance?:synchronized(this){
                Room.databaseBuilder(
                    context.applicationContext,
                    UserDatabase::class.java,
                    "user_database"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also { Instance =it }
            }
        }
    }


}