package com.coreclouet.getscan.usecase

import android.app.DownloadManager
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.util.Log
import com.coreclouet.getscan.db.entity.ErrorEntity
import com.coreclouet.getscan.repository.ErrorRepository
import java.io.File

class DownloadImageUseCase(
    private val context: Context,
    private val errorRepository: ErrorRepository
) {

    /**
     * Download image
     */
    suspend fun invoke(
        mangaName: String,
        currentChapter: Int,
        nbImage: Int,
        downloadUrlOfImage: String
    ): Boolean {
        try {
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadUri: Uri = Uri.parse(downloadUrlOfImage)
            val request = DownloadManager.Request(downloadUri)
            val filename = getFileName(currentChapter, nbImage)
            request.setAllowedOverRoaming(false)
                .setTitle(currentChapter.toString() + "_" + nbImage.toString())
                .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                .setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    File.separator + "$mangaName/$currentChapter/$filename.jpg"
                )
            val downloadId = downloadManager.enqueue(request)
            val downloadResult = queryDownload(filename, downloadId, downloadManager)
            // check error
            if (!downloadResult) {
                errorRepository.insert(
                    ErrorEntity(
                        0,
                        mangaName,
                        "DL FAILED on chapter $currentChapter image $nbImage"
                    )
                )
            }
            return downloadResult
        } catch (e: Exception) {
            errorRepository.insert(ErrorEntity(0, mangaName, e.message.toString()))
            return false
        }
    }

    /**
     * Check download state
     */
    private suspend fun queryDownload(
        fileName: String,
        downloadId: Long?,
        downloadManager: DownloadManager?
    ): Boolean {
        if (downloadId == null || downloadManager == null) return false
        // using query method
        var finishDownload = false
        while (!finishDownload) {
            val cursor: Cursor =
                downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
            if (cursor.moveToFirst()) {
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when (status) {
                    DownloadManager.STATUS_FAILED -> {
                        cursor.close()
                        return false
                    }
                    DownloadManager.STATUS_PAUSED -> {}
                    DownloadManager.STATUS_PENDING -> {}
                    DownloadManager.STATUS_RUNNING -> {}
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        Log.d("CCL", "STATUS_SUCCESSFUL $fileName")
                        finishDownload = true
                        cursor.close()
                    }
                }
            }
            cursor.close()
        }
        return true
    }

    /**
     * Manage file name with chapter and image number
     */
    private fun getFileName(currentChapter: Int, nbImage: Int): String {
        var finalNbImage = ""
        finalNbImage = when {
            nbImage < 10 -> {
                "00$nbImage"
            }
            nbImage <= 99 -> {
                "0$nbImage"
            }
            else -> {
                nbImage.toString()
            }
        }
        return currentChapter.toString() + "_" + finalNbImage
    }

}