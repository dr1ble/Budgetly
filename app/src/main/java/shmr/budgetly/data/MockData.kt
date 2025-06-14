package shmr.budgetly.data

import shmr.budgetly.domain.entity.Account
import shmr.budgetly.domain.entity.Category
import shmr.budgetly.domain.entity.Transaction

object MockData {


    // Расходы
    private val categoryRent =
        Category(id = 1, name = "Аренда квартиры", emoji = "\uD83C\uDFE1", isIncome = false)
    private val categoryClothes =
        Category(id = 2, name = "Одежда", emoji = "\uD83D\uDC57", isIncome = false)
    private val categoryDog =
        Category(id = 3, name = "На собачку", emoji = "\uD83D\uDC36", isIncome = false)
    private val categoryRepair =
        Category(id = 4, name = "Ремонт квартиры", emoji = "РК", isIncome = false)
    private val categoryFood =
        Category(id = 5, name = "Продукты", emoji = "\uD83C\uDF6D", isIncome = false)
    private val categorySport =
        Category(id = 6, name = "Спортзал", emoji = "\uD83D\uDCAA", isIncome = false)
    private val categoryMedicine =
        Category(id = 7, name = "Медицина", emoji = "\uD83D\uDC8A", isIncome = false)

    // Доходы
    private val categorySalary =
        Category(id = 8, name = "Зарплата", emoji = "\uD83D\uDCB0", isIncome = true)
    private val categorySideJob =
        Category(id = 9, name = "Подработка", emoji = "\uD83D\uDCBB", isIncome = true)

    val expenseTransactions = listOf(
        Transaction(id = 1, category = categoryRent, amount = "100 000 ₽"),
        Transaction(id = 2, category = categoryClothes, amount = "100 000 ₽"),
        Transaction(id = 3, category = categoryDog, amount = "100 000 ₽", comment = "Джек"),
        Transaction(id = 4, category = categoryDog, amount = "100 000 ₽", comment = "Энни"),
        Transaction(id = 5, category = categoryRepair, amount = "100 000 ₽"),
        Transaction(id = 6, category = categoryFood, amount = "100 000 ₽"),
        Transaction(id = 7, category = categorySport, amount = "100 000 ₽"),
        Transaction(id = 8, category = categoryMedicine, amount = "100 000 ₽")
    )

    val incomeTransactions = listOf(
        Transaction(id = 9, category = categorySalary, amount = "500 000 ₽"),
        Transaction(id = 10, category = categorySideJob, amount = "100 000 ₽")
    )
    val balanceCategory =
        Category(id = 99, name = "Баланс", emoji = "\uD83D\uDCB0", isIncome = false)

    val mainAccount = Account(
        id = 1,
        name = "Мой счет",
        balance = "-670 000",
        currency = "₽"
    )

    val allCategories = listOf(
        categoryRent,
        categoryClothes,
        categoryDog,
        categoryRepair,
        categoryFood,
        categorySport,
        categoryMedicine,
    )

    val settingsItems = listOf(
        "Тёмная тема",
        "Основной цвет",
        "Звуки",
        "Хаптики",
        "Код пароль",
        "Синхронизация",
        "Язык",
        "О программе"
    )
}
