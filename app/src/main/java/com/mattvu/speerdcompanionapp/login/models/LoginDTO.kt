package com.mattvu.speerdcompanionapp.login.models

data class LoginDTO(
    val username: String,
    val password: String
)

data class LoginResponse(
    val message: String,
    val userId:Int
)
