package ru.oftendev.xbattlepass.commands

import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import ru.oftendev.xbattlepass.battlepass.BattlePasses
import ru.oftendev.xbattlepass.gui.CategoriesGUI
import ru.oftendev.xbattlepass.plugin

object QuestsCommand: PluginCommand(
    plugin,
    "quests",
    "xbattlepass.command.quests",
    true
) {
    override fun onExecute(sender: Player, args: MutableList<String>) {
        val passString = args.getOrNull(0) ?: run {
            sender.sendMessage(plugin.langYml.getMessage("pass-required"))
            return
        }

        val pass = BattlePasses.getByID(passString) ?: run {
            sender.sendMessage(plugin.langYml.getMessage("pass-not-found"))
            return
        }

        CategoriesGUI(sender, pass).open()
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        return when(args.size) {
            1 -> StringUtil.copyPartialMatches(args.first(), BattlePasses.values().map { it.id }, mutableListOf())
            else -> emptyList()
        }
    }
}