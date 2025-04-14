package com.example.chatapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insertProfile(profile: UserProfile)
    @Query("SELECT * FROM profiles ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestProfile(): UserProfile?
}