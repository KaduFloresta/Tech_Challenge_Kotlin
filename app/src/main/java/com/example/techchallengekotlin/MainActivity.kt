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
import com.example.techchallengekotlin.databinding.EditDialogLayoutBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        supportActionBar?.hide()

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
                                editMode(cardBinding)
                            }

                            cardBinding.iconDelete.setOnClickListener {
                                deleteMode(cardBinding)
                            }

                            groupLayout.addView(cardLayout)

                            if ((index + 1) % usersPerGroup == 0) {
                                currentGroupIndex++
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Error: Group layout no found!", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error: " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun adjustFontSize(textView: TextView, text: String, defaultSizeRes: Int) {
        val maxWidth = textView.width
        val paint = TextPaint(textView.paint)
        val defaultSize = resources.getDimension(defaultSizeRes)
        paint.textSize = defaultSize

        val textWidth = paint.measureText(text)
        if (textWidth > maxWidth) {
            val scaleFactor = maxWidth / textWidth
            val newSize = defaultSize * scaleFactor
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize)
        } else {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, defaultSize)
        }

        textView.text = text
    }

    private fun editMode(cardBinding: CardLayoutBinding) {
        val dialogBinding = EditDialogLayoutBinding.inflate(layoutInflater)

        dialogBinding.editName.setText(cardBinding.textName.text)
        dialogBinding.editEmail.setText(cardBinding.textEmail.text)
        dialogBinding.editAge.setText(cardBinding.textAge.text)
        dialogBinding.editId.text = cardBinding.textId.text

        dialogBinding.editId.isEnabled = false

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setTitle("")

        val dialog = dialogBuilder.create()

        dialogBinding.iconConfirm.setOnClickListener {
            val newName = dialogBinding.editName.text.toString()
            val newEmail = dialogBinding.editEmail.text.toString()
            val newAge = dialogBinding.editAge.text.toString()

            if (newName.isNotEmpty() && newEmail.isNotEmpty() && newAge.isNotEmpty()) {
                cardBinding.textName.text = newName
                cardBinding.textEmail.text = newEmail
                cardBinding.textAge.text = newAge

                // Ajustar o tamanho da fonte dos campos de texto no card, se necessário
                adjustFontSize(cardBinding.textName, newName, R.dimen.default_name_text_size)
                adjustFontSize(cardBinding.textEmail, newEmail, R.dimen.default_email_text_size)

                dialog.dismiss()
            } else {
                Toast.makeText(this, "Complete all the fields!", Toast.LENGTH_SHORT).show()
            }
        }

        dialogBinding.iconCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteMode(cardBinding: CardLayoutBinding) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Confirmation")
        alertDialogBuilder.setMessage("Are you sure you want to delete this card?")

        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            val parentLayout = cardBinding.root.parent as? ViewGroup
            parentLayout?.removeView(cardBinding.root)
        }

        alertDialogBuilder.setNegativeButton("No") { _, _ ->
        }

        alertDialogBuilder.show()
    }
}