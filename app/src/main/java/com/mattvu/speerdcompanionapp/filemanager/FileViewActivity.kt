package com.mattvu.speerdcompanionapp.filemanager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mattvu.speerdcompanionapp.R
import com.mattvu.speerdcompanionapp.filemanager.models.CsvFileModel
import com.mattvu.speerdcompanionapp.filemanager.models.UserChangesDTO
import com.mattvu.speerdcompanionapp.filemanager.models.UserResponse
import com.mattvu.speerdcompanionapp.filemanager.models.UserUpdateDTO
import com.mattvu.speerdcompanionapp.filemanager.services.CSVAPIClient
import com.mattvu.speerdcompanionapp.filemanager.services.UserAPIClient
import com.mattvu.speerdcompanionapp.login.LoginActivity
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.regex.Pattern

class FileViewActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CsvFileAdapter
    private lateinit var recyclerViewUsers: RecyclerView
    private lateinit var userAdapter: UserAdapter
    // Declare userId as a class-level property
    private var userId: Int = -1
    companion object {
        private const val REQUEST_CODE_PICK_CSV = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_manager)
        supportActionBar?.hide()

        // Initialize RecyclerView for CSV files
        recyclerView = findViewById(R.id.recyclerViewCsvFiles)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CsvFileAdapter(
            mutableListOf(),
            onDeleteClick = { csvFileModelID -> deleteFile(csvFileModelID) },
            onDownloadClick = { csvFileModelID, fileName -> downloadCsvFile(csvFileModelID, fileName) }
        )
        recyclerView.adapter = adapter

        // Initialize RecyclerView for Users
        recyclerViewUsers = findViewById(R.id.recyclerViewUserDetails)
        recyclerViewUsers.layoutManager = LinearLayoutManager(this)
        userAdapter = UserAdapter(this, mutableListOf()) // Initialize with an empty list of UserUpdateDTO
        recyclerViewUsers.adapter = userAdapter

        // Setup other UI elements
        setupSeekBar()
        setupButtons()

        // Fetch data
        fetchData()
        fetchUserData()
        userId = getUserIdFromSharedPrefs()// Implement this function to get the user ID from shared preferences




    }

    private fun setupButtons() {
        val logoutButton = findViewById<Button>(R.id.logout)
        val buttonUploadCsv = findViewById<Button>(R.id.buttonUploadCsv)
        buttonUploadCsv.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "text/csv"
            startActivityForResult(intent, REQUEST_CODE_PICK_CSV)}
            logoutButton.setOnClickListener{
            val Logoutintent = Intent(this@FileViewActivity, LoginActivity::class.java)
            startActivity(Logoutintent)}

    }



    private fun fetchUserData() {
        val userId = getUserIdFromSharedPrefs()
        if (userId != -1) {
            UserAPIClient.service.getUserDetails(userId).enqueue(object : Callback<UserChangesDTO> {
                override fun onResponse(call: Call<UserChangesDTO>, response: Response<UserChangesDTO>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        user?.let {
                            updateUsers(listOf(it)) // Assuming you receive a single UserChangesDTO
                        }
                    } else {
                        // Handle errors
                    }
                }

                override fun onFailure(call: Call<UserChangesDTO>, t: Throwable) {
                    // Handle network error
                }
            })
        } else {
            // Handle invalid userId
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_CSV && resultCode == RESULT_OK) {
            val fileUri = data?.data
            // Handle the file URI (e.g., upload to server)
        }
    }

    private fun fetchData() {
        val userId = getUserIdFromSharedPrefs()
        CSVAPIClient.service.getFilesByUserId(userId.toString())
            .enqueue(object : Callback<List<CsvFileModel>> {
                override fun onResponse(
                    call: Call<List<CsvFileModel>>,
                    response: Response<List<CsvFileModel>>
                ) {
                    if (response.isSuccessful) {
                        val files = response.body()?.toMutableList() ?: mutableListOf()
                        adapter.updateData(files)
                    } else {
                        Log.e(
                            "FileViewActivity",
                            "Error fetching files: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: Call<List<CsvFileModel>>, t: Throwable) {
                    Log.e("FileViewActivity", "Network error: ${t.message}")
                }
            })
    }



    private fun updateUsers(users: List<UserChangesDTO>) {
        val userUpdates = users.map {
            UserUpdateDTO(it.username, it.email, it.password)
        }
        userAdapter.updateUsers(userUpdates)
    }



    private fun getUserIdFromSharedPrefs(): Int {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPreferences.getInt("userId", -1) // Default to -1 if not found
        Log.d("FileViewActivity", "Retrieved userId from SharedPreferences: $userId")
        return userId
    }

    private fun deleteFile(csvFileModelID: Int) {
        CSVAPIClient.service.deleteFile(csvFileModelID).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Remove the item from the adapter's data set
                    adapter.removeItemById(csvFileModelID)
                } else {
                    // Handle unsuccessful response, maybe log the error or show a toast
                    Log.e(
                        "FileViewActivity",
                        "Error deleting file: ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle network error
                Log.e("FileViewActivity", "Network error: ${t.message}")
            }
        })
    }

    fun downloadCsvFile(csvFileModelID: Int, fileName: String) {
        Log.d("FileViewActivity", "Downloading file: $fileName")

        CSVAPIClient.service.downloadFile(csvFileModelID).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("FileViewActivity", "File download successful")

                    // Save the downloaded file to storage
                    val body = response.body()
                    if (body != null) {
                        saveFileToStorage(body.byteStream(), fileName)
                    }
                } else {
                    // Handle unsuccessful response, maybe log the error or show a toast
                    Log.e(
                        "FileViewActivity",
                        "Error downloading file: ${response.errorBody()?.string()}"
                    )
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // Handle network error
                Log.e("FileViewActivity", "Network error: ${t.message}")
            }



            private fun saveFileToStorage(inputStream: InputStream, fileName: String) {
                val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val file = File(directory, fileName)
                try {
                    val outputStream = FileOutputStream(file)
                    val buffer = ByteArray(1024)
                    var len: Int
                    while (inputStream.read(buffer).also { len = it } != -1) {
                        outputStream.write(buffer, 0, len)
                    }
                    outputStream.flush()
                    outputStream.close()
                    inputStream.close()

                    // Displaying the toast message
                    runOnUiThread {
                        Toast.makeText(this@FileViewActivity, "Download complete: $fileName", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
        }
    }





            private fun validateInputs(): Boolean {
                val editTextUserName = findViewById<EditText>(R.id.editTextUserName)
                val editTextUserEmail = findViewById<EditText>(R.id.editTextUserEmail)
                val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
                val editTextPasswordValidation = findViewById<EditText>(R.id.editTextPasswordValidation)

                // Username validation (max 50 characters)
                if (editTextUserName.text.length > 50) {
                    editTextUserName.error = "Username too long"
                    return false
                }

                // Email validation
                if (!Patterns.EMAIL_ADDRESS.matcher(editTextUserEmail.text).matches()) {
                    editTextUserEmail.error = "Invalid email address"
                    return false
                }

                // Password validation (regex pattern)
                val passwordPattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,}$")
                if (!passwordPattern.matcher(editTextPassword.text).matches()) {
                    editTextPassword.error = "Password does not meet criteria"
                    return false
                }

                // Password match validation
                if (editTextPassword.text.toString() != editTextPasswordValidation.text.toString()) {
                    editTextPasswordValidation.error = "Passwords do not match"
                    return false
                }

                return true
            }




        })
    }
    private fun setupSeekBar() {
        val slideToActSeekbar: SeekBar = findViewById(R.id.slideToConfirm)
        slideToActSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Optional: Implement any action on progress change
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Optional: Implement any action on start of sliding
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                seekBar?.let {
                    if (it.progress == 100) {
                        submitUserUpdates()
                        it.progress = 0 // Reset SeekBar progress
                    }
                }
            }
        })
    }

    private fun submitUserUpdates() {
        val updatedUsers = userAdapter.getUpdatedUsers()
        if (updatedUsers.isEmpty()) {
            Toast.makeText(this, "No user updates to submit.", Toast.LENGTH_SHORT).show()
            return
        }

        updatedUsers.forEach { userUpdate ->
            Log.d("FileViewActivity", "Updating user with userId: $userId")
            UserAPIClient.service.updateUser(userId, userUpdate).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d("FileViewActivity", "User update successful: ${responseBody?.message}")
                        Toast.makeText(this@FileViewActivity, "Update successful: ${responseBody?.message}", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("FileViewActivity", "Error updating user: $errorBody")
                        Toast.makeText(this@FileViewActivity, "Error updating user: $errorBody", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Log.e("FileViewActivity", "Network error on updating user: ${t.message}")
                    Toast.makeText(this@FileViewActivity, "Network error on updating user: ${userUpdate.username}", Toast.LENGTH_SHORT).show()

                }
            })
        }
    }


}
