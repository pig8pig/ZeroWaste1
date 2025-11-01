package com.example.zerowaste.data.di

import android.content.Context
import androidx.room.Room
import com.example.zerowaste.data.db.AppDatabase
import com.example.zerowaste.data.db.GroceryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "zero-waste-db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideGroceryDao(appDatabase: AppDatabase): GroceryDao {
        return appDatabase.groceryDao()
    }
}
