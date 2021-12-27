package com.coreclouet.getscan

import android.app.Application
import android.os.StrictMode
import com.coreclouet.getscan.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin {
            androidContext(this@MyApplication)
            modules(appModule)
        }
        // Strict mode
        if (BuildConfig.DEBUG)
            StrictMode.enableDefaults()
    }
}