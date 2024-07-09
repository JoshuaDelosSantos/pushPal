package au.edu.jcu.pushpal

import android.app.Application
import timber.log.Timber

class PushPalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}