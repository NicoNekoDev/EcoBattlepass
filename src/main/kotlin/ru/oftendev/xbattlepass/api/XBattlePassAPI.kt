package ru.oftendev.xbattlepass.api

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import com.willfp.eco.core.data.profile
import org.bukkit.entity.Player
import ru.oftendev.xbattlepass.battlepass.BPTier
import ru.oftendev.xbattlepass.plugin

var premiumPermission = plugin.battlePassYml.getString("battlepass.premium-permission")

fun updatePremiumPermission() {
    premiumPermission = plugin.battlePassYml.getString("battlepass.premium-permission")
}

val bpTierKey = PersistentDataKey(
    plugin.createNamespacedKey("bp_tier"),
    PersistentDataKeyType.INT, 0
)

val receivedTiersKey = PersistentDataKey(
    plugin.createNamespacedKey("bp_tiers_received"),
    PersistentDataKeyType.STRING_LIST, emptyList()
)

var Player.bpTier: Int
    get() = this.profile.read(bpTierKey)
    set(value) = this.profile.write(bpTierKey, value)

var Player.receivedTiers: List<String>
    get() = this.profile.read(receivedTiersKey)
    set(value) = this.profile.write(receivedTiersKey, value.distinct())

val Player.hasPremium: Boolean
    get() = this.hasPermission(premiumPermission)

fun Player.receivedTier(tier: BPTier) {
    tier.rewards.forEach {  }
}