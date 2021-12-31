package com.coreclouet.getscan.usecase

import java.net.HttpURLConnection
import java.net.URL

class GetSourceCodeUseCase {

    fun invoke(sourceUrl: String): String? {
        val url = URL(sourceUrl)
        val urlConnection = url.openConnection() as HttpURLConnection
        var result: String?
        try {
            result = urlConnection.inputStream.bufferedReader().readText()
            urlConnection.inputStream.close()
        } catch (e: Exception) {
            result = null
        } finally {
            urlConnection.disconnect()
        }
        return result
    }

}