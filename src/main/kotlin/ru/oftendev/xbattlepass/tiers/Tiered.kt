package ru.oftendev.xbattlepass.tiers

import org.bukkit.entity.Player
import ru.oftendev.xbattlepass.api.hasPremium
import ru.oftendev.xbattlepass.battlepass.BattlePass
import ru.oftendev.xbattlepass.plugin

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