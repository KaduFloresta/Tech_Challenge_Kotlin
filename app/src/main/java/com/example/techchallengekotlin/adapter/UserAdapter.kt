//package com.example.techchallengekotlin.adapter
//
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import androidx.recyclerview.widget.RecyclerView
//import com.example.techchallengekotlin.data.UserData
//import com.example.techchallengekotlin.databinding.CardLayoutBinding
//import java.lang.Math.min
//
//class UserAdapter(private var users: List<UserData>) :
//    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
//
//    fun updateData(newData: List<UserData>) {
//        val oldSize = users.size
//        users = newData
//        val newSize = newData.size
//        notifyItemRangeChanged(0, min(oldSize, newSize))
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
//        val binding = CardLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return UserViewHolder(binding)
//    }
//
//    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
//        val currentUser = users[position]
//        val binding = holder.binding
//
//        binding.textName.text = currentUser.name
//        binding.textEmail.text = currentUser.email
//        binding.textAge.text = currentUser.age.toString()
//        binding.textId.text = currentUser.id.toString()
//    }
//
//    override fun getItemCount() = users.size
//
//    class UserViewHolder(val binding: CardLayoutBinding) : RecyclerView.ViewHolder(binding.root)
//}
