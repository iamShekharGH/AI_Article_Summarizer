package com.shekharhandigol.aiarticlesummarizer.domain

import com.google.common.truth.Truth.assertThat
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class GetAllArticlesUseCaseTest {
    private lateinit var repository: AiArticleSummarizerRepository
    private lateinit var useCase: GetAllArticlesUseCase

    @BeforeEach
    fun setUp() {
        repository = mockk()
        useCase = GetAllArticlesUseCase(repository)
    }

    @Test
    @DisplayName("invoke calls repository to get all articles")
    fun `invoke calls repository to get all articles`() = runTest {
        // Given
        val listOfArticleUiModel = listOf(
            ArticleUiModel(
                articleId = 1,
                title = "Test Article",
                articleUrl = "http://test.com",
                favouriteArticles = false,
                date = System.currentTimeMillis(),
                tags = emptyList(),
                typeOfSummary = "Test Source",
                imageUrl = "https://images.pexels.com/photos/1925536/pexels-photo-1925536.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
            ),
            ArticleUiModel(
                articleId = 2,
                title = "Test Article",
                articleUrl = "http://test.com",
                favouriteArticles = false,
                date = System.currentTimeMillis(),
                tags = emptyList(),
                typeOfSummary = "Test Source",
                imageUrl = "https://images.pexels.com/photos/1925536/pexels-photo-1925536.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
            )

        )
        val expectedResult = AiSummariserResult.Success(listOfArticleUiModel)
        coEvery { repository.getAllArticles() } returns flowOf(expectedResult)

        // When
        val result = useCase.invoke()
        assertThat(result.last()).isEqualTo(expectedResult)
    }
}