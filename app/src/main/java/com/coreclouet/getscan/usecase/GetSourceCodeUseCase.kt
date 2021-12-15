package com.coreclouet.getscan.usecase

import java.net.HttpURLConnection
import java.net.URL

class GetSourceCodeUseCase {

    suspend fun invoke(sourceUrl: String): String {
        val url = URL(sourceUrl)
        val urlConnection = url.openConnection() as HttpURLConnection
        try {
            return urlConnection.inputStream.bufferedReader().readText()
        } finally {
            urlConnection.disconnect()
        }
    }

}