package com.example.techchallengekotlin.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.techchallengekotlin.R
import com.example.techchallengekotlin.data.UserData
import com.example.techchallengekotlin.databinding.ActivityMainBinding
import com.example.techchallengekotlin.databinding.CardLayoutBinding
import com.example.techchallengekotlin.databinding.EditCardLayoutBinding
import com.example.techchallengekotlin.viewModel.UserViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set custom toolbar
        setSupportActionBar(binding.customToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Start ViewModel
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        // Observe the users data changes and update UI
        userViewModel.userLiveData.observe(this) { userDataList ->
            fillCardViews(userDataList)
        }

        // Starts searching for user data
        userViewModel.searchUserData()
    }

    // List of card group IDs
    private fun fillCardViews(userDataList: List<UserData>) {
        val groupIds = listOf(
            binding.cardContainer1.id,
            binding.cardContainer2.id,
            binding.cardContainer3.id,
            binding.cardContainer4.id,
            binding.cardContainer5.id
        )

        // Calculate the number of users per group
        val usersGroup = userDataList.size / groupIds.size
        var currentPosition = 0

        for ((position, userData) in userDataList.withIndex()) {
            if (currentPosition < groupIds.size) {
                val groupId = groupIds[currentPosition]
                val groupLayout = binding.root.findViewById<LinearLayout>(groupId)

                // Fill card views with user data
                val cardLayout = layoutInflater.inflate(R.layout.card_layout, groupLayout, false)
                val cardBinding = CardLayoutBinding.bind(cardLayout)
                cardBinding.textName.text = userData.name
                cardBinding.textEmail.text = userData.email
                cardBinding.textAge.text = userData.age.toString()
                cardBinding.textId.text = userData.id.toString()

                // Update and delete listeners
                cardBinding.iconUpdate.setOnClickListener {
                    updateMode(cardBinding)
                }

                cardBinding.iconDelete.setOnClickListener {
                    deleteMode(cardBinding)
                }

                // Add the card to the group
                groupLayout.addView(cardLayout)

                // Calculate the position of the next group
                if ((position + 1) % usersGroup == 0) {
                    currentPosition++
                }
            }
        }
    }

    private fun updateMode(cardBinding: CardLayoutBinding) {
        val dialogBinding = EditCardLayoutBinding.inflate(layoutInflater) // Edit mode dialog

        // Put the current data on the fields
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

                Toast.makeText(this, "Update completed!", Toast.LENGTH_SHORT).show()

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