package com.coreclouet.getscan.usecase

import com.coreclouet.getscan.model.Website
import com.coreclouet.getscan.utils.*

class FindImagesUseCase {

    /**
     * Get list of images url
     */
    suspend fun invoke(sourceCode: String, website: Website): List<String> {
        return if (website == Website.SUSHI_SCAN) {
            getImagesForSushiScan(sourceCode)
        } else {
            getImagesByDefault(sourceCode)
        }
    }

    /**
     * Search images in source code with data-src regex
     */
    private suspend fun getImagesByDefault(sourceCode: String): List<String> {
        val regex = Regex(DATA_SRC_REGEX)
        val matches = regex.findAll(sourceCode)
        val images = matches.map { it.groupValues[0] }.joinToString(separator = DELIMITER)
        return images.split(DELIMITER).map { replaceUnusedCharacters(it) }
    }

    /**
     * Search images in source code with reader regex (specific to sushi_scan)
     */
    private suspend fun getImagesForSushiScan(sourceCode: String): List<String> {
        val readerRegex = Regex(READER_REGEX)
        val readerMatches = readerRegex.findAll(sourceCode)
        val readerMatch =
            readerMatches.map { it.groupValues[0] }.joinToString(separator = DELIMITER)
        val imgRegex = Regex(HTTP_IMG_REGEX)
        val imgMatches = imgRegex.findAll(readerMatch)
        val images = imgMatches.map { it.groupValues[0] }.joinToString(separator = DELIMITER)
        return images.split(DELIMITER)
    }

    /**
     * Replace "data-src" and unused "'" to get only img URL
     */
    private suspend fun replaceUnusedCharacters(rawUrlImage: String): String {
        var result = rawUrlImage.replace("'", "").replace("data-src=", "").trim()
        if (!result.contains("https")) {
            result = result.replace("http", "https")
        }
        if (result.startsWith("/", 1, false)) {
            result = "https:$result"
        }
        return result
    }

}