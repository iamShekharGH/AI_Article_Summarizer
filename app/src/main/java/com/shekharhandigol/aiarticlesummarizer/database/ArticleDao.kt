package com.shekharhandigol.aiarticlesummarizer.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.shekharhandigol.aiarticlesummarizer.util.DATABASE_NAME

@Dao
interface ArticleDao {

    @Query("SELECT * FROM $DATABASE_NAME")
    fun getAllArticles(): List<Article>

    @Query("SELECT * FROM $DATABASE_NAME WHERE articleId = :articleId")
    suspend fun getArticleById(articleId: Int): Article?

    @Insert
    suspend fun insertArticle(article: Article) : Long

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("DELETE FROM $DATABASE_NAME WHERE articleId = :articleId")
    suspend fun deleteArticleById(articleId: Int)

    @Transaction
    @Query("SELECT * FROM $DATABASE_NAME WHERE articleId = :articleId")
    suspend fun getArticleWithSummaries(articleId: Int): ArticleWithSummaries?

}


data class ArticleWithSummaries(
    @androidx.room.Embedded val article: Article,
    @Relation(
        parentColumn = "articleId",
        entityColumn = "articleId"
    )
    val summaries: List<Summary>
)