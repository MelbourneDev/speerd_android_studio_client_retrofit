package com.mattvu.speerdcompanionapp.filemanager.models

data class UserChangesDTO(
    val username: String,
    val password: String,
    val email: String
)

data class UserResponse(
    val success: Boolean,
    val message: String,
    // other fields as necessary
)

data class UserUpdateDTO(
    var username: String,
    var email: String,
    var password: String
)
