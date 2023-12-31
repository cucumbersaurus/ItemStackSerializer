package com.cucumbersaurus.serializer

import net.md_5.bungee.api.chat.hover.content.Item
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class SerializeTestCommand:CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>?): Boolean {
        if(args!=null && args.isNotEmpty()){
            val json = args.joinToString(separator = " ")
            val item = ItemStackSerializer.fromJson(json)!!
            Bukkit.getOnlinePlayers().forEach {
                it.inventory.addItem(item)
            }
        }
        else
            if(sender is Player) {

                val item = sender.inventory.itemInMainHand
                val json = item.serializeToJson()
                Bukkit.getLogger().info(json)

                val newItem = ItemStackSerializer.fromJson(json)!!
                sender.inventory.addItem(newItem)
            }
        return true
    }
}