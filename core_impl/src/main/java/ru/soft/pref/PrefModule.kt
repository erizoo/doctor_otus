package ru.soft.pref

import android.content.*
import dagger.*
import javax.inject.*

@Module
class PrefModule(private val context: Context) {

    @Provides
    @Singleton
    fun provideSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun providePreferencesEditor(prefs: SharedPreferences): SharedPreferences.Editor {
        return prefs.edit()
    }
}