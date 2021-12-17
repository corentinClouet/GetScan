package com.coreclouet.getscan.ui.activity

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
        initObservers()
    }

    private fun initObservers() {
        viewModel.infos.observe(this, { infos ->
            binding.tvInfoDownload.text = infos
        })

        viewModel.loading.observe(this, { isLoading ->
            manageLoading(isLoading)
        })
    }

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

}
