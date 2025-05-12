package com.shekharhandigol.aiarticlesummarizer.database

import android.content.Context
import androidx.room.Room
import com.shekharhandigol.aiarticlesummarizer.util.DATABASE_NAME
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
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration(false).build()
    }

    @Provides
    fun providesDao(database: AppDatabase): ArticleDao {
        return database.articleDao()
    }

    @Provides
    fun providesSummaryDao(database: AppDatabase): SummaryDao {
        return database.summaryDao()
    }
}