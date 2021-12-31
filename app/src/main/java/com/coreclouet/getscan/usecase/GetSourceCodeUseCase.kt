package com.coreclouet.getscan.usecase

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
            result = urlConnection.inputStream.bufferedReader().readText()
            urlConnection.inputStream.close()
        } catch (e: Exception) {
            errorRepository.insert(
                ErrorEntity(0, mangaName, "URL error on $sourceUrl")
            )
            result = null
        } finally {
            urlConnection.disconnect()
        }
        return result
    }

}