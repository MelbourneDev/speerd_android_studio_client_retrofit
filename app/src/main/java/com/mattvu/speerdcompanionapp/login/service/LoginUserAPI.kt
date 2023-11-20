package com.mattvu.speerdcompanionapp.login.service

import com.mattvu.speerdcompanionapp.login.models.LoginDTO
import com.mattvu.speerdcompanionapp.login.models.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginAPIService {
    @POST("user")
    fun loginUser(@Body loginDTO: LoginDTO): Call<LoginResponse>
}
