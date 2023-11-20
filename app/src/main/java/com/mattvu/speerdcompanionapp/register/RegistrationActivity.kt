package com.mattvu.speerdcompanionapp.register


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.mattvu.speerdcompanionapp.R
import com.mattvu.speerdcompanionapp.filemanager.FileViewActivity
import com.mattvu.speerdcompanionapp.login.LoginActivity
import com.mattvu.speerdcompanionapp.register.models.RegistrationResponse
import com.mattvu.speerdcompanionapp.register.models.UserDTO
import com.mattvu.speerdcompanionapp.register.service.RegisterAPIClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// RegistrationActivity: Manages the user registration process in the app.
class RegistrationActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var registerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        supportActionBar?.hide()

        // Initialize views
        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        emailEditText = findViewById(R.id.emailEditText)
        registerButton = findViewById(R.id.registerButton)
        val slideToActSeekbar: SeekBar = findViewById(R.id.slideToActSeekbar)



        // Set up button click listener
        registerButton.setOnClickListener {

            val registerUser = UserDTO(
                username = usernameEditText.text.toString().trim(),
                password = passwordEditText.text.toString().trim(),
                email = emailEditText.text.toString().trim()
            )

            RegisterAPIClient.service.registerUser(registerUser).enqueue(object :
                Callback<RegistrationResponse> {
                override fun onResponse(call: Call<RegistrationResponse>, response: Response<RegistrationResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("RegistrationActivity", "User registered successfully: ${responseBody?.message}")
                        responseBody?.let {
                            saveUserId(it.userId)
                            Toast.makeText(this@RegistrationActivity, it.message, Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@RegistrationActivity, FileViewActivity::class.java))
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        try {
                            val errorResponse = Gson().fromJson(errorBody, RegistrationResponse::class.java)
                            when (errorResponse.message) {
                                "Username already exists" ->
                                    Toast.makeText(this@RegistrationActivity, "Registration failed: Username already exists", Toast.LENGTH_SHORT).show()
                                "Email already exists" ->
                                    Toast.makeText(this@RegistrationActivity, "Registration failed: Email already exists", Toast.LENGTH_SHORT).show()
                                "Invalid password format" ->
                                    Toast.makeText(this@RegistrationActivity, "Registration failed: Invalid password format", Toast.LENGTH_SHORT).show()
                                else ->
                                    Toast.makeText(this@RegistrationActivity, "Registration failed: Please try again", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JsonSyntaxException) {
                            // Handle JSON parsing exception, if any
                            Toast.makeText(this@RegistrationActivity, "An error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                }

                override fun onFailure(call: Call<RegistrationResponse>, t: Throwable) {
                    Log.e("RegistrationActivity", "API call failed: ${t.message}")
                }
            })

        }

        slideToActSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress == 100) {
                    // Launch the intent when progress is 100
                    val intent = Intent(this@RegistrationActivity, LoginActivity::class.java)
                    startActivity(intent)

                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Reset the SeekBar back to start when the touch is released (unless it reached 100)
                if (seekBar?.progress ?: 0 < 100) {
                    seekBar?.progress = 0
                }
            }
        })
    }

    private fun saveUserId(userId: Int) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        sharedPreferences.edit().clear().apply() // Clear previous data
        sharedPreferences.edit().putInt("userId", userId).apply()
    }
}
