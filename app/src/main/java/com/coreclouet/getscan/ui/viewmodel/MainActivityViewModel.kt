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
import com.coreclouet.getscan.utils.Website
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val getSourceCodeUseCase: GetSourceCodeUseCase,
    private val findImagesUseCase: FindImagesUseCase,
    private val downloadImageUseCase: DownloadImageUseCase
) : ViewModel() {

    private val _infos: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val infos: LiveData<String> = _infos

    private val _loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val loading: LiveData<Boolean> = _loading

    private lateinit var website: Website
    private lateinit var url: String
    private var firstChapter: Int = 1
    private var lastChapter: Int = 1
    private lateinit var mangaName: String

    private var error: String = ""

    /**
     * Download selected chapters
     */
    fun downloadManga() {
        viewModelScope.launch(Dispatchers.IO) {
            //reset error and infos
            error = ""
            _infos.postValue("")
            // loop on all chapters
            for (currentChapter in firstChapter..lastChapter) {
                setLoading(true)
                // get URL and source code of it
                val currentUrl = url.replace(CHAPTER, currentChapter.toString())
                val sourceCode = getSourceCodeUseCase.invoke(currentUrl)
                if (sourceCode == null) {
                    Log.e("CCL", "URL error $currentChapter")
                    updateError("URL error on chapter : $currentChapter")
                    continue
                }
                // find all images in source code
                val images = findImagesUseCase.invoke(sourceCode, website)
                // update progress
                updateInfos("Chapter $currentChapter nbImages ${images.size}")
                // download each images
                for (currentImageIndice in images.indices) {
                    val downloadResult = downloadImageUseCase.invoke(
                        mangaName,
                        currentChapter,
                        currentImageIndice + 1,
                        images[currentImageIndice]
                    )
                    // manage error if download failed
                    if (!downloadResult) {
                        updateError("DL FAILED Chapter $currentChapter image ${currentImageIndice + 1}")
                    }
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
        website: Website,
        sourceUrl: String?,
        endpoint: String?,
        firstChapter: String?,
        lastChapter: String?,
        mangaName: String?
    ): Boolean {
        if (sourceUrl.isNullOrEmpty() || endpoint.isNullOrEmpty() || !endpoint.contains(CHAPTER)
            || firstChapter.isNullOrEmpty() || lastChapter.isNullOrEmpty() || lastChapter.toInt() < firstChapter.toInt() || mangaName.isNullOrEmpty()
        ) return false
        this.website = website
        this.url = sourceUrl + endpoint
        this.firstChapter = firstChapter.toInt()
        this.lastChapter = lastChapter.toInt()
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
        _infos.postValue(info)
    }

    /**
     * Update error string
     */
    private fun updateError(error: String) {
        this.error = this.error + "\n$error"
    }

}