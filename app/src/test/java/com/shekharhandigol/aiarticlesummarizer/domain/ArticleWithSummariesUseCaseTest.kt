package com.shekharhandigol.aiarticlesummarizer.domain

import com.google.common.truth.Truth.assertThat
import com.shekharhandigol.aiarticlesummarizer.core.AiSummariserResult
import com.shekharhandigol.aiarticlesummarizer.core.ArticleUiModel
import com.shekharhandigol.aiarticlesummarizer.core.ArticleWithSummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.core.SummaryUiModel
import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.jupiter.api.DisplayName
import org.junit.Test

class ArticleWithSummariesUseCaseTest {

    private lateinit var repository: AiArticleSummarizerRepository
    private lateinit var useCase: ArticleWithSummariesUseCase
    private lateinit var testCoroutineDispatcher: TestDispatcher

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        repository = mockk()
        useCase = ArticleWithSummariesUseCase(repository)
        kotlinx.coroutines.Dispatchers.setMain(testCoroutineDispatcher)
        testCoroutineDispatcher = StandardTestDispatcher()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("invoke returns flow of article with summaries from repository")
    fun invoke_returnsFlowOfArticleWithSummaries_fromRepository() = runTest {
        val articleId = 1
        val currentTime = System.currentTimeMillis()
        val mockArticleUiModel = ArticleUiModel(
            articleId = articleId,
            title = "Test Article",
            articleUrl = "http://test.com",
            favouriteArticles = false,
            date = currentTime,
            tags = emptyList(),
            typeOfSummary = "Test Source",
            imageUrl = "https://images.pexels.com/photos/1925536/pexels-photo-1925536.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2"
        )
        val mockSummaryUiModelList = listOf(
            SummaryUiModel(
                articleId = articleId,
                summaryText = "Summary 1",
                ogText = "Original Text 1"
            ),
            SummaryUiModel(
                articleId = articleId,
                summaryText = "Summary 2",
                ogText = "Original Text 2"
            )
        )

        val mockArticleWithSummaryUiModel = ArticleWithSummaryUiModel(
            articleUiModel = mockArticleUiModel,
            summaryUiModel = mockSummaryUiModelList
        )

        val expectedResult = AiSummariserResult.Success(mockArticleWithSummaryUiModel)
        coEvery { repository.getArticleWithSummaries(articleId) } returns flowOf(expectedResult)

        val result = useCase.invoke(articleId).first()

        assertThat(result).isEqualTo(expectedResult)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("invoke returns flow of error when repository returns error")
    fun invoke_returnsFlowOfError_whenRepositoryReturnsError() = runTest {
        val articleId = 1
        val exception = RuntimeException("Error fetching article")
        val expectedError = AiSummariserResult.Error(exception)
        coEvery { repository.getArticleWithSummaries(articleId) } returns flowOf(expectedError)
        val result = useCase.invoke(articleId).first()
        assertThat(result).isEqualTo(expectedError)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("invoke returns flow of loading when repository returns loading")
    fun invoke_returnsFlowOfLoading_whenRepositoryReturnsLoading() = runTest {
        val articleId = 1
        val expectedLoading = AiSummariserResult.Loading
        coEvery { repository.getArticleWithSummaries(articleId) } returns flowOf(expectedLoading)
        val result = useCase.invoke(articleId).first()
        assertThat(result).isEqualTo(expectedLoading)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    @DisplayName("invoke returns flow of loading initially then success")
    fun invoke_returnsFlowOfLoadingInitially_thenSuccess() = runTest {
        val articleId = 1
        coEvery { repository.getArticleWithSummaries(articleId) } returns flowOf(
            AiSummariserResult.Loading,
            AiSummariserResult.Success(mockk())
        )
        val results = mutableListOf<AiSummariserResult<ArticleWithSummaryUiModel>>()
        useCase.invoke(articleId).collect { results.add(it) }
        assertThat(results[0]).isEqualTo(AiSummariserResult.Loading)
        assertThat(results[1]).isInstanceOf(AiSummariserResult.Success::class.java)
    }

}