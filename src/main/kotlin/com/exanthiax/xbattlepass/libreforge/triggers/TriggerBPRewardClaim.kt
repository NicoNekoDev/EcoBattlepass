package com.exanthiax.xbattlepass.libreforge.triggers

import com.willfp.libreforge.toDispatcher
import com.willfp.libreforge.triggers.Trigger
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.event.EventHandler
import com.exanthiax.xbattlepass.api.events.PlayerRewardEvent
import com.exanthiax.xbattlepass.api.events.PlayerTaskCompleteEvent
import com.exanthiax.xbattlepass.api.events.PlayerTierLevelUpEvent

object TriggerBPRewardClaim: Trigger("claim_battlepass_reward") {
    override val parameters: Set<TriggerParameter> = setOf(
        TriggerParameter.PLAYER,
        TriggerParameter.EVENT
    )

    @EventHandler(ignoreCancelled = true)
    fun handleLevelUp(event: PlayerRewardEvent) {
        this.dispatch(
            event.player.toDispatcher(),
            TriggerData(
                dispatcher = event.player.toDispatcher(),
                player = event.player,
                text = event.reward.id,
                event = event,
                value = 1.0
            )
        )
    }
}