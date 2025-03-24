package com.example.yettel_vignette.di

import android.os.Build
import com.example.yettel_vignette.interfaces.VignetteApiService
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VignetteApiModule {

    private val BASE_URL = if (isEmulator()) {
        "http://10.0.2.2:8080/" // Emulator
    } else {
        "http://192.168.1.77:8080/" // Replace with actual local server IP
    }

    @Provides
    @Singleton
    fun provideObjectMapper(): ObjectMapper {
        return jacksonObjectMapper().registerModule(kotlinModule())
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create(provideObjectMapper()))
            .build()
    }

    @Provides
    @Singleton
    fun provideVignetteApiService(retrofit: Retrofit): VignetteApiService {
        return retrofit.create(VignetteApiService::class.java)
    }

    private fun isEmulator(): Boolean {
        return Build.FINGERPRINT.contains("generic") ||
                Build.MODEL.contains("Emulator") ||
                Build.MANUFACTURER.contains("Genymotion") ||
                Build.BRAND.startsWith("generic") ||
                Build.DEVICE.startsWith("generic")
    }
}