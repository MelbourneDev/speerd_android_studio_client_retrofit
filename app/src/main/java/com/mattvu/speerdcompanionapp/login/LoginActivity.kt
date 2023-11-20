package com.mattvu.speerdcompanionapp.login


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mattvu.speerdcompanionapp.R
import com.mattvu.speerdcompanionapp.filemanager.FileViewActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.mattvu.speerdcompanionapp.login.models.LoginDTO
import com.mattvu.speerdcompanionapp.login.models.LoginResponse
import com.mattvu.speerdcompanionapp.login.service.LoginAPIClient
import com.mattvu.speerdcompanionapp.register.RegistrationActivity


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<Button>(R.id.registerButton)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                Log.d("LoginActivity", "Attempting login with username: $username")
                performLogin(username, password)
            } else {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener{
           val RegistrationIntent = Intent(this@LoginActivity, RegistrationActivity::class.java)
            startActivity(RegistrationIntent)
        }
    }

    private fun performLogin(username: String, password: String) {
        LoginAPIClient.service.loginUser(LoginDTO(username, password)).enqueue(object :
            Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("LoginActivity", "Response received: $response")

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    Log.d("LoginActivity", "Login successful: ${responseBody.message}")

                    saveUserId(responseBody.userId)

                    Toast.makeText(this@LoginActivity, responseBody.message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, FileViewActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.d("LoginActivity", "Login failed: ${response.errorBody()?.string()}")
                    Toast.makeText(applicationContext, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("LoginActivity", "Login error", t)
                Toast.makeText(applicationContext, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserId(userId: Int) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply() // Clear previous data
        sharedPreferences.edit().putInt("userId", userId).apply()
    }

}
