package com.shekharhandigol.aiarticlesummarizer.util

import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.core.SummaryType
import com.shekharhandigol.aiarticlesummarizer.core.SummaryUiModel


val articleUiModel = ArticleUiModel(
    articleId = 1,
    title = "Sample Article Title",
    articleUrl = "https://example.com/sample-article",
    favouriteArticles = true,
    date = System.currentTimeMillis(),
    tags = listOf("sample", "testing", "preview"),
    imageUrl = "https://images.pexels.com/photos/1925536/pexels-photo-1925536.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
)

val summaryUiModel = SummaryUiModel(
    articleId = 1,
    summaryText = "This is a sample summary text. It provides a brief overview of the article's content.",
    ogText = "This is the original text of the article. It is longer and contains more details than the summary.",
    summaryType = SummaryType.SHORT_SUMMARY
)

val articleWithSummaries = ArticleWithSummaryUiModel(
    articleUiModel = articleUiModel,
    summaryUiModel = listOf(summaryUiModel)
)
val articleSummariesDummyData2 = ArticleWithSummaryUiModel(
    articleUiModel = ArticleUiModel(title = "Dummy Article", articleUrl = "http://example.com"),
    summaryUiModel = listOf(
        SummaryUiModel(
            articleId = 0,
            summaryText = "This is a dummy summary.",
            ogText = "This is the original dummy text.",
            summaryType = SummaryType.SHORT_SUMMARY
        )
    )
)


val articleSummariesDummyData = ArticleWithSummaryUiModel(
    articleUiModel = ArticleUiModel(
        title = "Kotlin’s Builder Functions: " +
                "A Better Way to Create " +
                "Lists, " +
                "Maps, " +
                "Strings & Sets",
        articleUrl = "https://medium.com/@shekharhandigol/kotlins-builder-functions",
        favouriteArticles = false,
        tags = listOf("Kotlin", "Programming", "Android"),
        imageUrl = "https://miro.medium.com/v2/resize:fit:1400/1*1Z3Z3Z3Z3Z3Z3Z3Z3Z3Z3A.png"
    ),
    summaryUiModel = listOf(
        SummaryUiModel(
            articleId = 777,
            summaryText = "Kotlin's builder functions simplify the creation of common data structures. buildList {} creates immutable lists from mutable operations within a lambda, with primitive-optimized versions like buildIntList {}. buildString {} offers a concise way to build strings using a StringBuilder in a lambda. buildSet {} constructs immutable sets, with type-specific options like buildIntSet {} (note: order is not guaranteed). buildMap {} facilitates immutable map creation, including specialized versions like buildIntIntMap {} for primitives. These standard library features reduce boilerplate and enhance code readability for object construction. Other libraries also provide similar builder utilities.",
            ogText = "This is the original text of the article. It's much longer and more detailed than the summary. It goes into depth about Kotlin's builder functions, providing code examples and explanations for each type: buildList, buildString, buildSet, and buildMap. The article also discusses the benefits of using these functions, such as improved code readability and reduced boilerplate. It might also touch upon performance considerations and compare these builders to traditional ways of creating collections or strings.",
            summaryType = SummaryType.MEDIUM_SUMMARY

        )
    )
)