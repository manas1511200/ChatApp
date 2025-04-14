package com.example.chatapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class UserProfile (
    @PrimaryKey(autoGenerate = true)val id: Int = 0,
    val name: String,
    val LocalImageUrl: String,
    val ServerImageUrl: String,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP") val createdAt: Long = System.currentTimeMillis()
)
