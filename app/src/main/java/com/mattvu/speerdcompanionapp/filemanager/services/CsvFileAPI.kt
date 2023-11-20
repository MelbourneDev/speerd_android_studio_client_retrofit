package com.mattvu.speerdcompanionapp.filemanager.services

import com.mattvu.speerdcompanionapp.filemanager.models.CsvFileModel
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface CsvFileAPIService {
    @GET("csvfiles/user/{userId}")
    fun getFilesByUserId(@Path("userId") userId: String): Call<List<CsvFileModel>>

    @DELETE("csvfiles/{id}")
    fun deleteFile(@Path("id") csvFileModelID: Int): Call<Void>

    @GET("csvfiles/download/{id}")
    fun downloadFile(@Path("id") csvFileModelID: Int): Call<ResponseBody>

    @GET("csvfiles/{id}")
    fun getFileById(@Path("id") csvFileModelID: Int): Call<CsvFileModel>

}