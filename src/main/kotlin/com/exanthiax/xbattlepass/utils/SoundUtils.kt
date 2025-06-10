package com.exanthiax.xbattlepass.utils

import com.exanthiax.xbattlepass.plugin
import org.bukkit.Sound
import org.bukkit.entity.Player

object SoundUtils {
    fun playIfEnabled(player: Player, configPath: String) {
        if (plugin.configYml.getBool("$configPath.enabled")) {
            player.playSound(
                player.location,
                Sound.valueOf(plugin.configYml.getString("$configPath.sound").uppercase()),
                plugin.configYml.getDouble("$configPath.volume").toFloat(),
                plugin.configYml.getDouble("$configPath.pitch").toFloat()
            )
        }
    }
}