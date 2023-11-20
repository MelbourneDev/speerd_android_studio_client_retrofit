package com.mattvu.speerdcompanionapp.register.models

data class UserDTO(
    val username: String,
    val password: String,
    val email: String

)


data class RegistrationResponse(
    val message: String,
    val userId: Int
)
