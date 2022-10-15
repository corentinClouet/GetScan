package com.coreclouet.getscan.ui.activity

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.coreclouet.getscan.R
import com.coreclouet.getscan.databinding.ActivityMainBinding
import com.coreclouet.getscan.db.entity.FolderEntity
import com.coreclouet.getscan.model.Website
import com.coreclouet.getscan.ui.viewmodel.MainActivityViewModel
import com.coreclouet.getscan.utils.DEFAULT_NB_CHAPTER
import com.coreclouet.getscan.utils.EMPTY
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initUiEvents()
        initData()
        initObservers()
    }

    /**
     * Save form in preferences
     */
    override fun onPause() {
        super.onPause()
        savePreferences()
    }

    private fun initUiEvents() {
        // Download scan click
        binding.btnDownloadScan.setOnClickListener {
            downloadManga()
        }
        // Website spinner selected
        binding.spWebsite.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //unused
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                binding.edBaseUrl.editableText.clear()
                binding.edBaseUrl.append(Website.values()[position].url)
            }
        }
        // Show error(s) click
        binding.btShowError.setOnClickListener {
            viewModel.getErrors()
        }
    }

    /**
     * Init infos and loading observers
     */
    private fun initObservers() {
        viewModel.infos.observe(this) { infos ->
            binding.tvInfoDownload.append("\n$infos")
        }

        viewModel.loading.observe(this) { isLoading ->
            manageLoading(isLoading)
        }

        viewModel.nbImages.observe(this) { nbImages ->
            updateProgressBarMax(nbImages)
        }

        viewModel.downloadProgress.observe(this) { progress ->
            updateDownloadProgress(progress)
        }

        viewModel.errors.observe(this) {
            showErrors(it)
        }

        viewModel.folders.observe(this) {
            setFoldersAdapter(it)
        }
    }

    /**
     * Init data with preferences
     */
    private fun initData() {
        binding.tvInfoDownload.movementMethod = ScrollingMovementMethod()
        binding.spWebsite.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, Website.values())

        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        binding.spWebsite.setSelection(sharedPref.getInt(getString(R.string.website_key), 0))
        binding.edBaseUrl.append(sharedPref.getString(getString(R.string.base_url_key), EMPTY))
        binding.edEndpoint.append(sharedPref.getString(getString(R.string.endpoint_key), EMPTY))
        binding.edStartChapter.append(
            sharedPref.getString(
                getString(R.string.first_chapter_key),
                DEFAULT_NB_CHAPTER
            )
        )
        binding.edLastChapter.append(
            sharedPref.getString(
                getString(R.string.last_chapter_key),
                DEFAULT_NB_CHAPTER
            )
        )
        binding.edMangaName.append(sharedPref.getString(getString(R.string.manga_name_key), EMPTY))
    }

    /**
     * Check data and download manga
     */
    private fun downloadManga() {
        if (viewModel.checkData(
                binding.spWebsite.selectedItem as Website,
                binding.edBaseUrl.text.toString(),
                binding.edEndpoint.text.toString(),
                binding.edStartChapter.text.toString(),
                binding.edLastChapter.text.toString(),
                binding.edMangaName.text.toString()
            )
        ) {
            viewModel.downloadManga()
        }
    }

    /**
     * Manage views states while loading
     */
    private fun manageLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.pbDownload.visibility = View.VISIBLE
            binding.tvProgress.visibility = View.VISIBLE
        } else {
            binding.pbDownload.visibility = View.INVISIBLE
            binding.tvProgress.visibility = View.INVISIBLE

        }
        binding.spWebsite.isEnabled = !isLoading
        binding.edEndpoint.isEnabled = !isLoading
        binding.edStartChapter.isEnabled = !isLoading
        binding.edLastChapter.isEnabled = !isLoading
        binding.edMangaName.isEnabled = !isLoading
        binding.btnDownloadScan.isEnabled = !isLoading
        binding.btShowError.isEnabled = !isLoading
    }

    /**
     * Save form in preferences
     */
    private fun savePreferences() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(getString(R.string.website_key), binding.spWebsite.selectedItemPosition)
            if (binding.edBaseUrl.text.toString()
                    .isNotEmpty()
            ) putString(getString(R.string.base_url_key), binding.edBaseUrl.text.toString())
            if (binding.edEndpoint.text.toString()
                    .isNotEmpty()
            ) putString(getString(R.string.endpoint_key), binding.edEndpoint.text.toString())
            if (binding.edStartChapter.text.toString().isNotEmpty()) putString(
                getString(R.string.first_chapter_key),
                binding.edStartChapter.text.toString()
            )
            if (binding.edLastChapter.text.toString().isNotEmpty()) putString(
                getString(R.string.last_chapter_key),
                binding.edLastChapter.text.toString()
            )
            if (binding.edMangaName.text.toString()
                    .isNotEmpty()
            ) putString(getString(R.string.manga_name_key), binding.edMangaName.text.toString())
            apply()
        }
    }

    /**
     * Update progress bar max with the number of images to download
     * Reset progress to 0
     */
    private fun updateProgressBarMax(max: Int) {
        binding.pbDownload.max = max
        binding.pbDownload.progress = 0
        binding.tvProgress.text = getString(R.string.download_progress, 0, max)
    }

    /**
     * Update download progress
     */
    private fun updateDownloadProgress(progress: Int) {
        binding.pbDownload.progress = progress
        binding.tvProgress.text =
            getString(R.string.download_progress, progress, binding.pbDownload.max)
    }

    /**
     * Show errors
     */
    private fun showErrors(error: String) {
        binding.tvInfoDownload.append(error)
    }

    private fun setFoldersAdapter(folders: List<FolderEntity>) {
        val namesAdapter: ArrayAdapter<FolderEntity> = ArrayAdapter<FolderEntity>(
            this,
            android.R.layout.simple_dropdown_item_1line, folders
        )
        binding.edMangaName.setAdapter(namesAdapter)
        binding.edMangaName.setOnItemClickListener { adapterView, view, i, l ->
            val folder: FolderEntity = adapterView.getItemAtPosition(i) as FolderEntity
            binding.edEndpoint.editableText.clear()
            binding.edEndpoint.append(folder.endpoint)
            binding.edStartChapter.editableText.clear()
            binding.edStartChapter.append((folder.lastChapter + 1).toString())
            binding.edLastChapter.editableText.clear()
            binding.edLastChapter.append((folder.lastChapter + 1).toString())
        }
    }

}
