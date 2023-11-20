package com.mattvu.speerdcompanionapp.register.service

import com.mattvu.speerdcompanionapp.register.models.RegistrationResponse
import com.mattvu.speerdcompanionapp.register.models.UserDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPIService {
    @POST("user")
    fun registerUser(@Body userDto: UserDTO): Call<RegistrationResponse>

}