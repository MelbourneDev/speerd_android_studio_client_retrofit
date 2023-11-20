package com.mattvu.speerdcompanionapp.filemanager.models

data class CsvFileModel(
    val csvFileModelID: Int,
    val fileName: String
//    val fileData: String, // Assuming the Blob is sent as a Base64 encoded string
//    val userId: String
)
