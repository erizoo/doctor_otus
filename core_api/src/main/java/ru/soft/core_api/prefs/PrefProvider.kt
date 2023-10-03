package ru.soft.core_api.prefs

import android.content.*

interface PrefProvider {

    fun provideSharedPreferences(): SharedPreferences

    fun providePreferencesEditor(): SharedPreferences.Editor

}