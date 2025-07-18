package shmr.budgetly.di.scope

import javax.inject.Scope

/**
 * Кастомная аннотация Scope для обозначения зависимостей,
 * жизненный цикл которых привязан к конкретному экрану (записи в NavBackStack).
 */
@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ScreenScope