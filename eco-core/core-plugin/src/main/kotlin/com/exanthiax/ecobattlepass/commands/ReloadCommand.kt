package com.exanthiax.ecobattlepass.commands

import com.exanthiax.ecobattlepass.plugin
import com.willfp.eco.core.Prerequisite
import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.util.NumberUtils
import com.willfp.eco.util.StringUtils
import org.bukkit.command.CommandSender

object ReloadCommand : PluginCommand(
    plugin,
    "reload",
    "ecobattlepass.command.reload",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val runnable = Runnable {
            sender.sendMessage(
                plugin.langYml.getMessage("reloaded", StringUtils.FormatOption.WITHOUT_PLACEHOLDERS)
                    .replace("%time%", NumberUtils.format(plugin.reloadWithTime().toDouble()))
            )
        }
        if (Prerequisite.HAS_FOLIA.isMet) {
            plugin.scheduler.runTask(runnable)
        } else {
            runnable.run()
        }
    }
}