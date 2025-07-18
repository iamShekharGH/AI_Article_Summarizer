package com.shekharhandigol.aiarticlesummarizer.domain

import com.shekharhandigol.aiarticlesummarizer.data.repoFiles.AiArticleSummarizerRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AddToFavoritesUseCaseTest {

    private lateinit var repository: AiArticleSummarizerRepository
    private lateinit var useCase: AddToFavoritesUseCase
    private val testCoroutineDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setUp() {
        repository = mockk()
        useCase = AddToFavoritesUseCase(repository)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @AfterEach
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `invoke calls repository to favourite article`() = runTest(testCoroutineDispatcher) {
        // Given
        val articleId = 1
        val isFavourite = true
        val input = Pair(articleId, isFavourite)

        // When
        useCase.invoke(input)

        // Then
        // Use MockK's verification
        coVerify { repository.favouriteThisArticle(articleId, isFavourite) }

    }

}