package com.mattvu.speerdcompanionapp.filemanager.services

import com.mattvu.speerdcompanionapp.filemanager.models.UserChangesDTO
import com.mattvu.speerdcompanionapp.filemanager.models.UserResponse
import com.mattvu.speerdcompanionapp.filemanager.models.UserUpdateDTO
import com.mattvu.speerdcompanionapp.register.models.UserDTO
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserAPIService {
    @GET("details/{userId}")
    fun getUserDetails(@Path("userId") userId: Int): Call<UserChangesDTO>

    @PUT("update/{userId}")
    fun updateUser(@Path("userId") userId: Int, @Body userUpdateDTO: UserUpdateDTO): Call<UserResponse>
}