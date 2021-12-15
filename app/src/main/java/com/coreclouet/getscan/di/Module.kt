package com.coreclouet.getscan.di

import com.coreclouet.getscan.ui.viewmodel.MainActivityViewModel
import com.coreclouet.getscan.usecase.DownloadImageUseCase
import com.coreclouet.getscan.usecase.FindImagesUseCase
import com.coreclouet.getscan.usecase.GetSourceCodeUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // single instance of HelloRepository
    //single<HelloRepository> { HelloRepositoryImpl() }

    // Simple Presenter Factory
    factory { GetSourceCodeUseCase() }
    factory { FindImagesUseCase() }
    factory { DownloadImageUseCase(context = androidContext()) }

    viewModel { MainActivityViewModel(getSourceCodeUseCase = get(), findImagesUseCase = get(), downloadImageUseCase = get()) }
}