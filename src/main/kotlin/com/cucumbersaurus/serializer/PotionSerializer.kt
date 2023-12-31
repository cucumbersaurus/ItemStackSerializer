package com.cucumbersaurus.serializer

import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionType

object PotionDataSerializer {
        fun toMap(data: PotionData): Map<String, Any> {
            val map = HashMap<String, Any>()
            map["type"] = data.type.name
            map["isExtended"] = data.isExtended
            map["isUpgraded"] = data.isUpgraded
            return map
        }

        fun fromMap(map: Map<String, Any>): PotionData {
            val type = PotionType.valueOf(map["type"] as String)
            val isExtended = map["isExtended"] as Boolean
            val isUpgraded = map["isUpgraded"] as Boolean
            return PotionData(type, isExtended, isUpgraded)
        }
    }

object PotionEffectSerializer {

        fun toMap(effect: PotionEffect) = effect.serialize()

        fun fromMap(map: Map<String, Any>) = PotionEffect(map)
    }
