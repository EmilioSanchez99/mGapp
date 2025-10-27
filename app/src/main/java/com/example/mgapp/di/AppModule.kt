package com.example.mgapp.di

import android.app.Application
import androidx.room.Room
import com.example.mgapp.data.local.AppDatabase
import com.example.mgapp.data.local.dao.HotspotDao
import com.example.mgapp.data.repository.HotspotRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase =
        Room.databaseBuilder(app, AppDatabase::class.java, "hotspot_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideHotspotDao(db: AppDatabase): HotspotDao = db.hotspotDao()

    @Provides
    @Singleton
    fun provideHotspotRepository(dao: HotspotDao): HotspotRepository = HotspotRepository(dao)
}
