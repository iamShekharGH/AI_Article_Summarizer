package com.shekharhandigol.aiarticlesummarizer

import android.content.Context
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.LocalFileDataSource
import com.shekharhandigol.aiarticlesummarizer.database.ArticleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }

    @Provides
    @Singleton
    fun provideLocalFileDataSource(
        json: Json,
        @ApplicationContext context: Context,
        articleDao: ArticleDao
    ): LocalFileDataSource {
        return LocalFileDataSource(
            json = json,
            context = context,
            articleDao = articleDao
        )
    }

}