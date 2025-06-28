package shmr.budgetly.data.network.exception

import java.io.IOException

/**
 * Исключение, выбрасываемое при отсутствии подключения к интернету.
 * Используется для типизации ошибок сети в слое данных.
 */
class NoConnectivityException : IOException("Нет подключения к интернету")