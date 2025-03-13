package ru.oftendev.xbattlepass.battlepass

import com.willfp.eco.core.config.interfaces.Config
import ru.oftendev.xbattlepass.rewards.Rewards

class BPTier(val config: Config) {
    val number = config.getInt("tier")
    val rewards = config.getSubsections("rewards").map { BPTier(it) }
    val saveId = "bptier_$number"
}

class BPReward(val config: Config): Tiered {
    val reward = Rewards.getByID(config.getString("id"))
    override val tier = TierType.entries.first {
        it.name.equals(config.getString("tier"), true)
    }
}