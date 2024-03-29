package com.coreclouet.getscan.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.coreclouet.getscan.db.entity.FolderEntity
import com.coreclouet.getscan.model.Website
import com.coreclouet.getscan.usecase.*
import com.coreclouet.getscan.utils.ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val getSourceCodeUseCase: GetSourceCodeUseCase,
    private val findImagesUseCase: FindImagesUseCase,
    private val downloadImageUseCase: DownloadImageUseCase,
    private val getErrorsUseCase: GetErrorsUseCase,
    private val getFoldersUseCase: GetFoldersUseCase,
    private val addFolderUseCase: AddFolderUseCase,
    private val deleteErrorsUseCase: DeleteErrorsUseCase
) : ViewModel() {

    private val _infos: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val infos: LiveData<String> = _infos

    private val _loading: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val loading: LiveData<Boolean> = _loading

    private val _nbImages: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val nbImages: LiveData<Int> = _nbImages

    private val _downloadProgress: MutableLiveData<Int> by lazy { MutableLiveData<Int>() }
    val downloadProgress: LiveData<Int> = _downloadProgress

    private val _errors: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val errors: LiveData<String> = _errors

    private val _folders: MutableLiveData<List<FolderEntity>> by lazy { MutableLiveData<List<FolderEntity>>() }
    val folders: LiveData<List<FolderEntity>> = _folders

    private lateinit var website: Website
    private lateinit var url: String
    private var firstChapter: Int = 1
    private var lastChapter: Int = 1
    private lateinit var mangaName: String

    init {
        getFolders()
    }

    /**
     * Download selected chapters
     */
    fun downloadManga() {
        viewModelScope.launch(Dispatchers.IO) {
            //reset error and infos
            _infos.postValue("")
            // loop on all chapters
            for (currentChapter in firstChapter..lastChapter) {
                setLoading(true)
                // get URL and source code of it
                val currentUrl = url.replace(ID, currentChapter.toString())
                val sourceCode = getSourceCodeUseCase.invoke(mangaName, currentUrl) ?: continue
                // find all images in source code
                val images = findImagesUseCase.invoke(sourceCode, website)
                // update progress
                updateNbImages(images.size)
                updateInfos("Chapter $currentChapter nbImages ${images.size}")
                // download each images
                for (currentImageIndice in images.indices) {
                    downloadImageUseCase.invoke(
                        mangaName,
                        currentChapter,
                        currentImageIndice + 1,
                        images[currentImageIndice]
                    )
                    // update download progress
                    updateDownloadProgress(currentImageIndice + 1)
                }
            }
            // download finish
            setLoading(false)
            getFolders()
            updateInfos("Download finish !")
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
        if (sourceUrl.isNullOrEmpty() || endpoint.isNullOrEmpty() || !endpoint.contains(ID)
            || firstChapter.isNullOrEmpty() || lastChapter.isNullOrEmpty() || lastChapter.toInt() < firstChapter.toInt() || mangaName.isNullOrEmpty()
        ) return false
        this.website = website
        this.url = sourceUrl + endpoint
        this.firstChapter = firstChapter.toInt()
        this.lastChapter = lastChapter.toInt()
        this.mangaName = mangaName
        // save folder in database
        addFolder(mangaName, endpoint, lastChapter.toInt())
        return true
    }

    private fun addFolder(name: String, endpoint: String, lastChapter: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            addFolderUseCase.invoke(name, endpoint, lastChapter)
        }
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
     * Update nb images to download to update the UI progress bar
     * Reset progress to 0
     */
    private fun updateNbImages(nbImages: Int) {
        _nbImages.postValue(nbImages)
        _downloadProgress.postValue(0)
    }

    /**
     * Update progress bar with current download progress
     */
    private fun updateDownloadProgress(progress: Int) {
        _downloadProgress.postValue(progress)
    }

    /**
     * Get download errors
     */
    fun getErrors() {
        viewModelScope.launch(Dispatchers.IO) {
            _errors.postValue(getErrorsUseCase.invoke())
        }
    }

    private fun getFolders() {
        viewModelScope.launch(Dispatchers.IO) {
            _folders.postValue(getFoldersUseCase.invoke())
        }
    }

    fun deleteErrors() {
        viewModelScope.launch(Dispatchers.IO) {
            deleteErrorsUseCase.invoke()
        }
    }
}