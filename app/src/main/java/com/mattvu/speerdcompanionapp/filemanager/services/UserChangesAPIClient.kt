package com.mattvu.speerdcompanionapp.filemanager.services

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class UserAPIClient {
    companion object {
        private val gson = GsonBuilder().setLenient().create()
        private val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/register/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val service: UserAPIService = retrofit.create(UserAPIService::class.java)
    }
}