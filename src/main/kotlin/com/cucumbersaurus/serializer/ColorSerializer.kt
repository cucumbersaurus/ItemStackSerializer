package com.cucumbersaurus.serializer

import org.bukkit.Color
import java.util.*

object ColorSerializer{
    fun toHexARGB(col: Color) = Integer.toHexString(col.asARGB()).uppercase(Locale.getDefault())

    fun fromHexARGB(argb: String): Color {
        val argbList = argb.chunked(2).map { it.toInt(16) }
        return Color.fromARGB(argbList[0], argbList[1], argbList[2], argbList[3])
    }
}
