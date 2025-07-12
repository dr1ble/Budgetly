package shmr.budgetly.di.viewmodel

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Кастомная аннотация MapKey для Dagger.
 * Она позволяет использовать класс ViewModel в качестве ключа в Map,
 * которую Dagger собирает для ViewModelFactory.
 *
 * @param value Класс ViewModel, который будет служить ключом.
 */
@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ViewModelKey(val value: KClass<out ViewModel>)