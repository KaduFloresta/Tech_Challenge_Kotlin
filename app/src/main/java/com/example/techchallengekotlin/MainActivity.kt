package com.example.techchallengekotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.example.techchallengekotlin.api.ApiServiceFactory
import com.example.techchallengekotlin.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoroutineScope(Dispatchers.IO).launch {
            fetchUserDataFromApi()
        }
    }

    private suspend fun fetchUserDataFromApi() {
        try {
            val apiService = ApiServiceFactory.create()
            val response = apiService.getUsers()

            if (response.isSuccessful) {
                val userResponse = response.body()
                val userDataList = userResponse?.users.orEmpty()

                runOnUiThread {
                    val groupIds = listOf(
                        R.id.cardContainer1,
                        R.id.cardContainer2,
                        R.id.cardContainer3,
                        R.id.cardContainer4,
                        R.id.cardContainer5
                    )

                    val usersPerGroup = userDataList.size / groupIds.size
                    var currentGroupIndex = 0

                    for ((index, userData) in userDataList.withIndex()) {
                        if (currentGroupIndex < groupIds.size) {
                            val groupId = groupIds[currentGroupIndex]
                            val groupLayout = findViewById<LinearLayout>(groupId)

                            val cardLayout =
                                layoutInflater.inflate(R.layout.card_layout, groupLayout, false)
                            val name = cardLayout.findViewById<TextView>(R.id.text_name)
                            val email = cardLayout.findViewById<TextView>(R.id.text_email)
                            val age = cardLayout.findViewById<TextView>(R.id.text_age)
                            val id = cardLayout.findViewById<TextView>(R.id.text_id)

                            name.text = userData.name
                            email.text = userData.email
                            age.text = userData.age.toString()
                            id.text = userData.id.toString()

                            groupLayout.addView(cardLayout)

                            if ((index + 1) % usersPerGroup == 0) {
                                currentGroupIndex++
                            }
                        }
                    }
                }
            } else {
                Log.e(
                    "API_ERROR",
                    "Error fetching user data from API: ${response.code()} - ${response.message()}"
                )
            }
        } catch (e: Exception) {
            Log.e("API_ERROR", "Error fetching user data from API: ${e.message}")
            // Lidar com erros, como problemas de conexão, aqui
        }
    }
}