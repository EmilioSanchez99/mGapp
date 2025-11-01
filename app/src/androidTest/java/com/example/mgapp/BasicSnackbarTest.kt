package com.example.mgapp

import androidx.activity.ComponentActivity
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class BasicSnackbarTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun snackbar_displaysMessage() {
        composeRule.setContent {
            Snackbar {
                Text("Exported successfully")
            }
        }

        composeRule.onNodeWithText("Exported successfully").assertExists()
    }
}
