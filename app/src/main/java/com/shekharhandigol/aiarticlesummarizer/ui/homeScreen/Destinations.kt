package com.shekharhandigol.aiarticlesummarizer.ui.homeScreen

import kotlinx.serialization.Serializable


@Serializable
sealed class Destinations {
    @Serializable
    data object MainHome : Destinations()

    @Serializable
    data object Home : Destinations()

    @Serializable
    data object Settings : Destinations()

    @Serializable
    data object List : Destinations()

    @Serializable
    data object Search : Destinations()

    @Serializable
    data object FavouriteList : Destinations()

    @Serializable
    data object ChooseTheme : Destinations()
}