package com.example.quicknote.data.notesOrderDB

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.quicknote.data.firebase.repository.FirebaseRepository
import com.example.quicknote.data.firebase.repository.FirebaseRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabaseRepository(dao: Dao, firebaseRepository: FirebaseRepository): DatabaseRepository {
        return DatabaseRepository(dao, firebaseRepository)
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application): NotesOrderDatabase {
        return NotesOrderDatabase.getDatabase(app)
    }

    @Singleton
    @Provides
    fun provideDao(database: NotesOrderDatabase): Dao {
        return database.dao()
    }

    @Singleton
    @Provides
    fun provideSyncLocalDataUseCase(
        databaseRepository: DatabaseRepository
    ): SyncLocalDataUseCase {
        return SyncLocalDataUseCase(databaseRepository, firebaseRepository = FirebaseRepositoryImpl())
    }

    @Singleton
    @Provides
    fun provideUpdateOrderUseCase(
        databaseRepository: DatabaseRepository
    ): UpdateOrderUseCase {
        return UpdateOrderUseCase(databaseRepository)
    }
}