package com.cucumbersaurus.serializer

import com.destroystokyo.paper.Namespaced
import com.destroystokyo.paper.inventory.meta.ArmorStandMeta
import korlibs.datastructure.fastCastTo
import korlibs.datastructure.iterators.fastForEach
import korlibs.io.serialization.json.Json
import korlibs.io.serialization.json.toJson
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.*
import org.bukkit.potion.PotionData
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionType
import java.util.*
import kotlin.collections.HashMap

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

    fun toMap(meta: ItemMeta): Map<String, Any> {
        val map = HashMap<String, Any>()
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
        return specialMetaToMap(map, meta)

    }

    fun specialMetaToMap(map: HashMap<String, Any>, meta: ItemMeta): Map<String, Any> {
        if (meta is ArmorMeta) {
            if (meta is ColorableArmorMeta) {

            } else {

            }
        }
        if (meta is ArmorStandMeta) {

        }
        if (meta is AxolotlBucketMeta) {

        }
        if (meta is BannerMeta) {

        }
        if (meta is BlockDataMeta) {

        }
        if (meta is BlockStateMeta) {

        }
        if (meta is BookMeta) {

        }
        if (meta is BundleMeta) {

        }
        // ColorableArmorMeta 는 armor 과 leather armor 아래로 이동
        if (meta is CompassMeta) {

        }
        if (meta is CrossbowMeta) {

        }
        if (meta is Damageable) {

        }
        if (meta is EnchantmentStorageMeta) {

        }
        if (meta is FireworkEffectMeta) {

        }
        if (meta is FireworkMeta) {

        }
        if (meta is KnowledgeBookMeta) {

        }
        if (meta is LeatherArmorMeta) {
            if (meta is ColorableArmorMeta) {

            } else {

            }
        }
        if (meta is MapMeta) {

        }
        if (meta is MusicInstrumentMeta) {

        }
        if (meta is PotionMeta) {
            Bukkit.broadcastMessage("potion")
            //potion data
            map["potionMeta_potionData"] = PotionInfoSerializer.PotionDataSerializer.toMap(meta.basePotionData)
            //custom effects
            if (meta.hasCustomEffects()) {
                val effects = meta.customEffects
                val effectList = ArrayList<Map<String, Any>>(meta.customEffects.size)
                effects.forEach {
                    effectList.add(PotionInfoSerializer.PotionEffectSerializer.toMap(it))
                }
                map["potionMeta_customEffects"] = effectList
            }
            //color
            if (meta.hasColor()) {
                map["potionMeta_color"] = ColorSerializer.toHexARGB(meta.color!!)
            }
        }
        if (meta is Repairable) {

        }
        if (meta is SkullMeta) {

        }
        if (meta is SpawnEggMeta) {

        }
        if (meta is SuspiciousStewMeta) {

        }
        if (meta is TropicalFishBucketMeta) {

        }
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
                    val modifier = AttributeModifier.deserialize(data as Map<String, Any> )
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
        return mapToSpecialMeta(map, meta)
    }

    fun mapToSpecialMeta(map: Map<String, Any>, meta: ItemMeta): ItemMeta {
        if(meta is ArmorMeta) {
            if(meta is ColorableArmorMeta){

            }
            else{

            }
        }
        if(meta is ArmorStandMeta)  {

        }
        if(meta is AxolotlBucketMeta) {

        }
        if(meta is BannerMeta) {

        }
        if(meta is BlockDataMeta)  {

        }
        if(meta is BlockStateMeta)  {

        }
        if(meta is BookMeta)  {

        }
        if(meta is BundleMeta)  {

        }
        // ColorableArmorMeta 는 armor 과 leather armor 아래로 이동
        if(meta is CompassMeta)  {

        }
        if(meta is CrossbowMeta)  {

        }
        if(meta is Damageable)  {

        }
        if(meta is EnchantmentStorageMeta)  {

        }
        if(meta is FireworkEffectMeta)  {

        }
        if(meta is FireworkMeta)  {

        }
        if(meta is KnowledgeBookMeta)  {

        }
        if(meta is LeatherArmorMeta)  {
            if(meta is ColorableArmorMeta){

            }
            else{

            }
        }
        if(meta is MapMeta)  {

        }
        if(meta is MusicInstrumentMeta)  {

        }
        if(meta is PotionMeta)  {
            //potion data
            if (map["potionMeta_potionData"] != null) {
                meta.basePotionData =
                    PotionInfoSerializer.PotionDataSerializer.fromMap(map["potionMeta_potionData"] as Map<String, Any>)
            }
            //custom effects
            if (map["potionMeta_customEffects"] != null) {
                val effectList = map["potionMeta_customEffects"] as List<Map<String, Any>>
                effectList.fastForEach {
                    meta.addCustomEffect(PotionInfoSerializer.PotionEffectSerializer.fromMap(it), false)
                }
            }
            //color
            if (map["potionMeta_color"] != null) {
                meta.color = ColorSerializer.fromHexARGB(map["potionMeta_color"] as String)
            }
        }
        if(meta is Repairable)  {

        }
        if(meta is SkullMeta)  {

        }
        if(meta is SpawnEggMeta)  {

        }
        if(meta is SuspiciousStewMeta)  {

        }
        if(meta is TropicalFishBucketMeta)  {

            }
        return meta
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

object PotionInfoSerializer {
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

        fun toMap(effect: PotionEffect): Map<String, Any> {
//            val map = HashMap<String, Any>()
//            map["amplifier"] = effect.amplifier
//            map["duration"] = effect.duration
//            map["type"] = effect.type.name
//            map["isAmbient"] = effect.isAmbient
//            return map
            return effect.serialize()
        }

        fun fromMap(map: Map<String, Any>): PotionEffect {
            return PotionEffect(map)
        }
    }
}

object ColorSerializer{
    fun toHexARGB(col: Color):String{
        return Integer.toHexString(col.asARGB()).uppercase(Locale.getDefault())
    }

    fun fromHexARGB(argb: String):Color{
        val argbList = argb.chunked(2).map { it.toInt(16) }
        return Color.fromARGB(argbList[0], argbList[1], argbList[2], argbList[3])
    }
}

private fun String.toMap(): LinkedHashMap<String, Any>? = Json.parse(this).fastCastTo()
private fun Map<*, *>.toMap(pretty: Boolean = false): String = Json.stringify(this, pretty)

fun ItemStack.serializeToJson(pretty: Boolean = false   ) = ItemStackSerializer.toJson(this, pretty)
fun String.deserializeFromJson() = ItemStackSerializer.fromJson(this)