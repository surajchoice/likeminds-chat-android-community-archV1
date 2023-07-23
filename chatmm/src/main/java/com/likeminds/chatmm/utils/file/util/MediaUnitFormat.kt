package com.likeminds.chatmm.utils.file.util

import java.text.DecimalFormat
import kotlin.math.log10
import kotlin.math.pow

/**
 * Used for the pretty formatting of bytes for user display.
 */
enum class MemoryUnitFormat(private val unitString: String) {
    BYTES(" B"), KILO_BYTES(" kB"), MEGA_BYTES(" MB"), GIGA_BYTES(" GB"), TERA_BYTES(" TB");

    fun fromBytes(bytes: Long): Double {
        return bytes / 1000.0.pow(ordinal.toDouble())
    }

    companion object {
        private val ONE_DP = DecimalFormat("#,##0.0")
        private val OPTIONAL_ONE_DP = DecimalFormat("#,##0.#")

        /**
         * Creates a string suitable to present to the user from the specified {@param originalBytes}.
         * It will pick a suitable unit of measure to display depending on the size of the bytes.
         * It will not select a unit of measure lower than the specified {@param minimumUnit}.
         *
         * @param forceOneDp If true, will include 1 decimal place, even if 0. If false, will only show 1 dp when it's non-zero.
         */
        @JvmOverloads
        fun formatBytes(
            originalBytes: Long,
            minimumUnit: MemoryUnitFormat = BYTES,
            forceOneDp: Boolean = false
        ): String {
            var bytes = originalBytes
            if (bytes <= 0) bytes = 0
            var ordinal = if (bytes != 0L) (log10(bytes.toDouble()) / 3).toInt() else 0
            if (ordinal >= values().size) {
                ordinal = values().size - 1
            }
            var unit = values()[ordinal]
            if (unit.ordinal < minimumUnit.ordinal) {
                unit = minimumUnit
            }
            return (if (forceOneDp) ONE_DP else OPTIONAL_ONE_DP).format(unit.fromBytes(bytes)) + unit.unitString
        }
    }
}