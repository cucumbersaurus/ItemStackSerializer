package com.cucumbersaurus.serializer

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer

object ComponentSerializer {
    fun toJson(component: Component) = JSONComponentSerializer.json().serialize(component)

    fun fromJson(json: String) = JSONComponentSerializer.json().deserialize(json)
}
