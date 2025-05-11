package com.shekharhandigol.aiarticlesummarizer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.shekharhandigol.aiarticlesummarizer.util.DATABASE_NAME

@Dao
interface ArticleDao {

    @Query("SELECT * FROM $DATABASE_NAME")
    fun getAllArticles(): List<Article>

    @Query("SELECT * FROM $DATABASE_NAME WHERE articleId = :articleId")
    suspend fun getArticleById(articleId: Int): Article?

    @Insert
    suspend fun insertArticle(article: Article)


/*
    @Transaction
    @Query("SELECT * FROM articles WHERE articleId = :articleId")
    suspend fun getArticleWithSummary(articleId: Int): Article?*/

}
