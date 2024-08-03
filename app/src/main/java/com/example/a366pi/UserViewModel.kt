package com.example.a366pi

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = AppDatabase.getInstance(application).userDao()

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> get() = _users

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    // Quick
    init {
        viewModelScope.launch {
            try {
                _users.value = userDao.getAllUsers() // Fetch users directly from the database
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    _errorMessage.value = "Error loading users from database"
                }
            }
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            val success = addUserToDb(user)
            if (success) {
                _users.value = getUsersFromDb() // Update the list of users
            } else {
                _errorMessage.value = "Failed to add user"
            }
        }
    }

    private suspend fun getUsersFromDb(): List<User> {
        return withContext(Dispatchers.IO) {
            userDao.getAllUsers()
        }
    }

    // This function adds the user details into the database
    private suspend fun addUserToDb(user: User): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                userDao.insertUser(user)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
