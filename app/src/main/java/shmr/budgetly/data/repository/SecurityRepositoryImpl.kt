package shmr.budgetly.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import shmr.budgetly.data.local.secure.SecureStorage
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.repository.SecurityRepository
import java.security.MessageDigest
import javax.inject.Inject

@AppScope
class SecurityRepositoryImpl @Inject constructor(
    private val secureStorage: SecureStorage
) : SecurityRepository {

    override suspend fun savePin(pin: String) = withContext(Dispatchers.IO) {
        val hashedPin = hashString(pin)
        secureStorage.savePin(hashedPin)
    }

    override suspend fun checkPin(pin: String): Boolean = withContext(Dispatchers.IO) {
        val storedPinHash = secureStorage.getPin() ?: return@withContext false
        val providedPinHash = hashString(pin)
        return@withContext storedPinHash == providedPinHash
    }

    override suspend fun isPinSet(): Boolean = withContext(Dispatchers.IO) {
        return@withContext secureStorage.isPinSet()
    }

    override suspend fun clearPin() = withContext(Dispatchers.IO) {
        secureStorage.clearPin()
    }

    /**
     * Хеширует строку с использованием SHA-256.
     * Это одностороннее преобразование для безопасного сравнения пин-кодов.
     */
    private fun hashString(input: String): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }
}