package com.exanthiax.xbattlepass.commands

import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.command.CommandSender
import com.exanthiax.xbattlepass.plugin

object XBattlePassCommand: PluginCommand(
    plugin,
    "xbattlepass",
    "xbattlepass.command.xbattlepass",
    false
) {
    init {
        this.addSubcommand(
            com.exanthiax.xbattlepass.commands.QuestsCommand
        ).addSubcommand(
            com.exanthiax.xbattlepass.commands.ReloadCommand
        ).addSubcommand(
            com.exanthiax.xbattlepass.commands.ResetCommand
        ).addSubcommand(
            com.exanthiax.xbattlepass.commands.GiveCommand
        ).addSubcommand(
            com.exanthiax.xbattlepass.commands.TiersCommand
        )
    }

    override fun onExecute(sender: CommandSender, args: MutableList<String>) {
        sender.sendMessage(plugin.langYml.getMessage("invalid-command"))
    }
}