package com.mattvu.speerdcompanionapp.filemanager

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mattvu.speerdcompanionapp.R
import com.mattvu.speerdcompanionapp.filemanager.models.CsvFileModel
import java.io.File


class CsvFileAdapter(private val fileList: MutableList<CsvFileModel>,
                     private val onDeleteClick: (Int) -> Unit,
                     private val onDownloadClick: (Int, String) -> Unit) : RecyclerView.Adapter<CsvFileAdapter.ViewHolder>() {




    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewFileName: TextView = view.findViewById(R.id.textViewFileName)
        val imageViewDownload: ImageView = view.findViewById(R.id.imageViewDownload)
        val imageViewDelete: ImageView = view.findViewById(R.id.imageViewDelete)


        fun bind(fileModel: CsvFileModel) {
            textViewFileName.text = fileModel.fileName


        }
    }

    fun removeItemById(csvFileModelID: Int) {
        val index = fileList.indexOfFirst { it.csvFileModelID == csvFileModelID }
        if (index != -1) {
            fileList.removeAt(index)
            notifyItemRemoved(index)
        }


    }
    fun updateData(newFileList: MutableList<CsvFileModel>) {
        fileList.clear()
        fileList.addAll(newFileList)
        notifyDataSetChanged()
    }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.csv_file_recyclerview, parent, false)
            return ViewHolder(view)
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val csvFile = fileList[position]
        holder.textViewFileName.text = csvFile.fileName
        holder.imageViewDelete.setOnClickListener {
            onDeleteClick(csvFile.csvFileModelID)
        }
        holder.imageViewDownload.setOnClickListener {
            onDownloadClick(csvFile.csvFileModelID, csvFile.fileName ?: "download.csv")
        }


    }



    override fun getItemCount() = fileList.size


    }
