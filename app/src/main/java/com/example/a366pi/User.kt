package com.example.a366pi

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    var id: Int,
    var first_name: String,
    var last_name: String,
    var email: String,
)
