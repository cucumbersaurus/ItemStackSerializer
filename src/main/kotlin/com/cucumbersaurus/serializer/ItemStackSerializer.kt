package com.cucumbersaurus.serializer

import com.destroystokyo.paper.Namespaced
import korlibs.datastructure.fastCastTo
import korlibs.datastructure.iterators.fastForEach
import korlibs.io.serialization.json.Json
import korlibs.io.serialization.json.toJson
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

object ItemStackSerializer {
    fun toJson(item: ItemStack, pretty: Boolean = false): String{
        val map = toMap(item)
        return map.toJson(pretty)
    }

    private fun toMap(item: ItemStack): Map<String, Any>{
        val map  = HashMap<String, Any>()
        map["type"] = item.type.name
        map["amount"] = item.amount
        map["meta"] = ItemMetaSerializer.toMap(item.itemMeta)
        //item.data???
        return map
    }

    fun fromJson(json: String): ItemStack? {
        val map = json.toMap() ?: return null

        val item = ItemStack(Material.getMaterial(map["type"] as String)!!)
        item.amount = map["amount"] as Int
        item.setItemMeta(ItemMetaSerializer.fromMap(map["meta"] as Map<String, Any>, item.itemMeta))

        return item
    }
}

object ItemMetaSerializer {
    fun toMap(meta: ItemMeta): Map<String,Any> {
        val metaSerializer = MetaSerializer()
        return metaSerializer.toMap(meta)
    }

    fun fromMap(map: Map<String, Any>, meta: ItemMeta): ItemMeta{
        val metaSerializer = MetaSerializer()
        return metaSerializer.fromMap(map, meta)
    }
    private class MetaSerializer {
        private val map = HashMap<String, Any>()

        fun toMap(meta: ItemMeta): Map<String, Any> {
            //display name
            if (meta.hasDisplayName()) {
                map["displayName"] = ComponentSerializer.toJson(meta.displayName()!!)
            }
            //lore
            if (meta.hasLore()) {
                val lore = meta.lore()!!
                val loreList = ArrayList<String>(lore.size)
                lore.fastForEach {
                    loreList.add(ComponentSerializer.toJson(it))
                }
                map["lore"] = loreList
            }
            //custom model data
            if (meta.hasCustomModelData()) {
                map["customModelData"] = meta.customModelData
            }
            //enchants
            if (meta.hasEnchants()) {
                val enchants = meta.enchants
                val enchantMap =
                    HashMap<String, Int>(enchants.size) //namespaced key만 주의하면 될듯
                enchants.forEach { (enchant, level) ->
                    enchantMap[enchant.key.toString()] = level
                }
                map["enchants"] = enchantMap
            }
            //item flags
            if (meta.itemFlags.isNotEmpty()) {
                val flags = meta.itemFlags
                val flagList = ArrayList<String>(flags.size)
                flags.forEach {
                    flagList.add(it.toString())
                }
                map["itemFlags"] = flagList
            }
            //unbreakable
            map["unbreakable"] = meta.isUnbreakable
            //attribute modifiers
            if (meta.hasAttributeModifiers()) {
                val modifiers = meta.attributeModifiers!!
                val modifierList =
                    ArrayList<Map<String, Any>>(modifiers.size()) //attributeModifier 은 serialize 가 있네
                modifiers.forEach { key, data ->
                    modifierList.add(mapOf(Pair(key.name, data.serialize())))
                }
                map["attributeModifiers"] = modifierList
            }
            //destroyable
            if (meta.hasDestroyableKeys()) {
                val keySet = meta.destroyableKeys
                val keyList = ArrayList<String>(keySet.size)
                keySet.forEach {
                    keyList.add(it.namespace + ":" + it.key)
                }
                map["destroyableKeys"] = keyList
            }
            //placeable
            if (meta.hasPlaceableKeys()) {
                val keySet = meta.placeableKeys
                val keyList = ArrayList<String>(keySet.size)
                keySet.forEach {
                    keyList.add(it.namespace + ":" + it.key)
                }
                map["placeableKeys"] = keyList
            }
            //item meta 별로 추가적인 처리 필요
            return map
        }

        fun fromMap(map: Map<String, Any>, meta: ItemMeta): ItemMeta {
            //display name
            if (map["displayName"] != null){
                meta.displayName(ComponentSerializer.fromJson(map["displayName"] as String))
            }
            //lore
            if(map["lore"] != null){
                val loreList = map["lore"] as List<String>
                val lore = ArrayList<Component>(loreList.size)
                loreList.fastForEach {
                    lore.add(ComponentSerializer.fromJson(it))
                }
                meta.lore(lore)
            }
            //custom model data
            if (map["customModelData"] != null){
                meta.setCustomModelData(map["customModelData"] as Int)
            }
            //enchants
            if(map["enchants"] != null){
                val enchantMap = map["enchants"] as Map<String, Int>
                enchantMap.forEach { (key, level) ->
                    meta.addEnchant(Enchantment.getByKey(NamespacedKey.fromString(key))!!, level, true)
                }
            }
            //item flags
            if(map["itemFlags"] != null){
                val flagList = map["itemFlags"] as List<String>
                flagList.fastForEach {
                    meta.addItemFlags(ItemFlag.valueOf(it))
                }
            }
            //unbreakable
            if(map["unbreakable"] != null){
                meta.isUnbreakable = map["unbreakable"] as Boolean
            }
            //attribute modifiers
            if (map["attributeModifiers"] != null) {
                val modifierList = map["attributeModifiers"] as ArrayList<Map<String, Any>>
                modifierList.fastForEach {
                    it.forEach{ (k, data) ->
                        val attribute = Attribute.valueOf(k)
                        val modifier = AttributeModifier.deserialize((data as String).toMap()!!)
                        meta.addAttributeModifier(attribute, modifier)
                    }
                }
            }
            //destroyable
            if(map["destroyableKeys"] != null){
                val keyList = map["destroyableKeys"] as ArrayList<String>
                val keySet = HashSet<Namespaced>(keyList.size)
                keyList.fastForEach {
                    keySet.add(NamespacedKey.fromString(it)!!)
                }
                meta.setDestroyableKeys(keySet)
            }
            // placeable
            if (map["placeableKeys"] != null){
                val keyList = map["placeableKeys"] as ArrayList<String>
                val keySet = HashSet<Namespaced>(keyList.size)
                keyList.fastForEach {
                    keySet.add(NamespacedKey.fromString(it)!!)
                }
                meta.setPlaceableKeys(keySet)
            }
            //item meta 별로 추가적인 처리 필요
            return meta
        }
    }
}

object ComponentSerializer {
    fun toJson(component: Component): String {
        return JSONComponentSerializer.json().serialize(component)
    }

    fun fromJson(json: String): Component {
        return JSONComponentSerializer.json().deserialize(json)
    }
}

private fun String.toMap(): LinkedHashMap<String, Any>? = Json.parse(this).fastCastTo()
private fun Map<*, *>.toJson(pretty: Boolean = false): String = Json.stringify(this, pretty)

fun ItemStack.serializeToJson(pretty: Boolean = false   ) = ItemStackSerializer.toJson(this, pretty)
fun String.deserializeFromJson() = ItemStackSerializer.fromJson(this)