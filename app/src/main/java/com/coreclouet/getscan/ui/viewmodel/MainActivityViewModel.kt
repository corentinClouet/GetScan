package com.coreclouet.getscan.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coreclouet.getscan.usecase.DownloadImageUseCase
import com.coreclouet.getscan.usecase.FindImagesUseCase
import com.coreclouet.getscan.usecase.GetSourceCodeUseCase
import com.coreclouet.getscan.utils.CHAPTER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val getSourceCodeUseCase: GetSourceCodeUseCase,
    private val findImagesUseCase: FindImagesUseCase,
    private val downloadImageUseCase: DownloadImageUseCase
) : ViewModel() {

    private lateinit var sourceUrl: String
    private var firstChapter: Int = 1
    private var lastChapter: Int = 1
    private lateinit var mangaName: String

    fun downloadManga() {
        viewModelScope.launch(Dispatchers.IO) {
            for (currentChapter in firstChapter..lastChapter) {
                val currentUrl = sourceUrl.replace(CHAPTER, currentChapter.toString())
                val sourceCode = getSourceCodeUseCase.invoke(currentUrl)
                val images = findImagesUseCase.invoke(sourceCode)
                Log.d("CCL", "Chapter $currentChapter nbImages ${images.size}")
                for (currentImageIndice in images.indices) {
                    downloadImageUseCase.invoke(mangaName, currentChapter, currentImageIndice + 1, images[currentImageIndice])
                }
            }
            Log.d("CCL", "Download finish !")
        }
    }

    fun checkData(sourceUrl: String?, firstChapter: Int, lastChapter: Int, mangaName: String?): Boolean {
        if (sourceUrl.isNullOrEmpty() || !sourceUrl.contains(CHAPTER)
            || firstChapter < 1 || lastChapter < 1 || mangaName.isNullOrEmpty()) return false
        this.sourceUrl = sourceUrl
        this.firstChapter = firstChapter
        this.lastChapter = lastChapter
        this.mangaName = mangaName
        return true
    }

}