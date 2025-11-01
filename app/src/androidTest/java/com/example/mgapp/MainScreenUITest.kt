package com.example.mgapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.material3.Text
import org.junit.Rule
import org.junit.Test

class MainScreenUITest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun text_isDisplayedCorrectly() {
        composeRule.setContent {
            Text("Hello mGapp!")
        }

        composeRule.onNodeWithText("Hello mGapp!").assertIsDisplayed()
    }
}
