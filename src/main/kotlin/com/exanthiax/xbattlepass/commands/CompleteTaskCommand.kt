package com.exanthiax.xbattlepass.commands

import com.exanthiax.xbattlepass.api.checkCompletedQuest
import com.exanthiax.xbattlepass.api.setCompletedTask
import com.willfp.eco.core.command.impl.PluginCommand
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.util.StringUtil
import com.exanthiax.xbattlepass.battlepass.BattlePasses
import com.exanthiax.xbattlepass.categories.Categories
import com.exanthiax.xbattlepass.plugin

object CompleteTaskCommand : PluginCommand(
    plugin,
    "complete_task",
    "xbattlepass.command.completetask",
    false
) {
    override fun onExecute(sender: CommandSender, args: List<String>) {
        val playerString = args.getOrNull(0) ?: run {
            sender.sendMessage(plugin.langYml.getMessage("player-required"))
            return
        }

        val passString = args.getOrNull(1) ?: run {
            sender.sendMessage(plugin.langYml.getMessage("pass-required"))
            return
        }

        val categoryString = args.getOrNull(2) ?: run {
            sender.sendMessage(plugin.langYml.getMessage("category-required"))
            return
        }

        val questString = args.getOrNull(3) ?: run {
            sender.sendMessage(plugin.langYml.getMessage("quest-required"))
            return
        }

        val taskString = args.getOrNull(4) ?: run {
            sender.sendMessage(plugin.langYml.getMessage("task-required"))
            return
        }

        val pass = BattlePasses.getByID(passString) ?: run {
            sender.sendMessage(plugin.langYml.getMessage("pass-not-found"))
            return
        }

        val category = Categories.getByID(categoryString) ?: run {
            sender.sendMessage(plugin.langYml.getMessage("invalid-category"))
            return
        }

        if (category.battlepass != pass) {
            sender.sendMessage(plugin.langYml.getMessage("invalid-category"))
            return
        }

        val activeQuest = category.quests.find { it.parent.id.equals(questString, true) } ?: run {
            sender.sendMessage(plugin.langYml.getMessage("invalid-quest"))
            return
        }

        val activeTask = activeQuest.tasks.find { it.parent.id.equals(taskString, true) } ?: run {
            sender.sendMessage(plugin.langYml.getMessage("invalid-task"))
            return
        }

        val isAll = playerString.equals("all", ignoreCase = true)
        val players = if (isAll) {
            Bukkit.getOnlinePlayers().toList()
        } else {
            val p = Bukkit.getPlayer(playerString) ?: run {
                sender.sendMessage(plugin.langYml.getMessage("player-not-found"))
                return
            }
            listOf(p)
        }

        for (player in players) {
            player.setCompletedTask(activeTask, true)
            player.checkCompletedQuest(activeTask)
        }

        val taskName = activeTask.parent.name

        sender.sendMessage(
            plugin.langYml.getMessage("completed-task")
                .replace("%playername%", if (isAll) "all players" else players.first().name)
                .replace("%task%", taskName)
                .replace("%pass%", pass.name)
        )
    }

    override fun tabComplete(sender: CommandSender, args: List<String>): List<String> {
        return when (args.size) {
            1 -> StringUtil.copyPartialMatches(args[0], Bukkit.getOnlinePlayers().map { it.name } + "all", mutableListOf())
            2 -> StringUtil.copyPartialMatches(args[1], BattlePasses.values().map { it.id }, mutableListOf())
            3 -> Categories.values().map { it.id }
            4 -> {
                val cat = Categories.getByID(args[2]) ?: return emptyList()
                cat.quests.map { it.parent.id }
            }
            5 -> {
                val cat = Categories.getByID(args[2]) ?: return emptyList()
                val quest = cat.quests.find { it.parent.id.equals(args[3], true) } ?: return emptyList()
                quest.tasks.map { it.parent.id }
            }
            else -> emptyList()
        }.let { StringUtil.copyPartialMatches(args.last(), it, mutableListOf()) }
    }
}