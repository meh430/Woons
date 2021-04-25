package com.mehul.woons

import android.app.Application
import com.mehul.woons.DI.AppComponent
import com.mehul.woons.DI.DaggerAppComponent
import timber.log.Timber

class WoonsApplication : Application() {
    lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().application(this).build()
        if (Constants.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}