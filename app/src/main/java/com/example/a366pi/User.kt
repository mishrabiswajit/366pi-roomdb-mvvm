package com.example.a366pi

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    var id: Int,
    var firstName: String,
    var lastName: String,
    var email: String,
    var address: String,
    var phoneNumber: String,
    var city: String,
    var state: String,
    var pinCode: String,
    var country: String,
    val dob: String
)
