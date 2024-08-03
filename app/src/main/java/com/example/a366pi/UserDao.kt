package com.example.a366pi

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    // Inserting Single User
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // Getting all the users
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>
}

