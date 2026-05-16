package id.my.rizalanggoro.arta.core.application

import android.app.Application
import id.my.rizalanggoro.arta.core.di.AppContainer

class MyApplication : Application() {
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()

        appContainer = AppContainer()
    }
}