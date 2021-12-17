package com.coreclouet.getscan.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _infos = MutableLiveData<String>()
    val infos: LiveData<String> = _infos

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private lateinit var sourceUrl: String
    private var firstChapter: Int = 1
    private var lastChapter: Int = 1
    private lateinit var mangaName: String

    private var error: String = ""

    /**
     * Download selected chapters
     */
    fun downloadManga() {
        viewModelScope.launch(Dispatchers.IO) {
            // loop on all chapters
            for (currentChapter in firstChapter..lastChapter) {
                setLoading(true)
                // get URL and source code of it
                val currentUrl = sourceUrl.replace(CHAPTER, currentChapter.toString())
                val sourceCode = getSourceCodeUseCase.invoke(currentUrl)
                if (sourceCode == null) {
                    Log.e("CCL", "URL error $currentChapter")
                    updateError("URL error on chapter : $currentChapter")
                    continue
                }
                // find all images in source code
                val images = findImagesUseCase.invoke(sourceCode)
                // update progress
                updateInfos("Chapter $currentChapter nbImages ${images.size}")
                // download each images
                for (currentImageIndice in images.indices) {
                    downloadImageUseCase.invoke(
                        mangaName,
                        currentChapter,
                        currentImageIndice + 1,
                        images[currentImageIndice]
                    )
                }
            }
            // download finish
            setLoading(false)
            if (error.isEmpty()) {
                updateInfos("Download finish !")
            } else {
                updateInfos(error)
            }

        }
    }

    /**
     * Check input data
     */
    fun checkData(
        sourceUrl: String?,
        firstChapter: Int,
        lastChapter: Int,
        mangaName: String?
    ): Boolean {
        if (sourceUrl.isNullOrEmpty() || !sourceUrl.contains(CHAPTER)
            || firstChapter < 1 || lastChapter < 1 || mangaName.isNullOrEmpty()
        ) return false
        this.sourceUrl = sourceUrl
        this.firstChapter = firstChapter
        this.lastChapter = lastChapter
        this.mangaName = mangaName
        return true
    }

    /**
     * Update loading state
     */
    private fun setLoading(isLoading: Boolean = false) {
        _loading.postValue(isLoading)
    }

    /**
     * Update download progress
     */
    private fun updateInfos(info: String) {
        Log.d("CCL", info)
        val result = _infos.value + "\n$info"
        _infos.postValue(result)
    }

    /**
     * Update error string
     */
    private fun updateError(error: String) {
        this.error = this.error + "\n$error"
    }

}