package shmr.budgetly.domain.monitor

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import shmr.budgetly.di.scope.AppScope
import shmr.budgetly.domain.model.HapticEffect
import javax.inject.Inject

/**
 * Менеджер для управления тактильной обратной связью (вибрацией).
 * Предоставляет унифицированный API для вызова различных эффектов вибрации.
 */
@AppScope
class HapticFeedbackManager @Inject constructor(context: Context) {

    private val vibrator: Vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager =
            context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    /**
     * Константы, определяющие паттерны вибрации для fallback-сценариев.
     */
    private object VibrationPatterns {
        const val CLICK_DURATION = 20L
        const val TICK_DURATION = 10L
        const val DOUBLE_CLICK_PAUSE_DURATION = 50L

        val CLICK_TIMINGS = longArrayOf(0, CLICK_DURATION)
        val CLICK_AMPLITUDES = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE)

        val TICK_TIMINGS = longArrayOf(0, TICK_DURATION)
        val TICK_AMPLITUDES = intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE / 2)

        val DOUBLE_CLICK_TIMINGS =
            longArrayOf(0, CLICK_DURATION, DOUBLE_CLICK_PAUSE_DURATION, CLICK_DURATION)
        val DOUBLE_CLICK_AMPLITUDES =
            intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE)

        val DEFAULT_TIMINGS = CLICK_TIMINGS
        val DEFAULT_AMPLITUDES = CLICK_AMPLITUDES
    }

    /**
     * Воспроизводит указанный тактильный эффект.
     * @param effect Тип эффекта для воспроизведения.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    fun performHapticEffect(effect: HapticEffect) {
        if (!vibrator.hasVibrator()) return

        val vibrationEffect = when (effect) {
            HapticEffect.CLICK -> createEffect(VibrationEffect.EFFECT_CLICK)
            HapticEffect.TICK -> createEffect(VibrationEffect.EFFECT_TICK)
            HapticEffect.DOUBLE_CLICK -> createEffect(VibrationEffect.EFFECT_DOUBLE_CLICK)
        }

        vibrator.vibrate(vibrationEffect)
    }

    /**
     * Создает VibrationEffect.
     * Сначала пытается использовать предпочтительный системный эффект,
     * а при неудаче создает кастомный паттерн.
     */
    private fun createEffect(effectId: Int): VibrationEffect {
        // На API 29+ доступен метод createPredefined
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // На API 30+ мы можем проверить, поддерживается ли эффект
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // Используем spread оператор (*) для передачи массива в vararg
                val supportResult = vibrator.areEffectsSupported(*intArrayOf(effectId))
                if (supportResult[0] == Vibrator.VIBRATION_EFFECT_SUPPORT_YES) {
                    return VibrationEffect.createPredefined(effectId)
                }
            } else {
                // На API 29 мы не можем проверить поддержку, но можем попытаться создать эффект.
                // Система сама обработает случай, если он не поддерживается.
                return VibrationEffect.createPredefined(effectId)
            }
        }
        // Fallback для старых устройств или если эффект не поддерживается на API 30+.
        return createFallbackWaveform(effectId)
    }

    /**
     * Создает кастомный паттерн вибрации (waveform) для старых API или
     * в случае отсутствия поддержки системного эффекта.
     */
    private fun createFallbackWaveform(effectId: Int): VibrationEffect {
        val (timings, amplitudes) = getFallbackPattern(effectId)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect.createWaveform(timings, amplitudes, -1)
        } else {
            @Suppress("DEPRECATION")
            VibrationEffect.createWaveform(timings, -1)
        }
    }

    /**
     * Возвращает пару массивов (тайминги и амплитуды) для заданного ID эффекта.
     */
    private fun getFallbackPattern(effectId: Int): Pair<LongArray, IntArray> {
        return when (effectId) {
            VibrationEffect.EFFECT_CLICK -> VibrationPatterns.CLICK_TIMINGS to VibrationPatterns.CLICK_AMPLITUDES
            VibrationEffect.EFFECT_TICK -> VibrationPatterns.TICK_TIMINGS to VibrationPatterns.TICK_AMPLITUDES
            VibrationEffect.EFFECT_DOUBLE_CLICK -> VibrationPatterns.DOUBLE_CLICK_TIMINGS to VibrationPatterns.DOUBLE_CLICK_AMPLITUDES
            else -> VibrationPatterns.DEFAULT_TIMINGS to VibrationPatterns.DEFAULT_AMPLITUDES
        }
    }
}