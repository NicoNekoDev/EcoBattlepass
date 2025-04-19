package com.exanthiax.xbattlepass.tiers

import org.bukkit.entity.Player
import com.exanthiax.xbattlepass.api.hasPremium
import com.exanthiax.xbattlepass.battlepass.BattlePass
import com.exanthiax.xbattlepass.plugin

interface Tiered {
    val tier: TierType

    fun isAllowed(player: Player, pass: BattlePass): Boolean {
        return when (tier) {
            TierType.FREE -> true
            TierType.PREMIUM -> player.hasPremium(pass)
        }
    }

    val formattedName: String
        get() = plugin.langYml.getFormattedString(this.tier.name.lowercase())
}