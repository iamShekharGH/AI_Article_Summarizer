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

    @Query("SELECT * FROM $DATABASE_NAME WHERE favouriteArticles = 1")
    fun getAllFavoriteArticles(): List<Article>

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


    @Query("UPDATE $DATABASE_NAME SET favouriteArticles = 1 WHERE articleId = :articleId")
    fun favouriteThisArticle(articleId: Int)

    @Query("UPDATE $DATABASE_NAME SET favouriteArticles = 0 WHERE articleId = :articleId")
    fun removeFavouriteFromThisArticle(articleId: Int)

    @Query("SELECT * FROM $DATABASE_NAME WHERE title LIKE '%' || :query || '%' OR articleUrl LIKE '%' || :query || '%'")
    fun searchArticles(query: String): List<Article>

}


data class ArticleWithSummaries(
    @androidx.room.Embedded val article: Article,
    @Relation(
        parentColumn = "articleId",
        entityColumn = "articleId"
    )
    val summaries: List<Summary>
)