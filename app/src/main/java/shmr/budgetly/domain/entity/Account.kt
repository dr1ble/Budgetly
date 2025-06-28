package shmr.budgetly.domain.entity

/**
 * Представляет счет пользователя в доменном слое.
 * @param id Уникальный идентификатор счета.
 * @param name Название счета.
 * @param balance Текущий баланс в виде строки.
 * @param currency Символ валюты.
 */
data class Account(
    val id: Int,
    val name: String,
    val balance: String,
    val currency: String = "₽"
)