package shmr.budgetly

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shmr.budgetly.ui.navigation.Settings

@RunWith(AndroidJUnit4::class)
class SettingsFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testThemeSwitching() {


        val settingsButtonText = composeTestRule.activity.getString(Settings.label)

        composeTestRule
            .onNodeWithText(settingsButtonText, ignoreCase = true)
            .assertExists()
            .performClick()
    }

    @Test
    fun testPinCodeSetupFlow() {
        // Arrange: Переходим на экран настроек пин-кода
        val settingsButtonText = composeTestRule.activity.getString(Settings.label)
        val pinCodeSettingsText = composeTestRule.activity.getString(R.string.setting_passcode)

        composeTestRule.onNodeWithText(settingsButtonText).performClick()
        composeTestRule.onNodeWithText(pinCodeSettingsText).performClick()

        // Нажимаем "Установить пин-код"
        val setupPinText = composeTestRule.activity.getString(R.string.setting_passcode_set)
        composeTestRule.onNodeWithText(setupPinText).performClick()

        // Act: Вводим пин-код в первый раз
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("3").performClick()
        composeTestRule.onNodeWithText("4").performClick()

        // Assert: Проверяем, что заголовок изменился на "Подтвердите пин-код"
        val confirmPinTitle = composeTestRule.activity.getString(R.string.pincode_confirm_title)
        composeTestRule.onNodeWithText(confirmPinTitle).assertIsDisplayed()

        // Act: Вводим тот же пин-код для подтверждения
        composeTestRule.onNodeWithText("1").performClick()
        composeTestRule.onNodeWithText("2").performClick()
        composeTestRule.onNodeWithText("3").performClick()
        composeTestRule.onNodeWithText("4").performClick()

        // Assert: После успешного создания пин-кода мы должны вернуться
        // на экран настроек пин-кода, где теперь есть опция "Изменить пин-код"
        composeTestRule.waitForIdle()
        val changePinText = composeTestRule.activity.getString(R.string.setting_passcode_change)
        composeTestRule.onNodeWithText(changePinText).assertIsDisplayed()
    }
}