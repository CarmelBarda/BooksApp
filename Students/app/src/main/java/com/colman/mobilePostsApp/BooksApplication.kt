package com.colman.mobilePostsApp

import android.app.Application
import android.content.Context

class BooksApplication : Application() {

    object Globals {
        var appContext: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        Globals.appContext = applicationContext
    }
}