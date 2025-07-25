package shmr.budgetly.data.local.secure

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import shmr.budgetly.di.scope.AppScope
import javax.inject.Inject

/**
 * Обертка для безопасного хранения данных с использованием EncryptedSharedPreferences.
 * Шифрует как ключи, так и значения.
 */
@Suppress("DEPRECATION")
@AppScope
class SecureStorage @Inject constructor(context: Context) {

    private object Keys {
        const val PIN_CODE_KEY = "budgetly_pin_code"
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "budgetly_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Сохраняет пин-код в зашифрованное хранилище.
     * @param pin Хеш пин-кода для сохранения.
     */
    fun savePin(pin: String) {
        with(sharedPreferences.edit()) {
            putString(Keys.PIN_CODE_KEY, pin)
            apply()
        }
    }

    /**
     * Получает сохраненный хеш пин-кода.
     * @return Хеш пин-кода или null, если он не установлен.
     */
    fun getPin(): String? {
        return sharedPreferences.getString(Keys.PIN_CODE_KEY, null)
    }

    /**
     * Проверяет, установлен ли пин-код.
     * @return true, если пин-код сохранен, иначе false.
     */
    fun isPinSet(): Boolean {
        return sharedPreferences.contains(Keys.PIN_CODE_KEY)
    }

    /**
     * Удаляет пин-код из хранилища.
     */
    fun clearPin() {
        with(sharedPreferences.edit()) {
            remove(Keys.PIN_CODE_KEY)
            apply()
        }
    }
}