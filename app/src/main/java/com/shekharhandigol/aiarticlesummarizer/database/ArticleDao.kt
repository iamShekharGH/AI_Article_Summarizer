package com.shekharhandigol.aiarticlesummarizer.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import com.shekharhandigol.aiarticlesummarizer.util.DATABASE_NAME
import kotlinx.coroutines.flow.Flow
import kotlin.collections.map

@Dao
interface ArticleDao {

    @Query("SELECT * FROM $DATABASE_NAME ORDER BY articleId DESC")
    fun getAllArticles(): Flow<List<Article>>

    @Query("SELECT * FROM $DATABASE_NAME WHERE favouriteArticles = 1 ORDER BY articleId DESC")
    fun getAllFavoriteArticles(): Flow<List<Article>>

    @Query("SELECT * FROM $DATABASE_NAME WHERE articleId = :articleId")
    suspend fun getArticleById(articleId: Int): Article?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: Article) : Long

    @Transaction
    suspend fun insertArticleAndSummaries(article: Article, summaries: List<Summary>): Long {
        val articleIdLong = insertArticle(article)
        val articleIdInt = articleIdLong.toInt() // Convert Long to Int for Summary's articleId

        val summariesWithArticleId = summaries.map { summary ->
            Summary(
                summaryId = summary.summaryId, // Keep original summaryId if needed for updates, or 0 for new
                articleId = articleIdInt,
                summaryText = summary.summaryText,
                ogText = summary.ogText
            )
        }
        if (summariesWithArticleId.isNotEmpty()) {
            insertSummaries(summariesWithArticleId)
        }
        return articleIdLong
    }

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummaries(summaries: List<Summary>)

}


data class ArticleWithSummaries(
    @androidx.room.Embedded val article: Article,
    @Relation(
        parentColumn = "articleId",
        entityColumn = "articleId"
    )
    val summaries: List<Summary>
)