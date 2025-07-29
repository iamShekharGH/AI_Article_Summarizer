package com.shekharhandigol.aiarticlesummarizer.domain

interface UseCase<in In, out Out> {
    suspend operator fun invoke(input: In): Out
}

interface NoInputUseCase<out Out> : UseCase<Unit, Out> {
    suspend operator fun invoke(): Out
    override suspend operator fun invoke(input: Unit): Out = invoke()
}

interface NoOutputUseCase<in In> : UseCase<In, Unit> {
    override suspend operator fun invoke(input: In)
}