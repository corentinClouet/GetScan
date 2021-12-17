package com.coreclouet.getscan.ui.activity

import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.coreclouet.getscan.R
import com.coreclouet.getscan.databinding.ActivityMainBinding
import com.coreclouet.getscan.ui.viewmodel.MainActivityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.tvInfoDownload.movementMethod = ScrollingMovementMethod()
        binding.btnDownloadScan.setOnClickListener {
            downloadManga()
        }
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

    /**
     * Init infos and loading observers
     */
    private fun initObservers() {
        viewModel.infos.observe(this, { infos ->
            binding.tvInfoDownload.text = infos
        })

        viewModel.loading.observe(this, { isLoading ->
            manageLoading(isLoading)
        })
    }

    /**
     * Init data with preferences
     */
    private fun initData() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        binding.edBaseUrl.append(sharedPref.getString(getString(R.string.url_key), ""))
        binding.edStartChapter.append(
            sharedPref.getInt(
                getString(R.string.first_chapter_key),
                1
            ).toString()
        )
        binding.edLastChapter.append(
            sharedPref.getInt(
                getString(R.string.last_chapter_key),
                1
            ).toString()
        )
        binding.edMangaName.append(sharedPref.getString(getString(R.string.manga_name_key), ""))
    }

    /**
     * Check data and download manga
     */
    private fun downloadManga() {
        if (viewModel.checkData(
                binding.edBaseUrl.text.toString(),
                binding.edStartChapter.text.toString().toInt(),
                binding.edLastChapter.text.toString().toInt(),
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
            binding.tvInfoDownload.visibility = View.VISIBLE
        } else {
            binding.pbDownload.visibility = View.INVISIBLE
        }
        binding.edBaseUrl.isEnabled = !isLoading
        binding.edStartChapter.isEnabled = !isLoading
        binding.edLastChapter.isEnabled = !isLoading
        binding.edMangaName.isEnabled = !isLoading
        binding.btnDownloadScan.isEnabled = !isLoading
    }

    /**
     * Save form in preferences
     */
    private fun savePreferences() {
        val sharedPref = getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            if (binding.edBaseUrl.text.toString()
                    .isNotEmpty()
            ) putString(getString(R.string.url_key), binding.edBaseUrl.text.toString())
            if (binding.edStartChapter.text.toString().isNotEmpty()) putInt(
                getString(R.string.first_chapter_key),
                binding.edStartChapter.text.toString().toInt()
            )
            if (binding.edLastChapter.text.toString().isNotEmpty()) putInt(
                getString(R.string.last_chapter_key),
                binding.edLastChapter.text.toString().toInt()
            )
            if (binding.edMangaName.text.toString()
                    .isNotEmpty()
            ) putString(getString(R.string.manga_name_key), binding.edMangaName.text.toString())
            apply()
        }
    }

}
