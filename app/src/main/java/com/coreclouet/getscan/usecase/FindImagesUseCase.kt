package com.coreclouet.getscan.usecase

import com.coreclouet.getscan.utils.DELIMITER
import com.coreclouet.getscan.utils.IMG_DATA_SRC_REGEX

class FindImagesUseCase {

    suspend fun invoke(sourceCode: String): List<String> {
        val regex = Regex(IMG_DATA_SRC_REGEX)
        val matches = regex.findAll(sourceCode)
        val images = matches.map { it.groupValues[0] }.joinToString(separator = DELIMITER)
        return images.split(DELIMITER).map { replaceUnusedCharacters(it) }
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