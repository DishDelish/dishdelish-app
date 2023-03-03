package com.github.siela1915.bootcamp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.siela1915.bootcamp.ui.theme.SDPBootcampTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setupNavHost() {
        composeTestRule.setContent {
            SDPBootcampTheme {
                MainActivityNavHost()
            }
        }
    }

    @Test
    fun navHost_mainScreenTest() {
        composeTestRule.onNodeWithTag("nameInputField").assertIsDisplayed()
        composeTestRule.onNodeWithTag("goGreetingButton").assertIsDisplayed()
    }

    @Test
    fun navHost_mainToEmptyGreetingTest() {
        composeTestRule.onNodeWithTag("goGreetingButton").performClick()
        composeTestRule.onNodeWithTag("greetingText").assertIsDisplayed().assertTextEquals("Hello !")
    }

    @Test
    fun navHost_mainToNameGreetingTest() {
        composeTestRule.onNodeWithTag("nameInputField").performTextInput("Test")
        composeTestRule.onNodeWithTag("goGreetingButton").performClick()
        composeTestRule.onNodeWithTag("greetingText").assertIsDisplayed().assertTextEquals("Hello Test!")
    }
}