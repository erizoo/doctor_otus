package ru.soft.core_api.network

import okhttp3.*
import retrofit2.*

interface NetworkProvider {

    fun provideOkHttpClient(): OkHttpClient

    fun provideRetrofit(): Retrofit

}