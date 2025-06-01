package com.exanthiax.xbattlepass.tasks

import com.exanthiax.xbattlepass.api.*
import com.exanthiax.xbattlepass.api.events.PlayerTaskExpGainEvent
import com.exanthiax.xbattlepass.plugin
import com.exanthiax.xbattlepass.quests.ActiveBattleQuest
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.formatWithCommas
import com.willfp.eco.util.toNiceString
import com.willfp.libreforge.counters.Accumulator
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ActiveBattleTask(val config: Config, val quest: ActiveBattleQuest) {
    val parent = BattleTasks.getByID(config.getString("id"))!!

    val requiredXP = config.getDoubleFromExpression("xp")

    val xpGainMethods = parent.xpGainMethods.map { it.clone() }

    val completedKey = PersistentDataKey(
        plugin.createNamespacedKey("${parent.id}_${quest.parent.id}_task_completed"),
        PersistentDataKeyType.BOOLEAN,
        false
    )

    val progressKey = PersistentDataKey(
        plugin.createNamespacedKey("${parent.id}_${quest.parent.id}_progress"),
        PersistentDataKeyType.DOUBLE,
        0.0
    )

    var isBound = false

    fun isActive(player: Player): Boolean {
        return !player.hasCompletedTask(this) && !player.hasCompletedQuest(this.quest) && this.quest.category
            .isActive && this.quest.parent.isAllowed(player, quest.category.battlepass)
    }

    private val accumulator = object : Accumulator {
        override fun accept(player: Player, count: Double) {
            if (!this@ActiveBattleTask.isActive(player) || !isBound) {
                return
            }

            this@ActiveBattleTask.gainExperience(player, count)
        }
    }

    private fun replaceBasicPlaceholders(input: String, player: Player): String {
        return input
            .replace("%task_name%", this.parent.name)
            .replace("%current_task_xp%", player.taskProgress(this).toNiceString())
            .replace("%current_task_xp_formatted%", player.taskProgress(this).formatWithCommas())
            .replace("%required_task_xp%", this.requiredXP.toNiceString())
            .replace("%required_task_xp_formatted%", this.requiredXP.formatWithCommas())
    }

    fun gainExperience(player: Player, count: Double) {
        val event = PlayerTaskExpGainEvent(player, this, count)
        Bukkit.getPluginManager().callEvent(event)

        if (!event.isCancelled) {
            player.giveTaskExperience(this, event.getAmount())
        }
    }

    fun reset(player: OfflinePlayer) {
        player.setCompletedTask(this, false)
        player.setTaskProgress(this, 0.0)
    }

    fun getDisplayItem(player: Player): ItemStack {
        return ItemStackBuilder(parent.testable)
            .setDisplayName(
                replaceBasicPlaceholders(parent.name, player)
                    .formatEco(formatPlaceholders = true)
            )
            .addLoreLines(
                parent.lore
                    .map { replaceBasicPlaceholders(it, player) }
                    .formatEco(formatPlaceholders = true)
            )
            .build()
    }

    fun getIconDescription(player: Player): List<String> {
        val result = mutableListOf<String>()
        val tasksFormat = plugin.configYml.getStrings("quests-icon.tasks-format")

        for (line in tasksFormat) {
            when {
                line.contains("%task_name%", ignoreCase = true) -> {
                    result.add(line.replace("%task_name%", replaceBasicPlaceholders(this.parent.name, player)))
                }
                line.contains("%task_lore%", ignoreCase = true) -> {
                    for (loreLine in this.parent.lore) {
                        result.add(line.replace("%task_lore%", replaceBasicPlaceholders(loreLine, player)))
                    }
                }
                else -> {
                    result.add(line)
                }
            }
        }

        return result.formatEco(player, true)
    }

    fun bind() {
        if (!isBound) {
            for (counter in xpGainMethods) {
                counter.bind(accumulator)
            }

            isBound = true
        }

    }

    fun unbind() {
        for (counter in xpGainMethods) {
            counter.unbind()
        }

        isBound = false
    }
}