package com.cucumbersaurus.serializer

import korlibs.datastructure.fastCastTo
import korlibs.io.serialization.json.Json
import korlibs.io.serialization.json.toJson
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.collections.HashMap

object ItemStackSerializer {
    fun toJson(item: ItemStack, pretty: Boolean = false): String{
        val map = toMap(item)
        return map.toJson(pretty)
    }

    fun toMap(item: ItemStack): Map<String, Any>{
        val map  = HashMap<String, Any>()
        map["type"] = item.type.name
        map["amount"] = item.amount
        map["meta"] = ItemMetaSerializer.toMap(item.itemMeta)
        // item.data???
        return map
    }

    fun fromJson(json: String): ItemStack? {
        val map = json.toMap() ?: return null
        return fromMap(map)
    }

    fun fromMap(map: Map<String, Any>): ItemStack{
        val item = ItemStack(Material.getMaterial(map["type"] as String)!!)
        item.amount = map["amount"] as Int
        item.setItemMeta(ItemMetaSerializer.fromMap(map["meta"] as Map<String, Any>, item.itemMeta))

        return item
    }
}

private fun String.toMap(): HashMap<String, Any>? = Json.parse(this).fastCastTo()
private fun Map<*, *>.toMap(pretty: Boolean = false): String = Json.stringify(this, pretty)

fun ItemStack.serializeToJson(pretty: Boolean = false) = ItemStackSerializer.toJson(this, pretty)
fun String.deserializeFromJson() = ItemStackSerializer.fromJson(this)

fun ItemStack.serializeToMap() = ItemStackSerializer.toMap(this)
fun Map<String, Any>.deserializeFromMap() = ItemStackSerializer.fromMap(this)