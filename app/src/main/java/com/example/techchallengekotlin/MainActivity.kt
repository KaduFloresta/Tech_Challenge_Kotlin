package com.example.techchallengekotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextPaint
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.techchallengekotlin.api.ApiServiceFactory
import com.example.techchallengekotlin.databinding.ActivityMainBinding
import com.example.techchallengekotlin.databinding.CardLayoutBinding
import com.example.techchallengekotlin.databinding.EditCardLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Custom toolbar - Hide app title
        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Fetch data from the API asynchronously
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

                // Update the UI
                runOnUiThread {
                    val groupIds = listOf(
                        binding.cardContainer1.id,
                        binding.cardContainer2.id,
                        binding.cardContainer3.id,
                        binding.cardContainer4.id,
                        binding.cardContainer5.id
                    )

                    // Calculate the number of users per group
                    val usersPerGroup = userDataList.size / groupIds.size
                    var currentGroupIndex = 0

                    for ((index, userData) in userDataList.withIndex()) {
                        if (currentGroupIndex < groupIds.size) {
                            val groupId = groupIds[currentGroupIndex]
                            val groupLayout = binding.root.findViewById<LinearLayout>(groupId)

                            // Inflate card
                            val cardLayout =
                                layoutInflater.inflate(R.layout.card_layout, groupLayout, false)

                            // Bind layout using binding class
                            val cardBinding = CardLayoutBinding.bind(cardLayout)

                            // Add data - name and email are adjusted to fix
                            cardBinding.textName.viewTreeObserver.addOnGlobalLayoutListener {
                                adjustFontSize(
                                    cardBinding.textName,
                                    userData.name,
                                    R.dimen.default_name_text_size
                                )
                            }

                            cardBinding.textEmail.viewTreeObserver.addOnGlobalLayoutListener {
                                adjustFontSize(
                                    cardBinding.textEmail,
                                    userData.email,
                                    R.dimen.default_email_text_size
                                )
                            }

                            cardBinding.textAge.text = userData.age.toString()
                            cardBinding.textId.text = userData.id.toString()

                            cardBinding.iconEdit.setOnClickListener {
                                editMode(cardBinding)
                            }

                            cardBinding.iconDelete.setOnClickListener {
                                deleteMode(cardBinding)
                            }

                            // Add card to the group
                            groupLayout.addView(cardLayout)

                            // // Calculate the current group index
                            if ((index + 1) % usersPerGroup == 0) {
                                currentGroupIndex++
                            }
                        }
                    }
                }

                // Show errors messages: Group not found and exception occurs
            } else {
                Toast.makeText(this, "Error: Group layout no found!", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    // Font size adjust method
    private fun adjustFontSize(textView: TextView, text: String, defaultSizeRes: Int) {
        val maxWidth = textView.width // Max width for TextView
        val paint = TextPaint(textView.paint) // Measuring text width
        val defaultSize = resources.getDimension(defaultSizeRes) // Get default text size
        paint.textSize = defaultSize // Set the text size to default

        // if text size is too wide
        val textWidth = paint.measureText(text)
        if (textWidth > maxWidth) {
            val scaleFactor = maxWidth / textWidth
            val newSize = defaultSize * scaleFactor
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize)
        } else {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultSize)
        }

        // Set the text
        textView.text = text
    }

    private fun editMode(cardBinding: CardLayoutBinding) {
        val dialogBinding = EditCardLayoutBinding.inflate(layoutInflater) // Edit mode dialog

        // Put the data on the fields
        dialogBinding.editName.setText(cardBinding.textName.text)
        dialogBinding.editEmail.setText(cardBinding.textEmail.text)
        dialogBinding.editAge.setText(cardBinding.textAge.text)
        dialogBinding.editId.text = cardBinding.textId.text

        // Id is not editable
        dialogBinding.editId.isEnabled = false

        // Build the edit mode
        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setTitle("")

        // Create and show
        val dialog = dialogBuilder.create()

        // Listeners to icons
        dialogBinding.iconConfirm.setOnClickListener {
            // Get values typed
            val newName = dialogBinding.editName.text.toString()
            val newEmail = dialogBinding.editEmail.text.toString()
            val newAge = dialogBinding.editAge.text.toString()

            // Not allowed empty fields
            if (newName.isNotEmpty() && newEmail.isNotEmpty() && newAge.isNotEmpty()) {
                cardBinding.textName.text = newName
                cardBinding.textEmail.text = newEmail
                cardBinding.textAge.text = newAge

                // Adjust font to fix
                adjustFontSize(cardBinding.textName, newName, R.dimen.default_name_text_size)
                adjustFontSize(cardBinding.textEmail, newEmail, R.dimen.default_email_text_size)

                dialog.dismiss()
            } else {
                // Message to user about empty fields
                Toast.makeText(this, "Complete all the fields!", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.iconCancel.setOnClickListener {
            // close dialog
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteMode(cardBinding: CardLayoutBinding) {
        // Dialog delete confirmation
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Confirmation")
        alertDialogBuilder.setMessage("Are you sure you want to delete this card?")

        // Get parent and remove card
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val parentLayout = cardBinding.root.parent as? ViewGroup
            parentLayout?.removeView(cardBinding.root)
            Toast.makeText(this, "Delete completed!", Toast.LENGTH_SHORT).show()
        }

        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        // Show the confirmation
        alertDialogBuilder.show()
    }
}