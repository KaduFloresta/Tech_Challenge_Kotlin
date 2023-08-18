package com.example.techchallengekotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.techchallengekotlin.api.ApiServiceFactory
import com.example.techchallengekotlin.data.UserData
import com.example.techchallengekotlin.databinding.ActivityMainBinding
import com.example.techchallengekotlin.databinding.CardLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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
                        binding.cardContainer1.id,
                        binding.cardContainer2.id,
                        binding.cardContainer3.id,
                        binding.cardContainer4.id,
                        binding.cardContainer5.id
                    )

                    val usersPerGroup = userDataList.size / groupIds.size
                    var currentGroupIndex = 0

                    for ((index, userData) in userDataList.withIndex()) {
                        if (currentGroupIndex < groupIds.size) {
                            val groupId = groupIds[currentGroupIndex]
                            val groupLayout = binding.root.findViewById<LinearLayout>(groupId)
                            val cardLayout =
                                layoutInflater.inflate(R.layout.card_layout, groupLayout, false)

                            val cardBinding = CardLayoutBinding.bind(cardLayout)

                            cardBinding.textName.text = userData.name
                            cardBinding.textEmail.text = userData.email
                            cardBinding.textAge.text = userData.age.toString()
                            cardBinding.textId.text = userData.id.toString()

                            cardBinding.iconEdit.setOnClickListener {
//                                enterEditMode(cardBinding)
                            }

                            cardBinding.iconDelete.setOnClickListener {
                                showDeleteConfirmationDialog(userData, cardBinding)
                            }

                            groupLayout.addView(cardLayout)

                            if ((index + 1) % usersPerGroup == 0) {
                                currentGroupIndex++
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Erro: layout de grupo não encontrado", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Ocorreu um erro: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun enterEditMode(cardBinding: CardLayoutBinding) {
        // modo de edição
    }

    private fun showDeleteConfirmationDialog(userData: UserData, cardBinding: CardLayoutBinding) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Confirmation")
        alertDialogBuilder.setMessage("Are you sure you want to delete this card?")

        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val parentLayout = cardBinding.root.parent as? ViewGroup
            parentLayout?.removeView(cardBinding.root)
            // remover o card dos dados
        }

        alertDialogBuilder.setNegativeButton("No") { _, _ ->
        }

        alertDialogBuilder.show()
    }

}
