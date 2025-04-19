package com.exanthiax.xbattlepass.listeners

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import com.exanthiax.xbattlepass.ConfiguredSound
import com.exanthiax.xbattlepass.api.events.PlayerBPExpGainEvent
import com.exanthiax.xbattlepass.api.events.PlayerQuestCompleteEvent
import com.exanthiax.xbattlepass.api.events.PlayerRewardEvent
import com.exanthiax.xbattlepass.api.events.PlayerTierLevelUpEvent
import com.exanthiax.xbattlepass.api.getTier
import com.exanthiax.xbattlepass.api.giveBPExperience
import com.exanthiax.xbattlepass.plugin

object BattlePassListener: Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun handleBPLevelUp(event: PlayerTierLevelUpEvent) {
        if (event.player.getTier(event.battlepass) >= event.battlepass.maxLevel) {
            event.isCancelled = true
            return
        }

        event.player.sendMessage(
            plugin.langYml.getMessage("tier-up").replace(
                "%tier%", event.level.toString()
            )
        )

        ConfiguredSound(
            plugin.configYml.getSubsection("sound.tier-up")
        ).play(event.player)

        // val tier = BattlePass.getTier(event.level) ?: return

        // event.player.receiveTier(tier)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun handleBPExp(event: PlayerBPExpGainEvent) {
        if (event.player.getTier(event.battlepass) >= event.battlepass.maxLevel) {
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun handleQuest(event: PlayerQuestCompleteEvent) {
        event.player.sendMessage(
            plugin.langYml.getMessage("quest-complete").replace(
                "%quest%", event.quest.getFormattedName(event.player)
            )
        )

        ConfiguredSound(
            plugin.configYml.getSubsection("sound.quest-complete")
        ).play(event.player)

        event.player.giveBPExperience(
            event.quest.category.battlepass,
            event.quest.parent.tierPoints.toDouble(),
            true
        )
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun handleReward(event: PlayerRewardEvent) {
        event.player.sendMessage(
            plugin.langYml.getMessage("reward-claim").replace(
                "%reward%", event.reward.getDisplayName(event.player)
            )
        )

        ConfiguredSound(
            plugin.configYml.getSubsection("sound.reward-claim")
        ).play(event.player)
    }
}