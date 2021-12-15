package com.coreclouet.getscan.usecase

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File

class DownloadImageUseCase(private val context: Context) {

    suspend fun invoke(
        mangaName: String,
        currentChapter: Int,
        nbImage: Int,
        downloadUrlOfImage: String
    ) {
        try {
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
            val downloadUri: Uri = Uri.parse(downloadUrlOfImage)
            val request = DownloadManager.Request(downloadUri)
            val filename = getFileName(currentChapter, nbImage)

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
                .setAllowedOverRoaming(false)
                .setTitle(currentChapter.toString() + "_" + nbImage.toString())
                .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    File.separator + "$mangaName/$currentChapter/$filename.jpg"
                )
            val downloadId = downloadManager?.enqueue(request)
            queryDownload(filename, downloadId, downloadManager)
        } catch (e: Exception) {
            Log.e("CCL DownloadImageUseCase", e.message.toString())
        }
    }

    private suspend fun queryDownload(
        fileName: String,
        downloadId: Long?,
        downloadManager: DownloadManager?
    ) {
        if (downloadId == null || downloadManager == null) return
        // using query method
        var finishDownload = false
        while (!finishDownload) {
            val cursor: Cursor =
                downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when (status) {
                    DownloadManager.STATUS_FAILED -> {
                        finishDownload = true
                        Log.e("CCL", "STATUS_FAILED $fileName")
                    }
                    DownloadManager.STATUS_PAUSED -> {}
                    DownloadManager.STATUS_PENDING -> {}
                    DownloadManager.STATUS_RUNNING -> {}
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        finishDownload = true
                        //Log.v("CCL", "STATUS_SUCCESSFUL $fileName")
                    }
                }
            }
        }
    }

    private fun getFileName(currentChapter: Int, nbImage: Int): String {
        var finalNbImage = ""
        if (nbImage.toString().length == 1) {
            finalNbImage = "00$nbImage"
        } else if (nbImage.toString().length < 100) {
            finalNbImage = "0$nbImage"
        }
        return currentChapter.toString() + "_" + finalNbImage
    }

}