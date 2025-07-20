package com.glanci.navigation.data.repository

import com.glanci.navigation.data.model.NavigationButtonDataModel

interface NavigationButtonRepository {

    fun upsertNavigationButtons(buttons: List<NavigationButtonDataModel>)

    fun getNavigationButtonsAfterTimestamp(userId: Int, timestamp: Long): List<NavigationButtonDataModel>

}