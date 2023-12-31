package com.cucumbersaurus.serializer

import org.bukkit.plugin.java.JavaPlugin

class SerializerPlugin : JavaPlugin() {
    override fun onLoad() {
        logger.info("loading serializer")
    }
    override fun onEnable() {
        getCommand("serialize_test")!!.setExecutor(SerializeTestCommand())
    }
}