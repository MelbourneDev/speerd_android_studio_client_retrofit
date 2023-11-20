package com.mattvu.speerdcompanionapp.filemanager


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.mattvu.speerdcompanionapp.R
import com.mattvu.speerdcompanionapp.filemanager.models.UserChangesDTO
import com.mattvu.speerdcompanionapp.filemanager.models.UserUpdateDTO


class UserAdapter(
    private val context: Context,
    initialUsers: List<UserChangesDTO>
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {


    private val userId = getUserIdFromSharedPrefs()

    private fun getUserIdFromSharedPrefs(): Int {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.getInt("userId", -1) // Default to -1 if not found
    }

    private var userUpdates = initialUsers.map {
        // Using the same userId for all instances
        UserUpdateDTO(it.username, it.email, it.password)
    }.toMutableList()

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val editTextUsername: EditText = view.findViewById(R.id.editTextUserName)

        val editTextEmail: EditText = view.findViewById(R.id.editTextUserEmail)

        val editTextPassword: EditText = view.findViewById(R.id.editTextPassword)


    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_details_recyclerview, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userUpdate = userUpdates[position]

        holder.editTextUsername.setText(userUpdate.username)
        holder.editTextEmail.setText(userUpdate.email)
        // Password is blank for security reasons

        holder.editTextUsername.doAfterTextChanged { text ->
            userUpdate.username = text.toString()
        }
        holder.editTextEmail.doAfterTextChanged { text ->
            userUpdate.email = text.toString()
        }
        holder.editTextPassword.doAfterTextChanged { text ->
            userUpdate.password = text.toString()
        }
    }

    override fun getItemCount(): Int {
        return userUpdates.size
    }
    fun getUpdatedUsers(): List<UserUpdateDTO> {
        return userUpdates
    }
    fun updateUsers(newUsers: List<UserUpdateDTO>) {
        userUpdates.clear()
        userUpdates.addAll(newUsers)
        notifyDataSetChanged()
    }
}




