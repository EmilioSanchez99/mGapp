package com.example.mgapp

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test

class ExportFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun exportShowsSnackbarMessage() {
        // Clic en el botón de exportar
        composeTestRule.onNodeWithContentDescription("Export JSON").performClick()

        // Comprueba que se muestra el mensaje de éxito
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Exported successfully").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Exported successfully").assertExists()
    }
}
