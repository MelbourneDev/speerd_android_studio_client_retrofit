package com.mattvu.speerdcompanionapp.login.service

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class LoginAPIClient {
    companion object {
        private val gson = GsonBuilder().setLenient().create()
        private val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/login/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val service: LoginAPIService = retrofit.create(LoginAPIService::class.java)
    }
}
