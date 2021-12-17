package com.coreclouet.getscan.usecase

import java.net.HttpURLConnection
import java.net.URL

class GetSourceCodeUseCase {

    fun invoke(sourceUrl: String): String? {
        val url = URL(sourceUrl)
        val urlConnection = url.openConnection() as HttpURLConnection
        val result: String? = try {
            urlConnection.inputStream.bufferedReader().readText()
        } catch (e: Exception) {
            null
        } finally {
            urlConnection.disconnect()
        }
        return result
    }

}