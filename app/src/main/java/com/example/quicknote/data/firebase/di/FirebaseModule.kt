package com.example.quicknote.data.firebase.di

import com.example.quicknote.data.firebase.repository.FirebaseRepository
import com.example.quicknote.data.firebase.repository.FirebaseRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseModule {
    @Binds
    @Singleton
    abstract fun provideFirebaseRepository(impl: FirebaseRepositoryImpl): FirebaseRepository
}