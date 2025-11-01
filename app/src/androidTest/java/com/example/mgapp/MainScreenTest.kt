package com.example.mgapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.assertIsDisplayed
import org.junit.Rule
import org.junit.Test

class MainScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun mainScreenDisplaysTabsAndTitle() {
        composeTestRule.onNodeWithText("mGapp SVG Editor").assertIsDisplayed()
        composeTestRule.onNodeWithText("SVG Map").assertIsDisplayed()
        composeTestRule.onNodeWithText("Hotspot List").assertIsDisplayed()
    }
}
