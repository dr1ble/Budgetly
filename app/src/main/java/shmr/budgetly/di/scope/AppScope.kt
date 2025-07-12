package shmr.budgetly.di.scope

import javax.inject.Scope

/**
 * Кастомная аннотация Scope для обозначения зависимостей,
 * жизненный цикл которых привязан к AppComponent (т.е. ко всему приложению).
 * Это гарантирует, что зависимость будет создана только один раз.
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class AppScope