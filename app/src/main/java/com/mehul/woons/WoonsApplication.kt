package com.mehul.woons

import android.app.Application
import com.mehul.woons.DI.AppComponent
import com.mehul.woons.DI.DaggerAppComponent

class WoonsApplication : Application() {
    lateinit var appComponent: AppComponent
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().application(this).build()
    }
}