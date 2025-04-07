package ru.oftendev.xbattlepass.commands.dynamic

import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.entity.Player
import ru.oftendev.xbattlepass.battlepass.BattlePass
import ru.oftendev.xbattlepass.gui.BattlePassGUI
import ru.oftendev.xbattlepass.plugin

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