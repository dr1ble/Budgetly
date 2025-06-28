package shmr.budgetly

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Основной класс приложения, инициализирующий Hilt для внедрения зависимостей.
 */
@HiltAndroidApp
class BudgetlyApp : Application()