package com.coreclouet.getscan.usecase

import android.util.Log
import com.coreclouet.getscan.db.entity.ErrorEntity
import com.coreclouet.getscan.repository.ErrorRepository
import java.net.HttpURLConnection
import java.net.URL

class GetSourceCodeUseCase(private val errorRepository: ErrorRepository) {

    suspend fun invoke(mangaName: String, sourceUrl: String): String? {
        val url = URL(sourceUrl)
        val urlConnection = url.openConnection() as HttpURLConnection
        var result: String?
        try {
            Log.d("CCL", "Code : ${urlConnection.responseCode}")
            result = urlConnection.inputStream.bufferedReader().readText()
            Log.d("CCL",  "Result : $result")
            urlConnection.inputStream.close()
        } catch (e: Exception) {
            Log.e("CCL", e.message ?: "Pas de message d'erreur")
            errorRepository.insert(
                ErrorEntity(0, mangaName, "URL error on $sourceUrl : ${e.message}")
            )
            result = null
        } finally {
            urlConnection.disconnect()
        }
        return result
    }

}