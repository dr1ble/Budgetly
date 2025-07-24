package shmr.budgetly

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import shmr.budgetly.domain.model.ThemeColor
import shmr.budgetly.ui.navigation.Settings
import shmr.budgetly.ui.theme.getBudgetlyApplicationScheme

/**
 * Тестовый класс для проверки пользовательских сценариев на экране настроек.
 */
@RunWith(AndroidJUnit4::class)
class SettingsFlowTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Тестирует функциональность переключения темной темы и проверяет реальное изменение цвета фона.
     * Сценарий:
     * 1. Дожидается появления главного экрана.
     * 2. Переходит на вкладку "Настройки".
     * 3. Проверяет, что цвет фона соответствует светлой теме.
     * 4. Находит и включает переключатель "Темная тема".
     * 5. Проверяет, что переключатель находится во включенном состоянии.
     * 6. Проверяет, что цвет фона изменился на цвет темной темы.
     * 7. Выключает переключатель и снова проверяет, что фон вернулся к цвету светлой темы.
     */
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun testThemeSwitching() {
        // Определяем ожидаемые цвета фона для светлой и темной тем
        val lightThemeBackgroundColor =
            getBudgetlyApplicationScheme(ThemeColor.GREEN, false).background
        val darkThemeBackgroundColor =
            getBudgetlyApplicationScheme(ThemeColor.GREEN, true).background

        val settingsButtonText = composeTestRule.activity.getString(Settings.label)

        // Ожидаем до 5 секунд, пока сплэш-скрин не исчезнет и появится вкладка "Настройки"
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithText(settingsButtonText, ignoreCase = true)
                .fetchSemanticsNodes().size == 1
        }

        // Переходим на экран настроек
        composeTestRule
            .onNodeWithText(settingsButtonText, ignoreCase = true)
            .performClick()

        // Находим контейнер экрана настроек и переключатель
        val settingsContainer = composeTestRule.onNodeWithTag("SettingsScreenContainer")
        val themeSwitch = composeTestRule.onNodeWithTag("DarkThemeSwitch")

        // 1. Проверяем начальное состояние: светлая тема
        themeSwitch.assertIsOff()
        settingsContainer.assertBackgroundColor(lightThemeBackgroundColor)

        // 2. Кликаем по переключателю, чтобы включить темную тему
        themeSwitch.performClick()

        // 3. Проверяем состояние после включения: темная тема
        themeSwitch.assertIsOn()
        settingsContainer.assertBackgroundColor(darkThemeBackgroundColor)

        // 4. Кликаем еще раз, чтобы выключить темную тему
        themeSwitch.performClick()

        // 5. Проверяем состояние после выключения: снова светлая тема
        themeSwitch.assertIsOff()
        settingsContainer.assertBackgroundColor(lightThemeBackgroundColor)
    }

    /**
     * Тестирует полный сценарий установки нового пин-кода.
     * Сценарий:
     * 1. Дожидается появления главного экрана.
     * 2. Переходит на экран "Настройки" -> "Пин-код".
     * 3. Нажимает "Установить пин-код".
     * 4. Вводит пин-код "1234".
     * 5. Проверяет, что открылся экран подтверждения пин-кода.
     * 6. Вводит тот же пин-код "1234" для подтверждения.
     * 7. Проверяет, что происходит возврат на экран настроек пин-кода
     *    и текст опции меняется на "Изменить пин-код".
     */
    @Test
    fun testPinCodeSetupFlow() {
        // Arrange: Переходим на экран настроек пин-кода
        val settingsButtonText = composeTestRule.activity.getString(Settings.label)
        val pinCodeSettingsText = composeTestRule.activity.getString(R.string.setting_passcode)

        // Ожидаем появления главного экрана
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            composeTestRule
                .onAllNodesWithText(settingsButtonText, ignoreCase = true)
                .fetchSemanticsNodes().size == 1
        }

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

    /**
     * Вспомогательная функция-расширение для проверки цвета фона компонента.
     * Она делает снимок компонента и проверяет цвет пикселя в его центре.
     * @param expectedColor Ожидаемый цвет фона.
     */
    @OptIn(ExperimentalTestApi::class)
    private fun SemanticsNodeInteraction.assertBackgroundColor(expectedColor: Color) {
        val imageBitmap = captureToImage()
        val pixelMap = imageBitmap.toPixelMap()
        val x = imageBitmap.width / 2
        val y = imageBitmap.height / 2
        assertEquals(expectedColor, pixelMap[x, y])
    }
}