package com.project.alarmwakeup.presentation

import android.app.Application
import com.project.alarmwakeup.presentation.di.appModule
import com.project.alarmwakeup.presentation.di.dataModule
import com.project.alarmwakeup.presentation.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            androidLogger(Level.DEBUG)
            modules(listOf(
                domainModule,
                appModule,
                dataModule
            ))
        }
    }
}