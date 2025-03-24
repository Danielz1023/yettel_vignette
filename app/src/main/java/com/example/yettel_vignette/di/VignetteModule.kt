package com.example.yettel_vignette.di

import com.example.yettel_vignette.interfaces.VignetteApiService
import com.example.yettel_vignette.interfaces.VignetteHandler
import com.example.yettel_vignette.interfaces.VignetteRepository
import com.example.yettel_vignette.services.VignetteHandlerService
import com.example.yettel_vignette.services.VignetteRepositoryService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VignetteModule {

    @Provides
    @Singleton
    fun provideVignetteRepository(apiService: VignetteApiService): VignetteRepository {
        return VignetteRepositoryService(apiService)
    }

    @Provides
    @Singleton
    fun provideVignetteHandler(repository: VignetteRepository): VignetteHandler {
        return VignetteHandlerService(repository)
    }
}
