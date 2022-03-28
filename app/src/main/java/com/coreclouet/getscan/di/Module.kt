package com.coreclouet.getscan.di

import androidx.room.Room
import com.coreclouet.getscan.db.AppDatabase
import com.coreclouet.getscan.repository.ErrorRepository
import com.coreclouet.getscan.repository.ErrorRepositoryImpl
import com.coreclouet.getscan.repository.FolderRepository
import com.coreclouet.getscan.repository.FolderRepositoryImpl
import com.coreclouet.getscan.ui.viewmodel.MainActivityViewModel
import com.coreclouet.getscan.usecase.*
import com.coreclouet.getscan.utils.DATABASE_NAME
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // ViewModels
    viewModel {
        MainActivityViewModel(
            getSourceCodeUseCase = get(),
            findImagesUseCase = get(),
            downloadImageUseCase = get(),
            getErrorsUseCase = get(),
            getFoldersUseCase = get(),
            addFolderUseCase = get()
        )
    }

    // Use cases
    factory { GetSourceCodeUseCase(errorRepository = get()) }
    factory { FindImagesUseCase() }
    factory { DownloadImageUseCase(context = androidContext(), errorRepository = get()) }
    factory { GetErrorsUseCase(errorRepository = get()) }
    factory { GetFoldersUseCase(folderRepository = get()) }
    factory { AddFolderUseCase(folderRepository = get()) }

    // Repositories
    factory<ErrorRepository> { ErrorRepositoryImpl(errorDao = get()) }
    factory<FolderRepository> { FolderRepositoryImpl(folderDao = get()) }

    // Database
    single {
        // TODO remove fallbackToDestructiveMigration when this goes to production
        Room.databaseBuilder(androidApplication(), AppDatabase::class.java, DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }
    factory { get<AppDatabase>().errorDao() }
    factory { get<AppDatabase>().folderDao() }
}