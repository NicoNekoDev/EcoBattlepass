package com.exanthiax.xbattlepass.commands.dynamic

import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.entity.Player
import com.exanthiax.xbattlepass.battlepass.BattlePass
import com.exanthiax.xbattlepass.gui.BattlePassGUI
import com.exanthiax.xbattlepass.plugin

class DynamicPassCommand(val pass: BattlePass, cmd: String): PluginCommand(
    plugin,
    cmd,
    "xbattlepass.command.$cmd",
    true
) {
    override fun onExecute(sender: Player, args: MutableList<String>) {
        BattlePassGUI.createAndOpen(sender, pass)
    }
}