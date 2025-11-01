package com.example.mgapp

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.onNodeWithText
import com.example.mgapp.ui.components.HotspotBottomSheetPreviewable
import org.junit.Rule
import org.junit.Test

class HotspotBottomSheetUITest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun bottomSheet_displaysBasicFields() {
        composeRule.setContent {
            // ⚠️ Usa la versión sin Hilt
            HotspotBottomSheetPreviewable()
        }

        composeRule.onNodeWithText("Name").assertExists()
        composeRule.onNodeWithText("Description").assertExists()
        composeRule.onNodeWithText("Save").assertExists()
        composeRule.onNodeWithText("Cancel").assertExists()
    }
}
