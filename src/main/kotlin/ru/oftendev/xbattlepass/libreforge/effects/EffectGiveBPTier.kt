package ru.oftendev.xbattlepass.libreforge.effects

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.libreforge.ConfigArguments
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.triggers.TriggerData
import com.willfp.libreforge.triggers.TriggerParameter
import org.bukkit.Bukkit
import ru.oftendev.xbattlepass.api.*
import ru.oftendev.xbattlepass.api.events.PlayerTierLevelUpEvent
import ru.oftendev.xbattlepass.battlepass.BattlePasses

object EffectGiveBPTier: Effect<NoCompileData>("give_battlepass_tiers") {
    override val arguments: ConfigArguments = arguments {
        require("tiers", "You must specify the amount of tiers to give!")
        require("battlepass",
            "You must specify a battlepass to check premium in!",
            {passId -> BattlePasses.getByID(passId)},
            {battlepass -> battlepass != null}
        )
    }

    override val parameters: Set<TriggerParameter> = setOf(TriggerParameter.PLAYER)

    override fun onTrigger(config: Config, data: TriggerData, compileData: NoCompileData): Boolean {
        val player = data.player ?: return false
        val amount = config.getIntFromExpression("amount", player)
        val pass = BattlePasses.getByID(config.getString("battlepass")) ?: return false

        val event = PlayerTierLevelUpEvent(player, pass, player.getTier(pass) + amount)

        Bukkit.getPluginManager().callEvent(event)

        if (!event.isCancelled) {
            player.setTier(pass, player.getTier(pass) + amount)
            return true
        }

        return false
    }
}