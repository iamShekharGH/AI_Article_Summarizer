package com.shekharhandigol.aiarticlesummarizer.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.shekharhandigol.aiarticlesummarizer.core.SummaryType
import com.shekharhandigol.aiarticlesummarizer.util.DATABASE_NAME

@Entity(tableName = DATABASE_NAME)
@TypeConverters(Converters::class)
data class Article(
    @PrimaryKey(autoGenerate = true)
    val articleId: Int = 0,
    val title: String,
    val articleUrl: String,
    val favouriteArticles: Boolean = false,
    val date: Long = System.currentTimeMillis(),

    val tags: List<String> = emptyList(),
    val imageUrl: String
)


@Entity(
    tableName = "summaries",
    foreignKeys = [ForeignKey(
        entity = Article::class,
        parentColumns = ["articleId"],
        childColumns = ["articleId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Summary(
    @PrimaryKey(autoGenerate = true)
    val summaryId: Int = 0,
    val articleId: Int,
    val summaryText: String,
    val ogText: String,
    val summaryType: SummaryType
)

class Converters {
    @TypeConverter
    fun fromString(value: String?): List<String>? {
        return value?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return list?.joinToString(",")
    }

    @TypeConverter
    fun fromSummaryType(value: SummaryType): String {
        return value.name
    }

    @TypeConverter
    fun toSummaryType(value: String): SummaryType {
        return try {
            SummaryType.valueOf(value) // Convert string back to enum
        } catch (e: Exception) {
            SummaryType.MEDIUM_SUMMARY // Default value if conversion fails
        }
    }
}