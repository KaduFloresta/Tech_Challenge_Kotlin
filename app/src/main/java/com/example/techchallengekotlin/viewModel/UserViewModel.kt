package com.example.techchallengekotlin.viewModel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techchallengekotlin.api.ApiServiceFactory
import com.example.techchallengekotlin.data.UserData
import com.example.techchallengekotlin.data.UserResponse
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {
    private val apiService = ApiServiceFactory.create()

    // User Data
    private val _userLiveData = MutableLiveData<List<UserData>>()
    val userLiveData: LiveData<List<UserData>> = _userLiveData

    // Get User Data
    fun searchUserData() {
        viewModelScope.launch {
            try {
                // Call API and get users data
                val response = apiService.getUsers()

                // Checks successful API response
                if (response.isSuccessful) {
                    // Converts response to a UserResponse object
                    val userResponse = response.body() as UserResponse

                    // Get UserData list from update LiveData
                    val userDataList = userResponse.users
                    _userLiveData.postValue(userDataList)
                } else {
                    // Unsuccessful API response = empty list
                    _userLiveData.postValue(emptyList())

                    // Error message and log
                    val errorMessage = "API request failed with code: ${response.code()}"
                    Log.e(TAG, errorMessage)

                    // Specifics Errors if necessary
                }
            } catch (e: Exception) {
                _userLiveData.postValue(emptyList())

                val exceptionMessage = "An exception occurred: ${e.message}"
                Log.e(TAG, exceptionMessage, e)
            }
        }
    }
}
