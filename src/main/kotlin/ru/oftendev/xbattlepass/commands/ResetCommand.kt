package ru.oftendev.xbattlepass.commands

import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil
import ru.oftendev.xbattlepass.battlepass.BattlePasses
import ru.oftendev.xbattlepass.plugin

object ResetCommand: PluginCommand(
    plugin,
    "reset",
    "xbattlepass.command.reset",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val playerString = args.firstOrNull() ?: run {
            sender.sendMessage(plugin.langYml.getMessage("player-required"))
            return
        }

        val player = Bukkit.getPlayer(playerString) ?: run {
            sender.sendMessage(plugin.langYml.getMessage("player-not-found"))
            return
        }

        val passString = args.getOrNull(1) ?: run {
            sender.sendMessage(plugin.langYml.getMessage("pass-required"))
            return
        }

        val pass = BattlePasses.getByID(passString) ?: run {
            sender.sendMessage(plugin.langYml.getMessage("pass-not-found"))
            return
        }

        pass.reset(player)

        sender.sendMessage(plugin.langYml.getMessage("reset-player")
            .replace("%playername%", player.name)
            .replace("%pass%", pass.name)
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        return when(args.size) {
            1 -> StringUtil.copyPartialMatches(args.first(), Bukkit.getOnlinePlayers().map { it.name }, mutableListOf())
            2 -> StringUtil.copyPartialMatches(args.first(), BattlePasses.values().map { it.id }, mutableListOf())
            else -> emptyList()
        }
    }
}