package com.example.mgapp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import com.example.mgapp.domain.model.Hotspot
import com.example.mgapp.ui.components.HotspotBottomSheet
import org.junit.Rule
import org.junit.Test

class HotspotBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun bottomSheetShowsFieldsAndButtons() {
        val hotspot = Hotspot(1, 0f, 0f, "", null)

        composeTestRule.setContent {
            HotspotBottomSheet(
                hotspot = hotspot,
                onSave = {},
                onDismiss = {}
            )
        }

        composeTestRule.onNodeWithText("New Hotspot").assertIsDisplayed()
        composeTestRule.onNodeWithText("Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Description").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").assertIsDisplayed()
    }
}
