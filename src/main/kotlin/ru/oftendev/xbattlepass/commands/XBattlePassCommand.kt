package ru.oftendev.xbattlepass.commands

import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.command.CommandSender
import ru.oftendev.xbattlepass.plugin

object XBattlePassCommand: PluginCommand(
    plugin,
    "xbattlepass",
    "xbattlepass.command.xbattlepass",
    false
) {
    init {
        this.addSubcommand(
            QuestsCommand
        ).addSubcommand(
            ReloadCommand
        ).addSubcommand(
            ResetCommand
        ).addSubcommand(
            GiveCommand
        ).addSubcommand(
            TiersCommand
        )
    }

    override fun onExecute(sender: CommandSender, args: MutableList<String>) {
        sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
    }
}