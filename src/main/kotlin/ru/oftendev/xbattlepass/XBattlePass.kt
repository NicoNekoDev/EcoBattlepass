package ru.oftendev.xbattlepass

import com.willfp.eco.core.command.impl.PluginCommand
import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType
import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.toNiceString
import com.willfp.libreforge.conditions.Conditions
import com.willfp.libreforge.effects.Effects
import com.willfp.libreforge.filters.Filters
import com.willfp.libreforge.loader.LibreforgePlugin
import com.willfp.libreforge.loader.configs.ConfigCategory
import com.willfp.libreforge.triggers.Triggers
import net.kyori.adventure.key.Key
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import ru.oftendev.xbattlepass.battlepass.BattlePasses
import ru.oftendev.xbattlepass.battlepass.BattlePasses.updateTaskBindings
import ru.oftendev.xbattlepass.categories.Categories
import ru.oftendev.xbattlepass.commands.XBattlePassCommand
import ru.oftendev.xbattlepass.libreforge.conditions.ConditionHasBPPremium
import ru.oftendev.xbattlepass.libreforge.conditions.ConditionHasBPTier
import ru.oftendev.xbattlepass.libreforge.effects.*
import ru.oftendev.xbattlepass.libreforge.filters.FilterReward
import ru.oftendev.xbattlepass.libreforge.filters.FilterTask
import ru.oftendev.xbattlepass.libreforge.triggers.TriggerBPExpGain
import ru.oftendev.xbattlepass.libreforge.triggers.TriggerBPRewardClaim
import ru.oftendev.xbattlepass.libreforge.triggers.TriggerBPTaskComplete
import ru.oftendev.xbattlepass.libreforge.triggers.TriggerBPTierUp
import ru.oftendev.xbattlepass.listeners.BattlePassListener
import ru.oftendev.xbattlepass.quests.BattleQuests
import ru.oftendev.xbattlepass.rewards.Rewards
import ru.oftendev.xbattlepass.tasks.BattleTasks

lateinit var plugin: XBattlePass
    private set

class XBattlePass: LibreforgePlugin() {
    init {
        plugin = this
        this.configHandler.addConfig(
            object: BaseConfig(
                "categories",
                this,
                false,
                ConfigType.YAML
            ) {}
        )
    }

    override fun loadListeners(): MutableList<Listener> {
        return mutableListOf(
            BattlePassListener
        )
    }

    override fun loadPluginCommands(): MutableList<PluginCommand> {
        return mutableListOf(
            XBattlePassCommand
        )
    }

    override fun loadConfigCategories(): List<ConfigCategory> {
        return mutableListOf(
            Rewards,
            BattleTasks,
            BattleQuests,
            BattlePasses,
            Categories
        )
    }

    override fun handleEnable() {
        BattlePasses.updateTaskBindings()

        // Libreforge register
        Effects.register(EffectBPExpMultiplier)
        Effects.register(EffectGiveBPExp)
        Effects.register(EffectGiveBPTier)
        Effects.register(EffectGiveTaskExp)
        Effects.register(EffectSetBPTier)
        Effects.register(EffectTaskExpMultiplier)

        Conditions.register(ConditionHasBPTier)
        Conditions.register(ConditionHasBPPremium)

        Filters.register(FilterReward)
        Filters.register(FilterTask)

        Triggers.register(TriggerBPExpGain)
        Triggers.register(TriggerBPRewardClaim)
        Triggers.register(TriggerBPTaskComplete)
        Triggers.register(TriggerBPTierUp)
    }

    override fun handleReload() {
        // BattlePassLegacy.update()
        BattlePasses.updateTaskBindings()
    }

    override fun createTasks() {
        this.scheduler.runAsyncTimer(1L, 100L) {
            Categories.values().forEach { category -> if (category.isToReset()) category.reset() }
            BattlePasses.tickUpdates()
        }
    }
}

fun msToString(ms: Long): String {
    // Define constants
    val secondsPerMs = 0.001
    val secondsInMinute = 60
    val secondsInHour = 3600
    val secondsInDay = 86400

    // Convert ticks to total seconds
    val totalSeconds = ms * secondsPerMs

    // Calculate days, hours, minutes, and seconds
    val days = (totalSeconds / secondsInDay).toInt()
    val hours = ((totalSeconds % secondsInDay) / secondsInHour).toInt()
    val minutes = ((totalSeconds % secondsInHour) / secondsInMinute).toInt()
    val seconds = (totalSeconds % secondsInMinute).toInt()

    val lst = mutableListOf<String>()

    if (days > 0) {
        lst += plugin.configYml.getFormattedString("time-format.days")
            .replace("%value%", days.toNiceString())
    }
    if (hours > 0) {
        lst += plugin.configYml.getFormattedString("time-format.hours")
            .replace("%value%", hours.toNiceString())
    }
    if (minutes > 0) {
        lst += plugin.configYml.getFormattedString("time-format.minutes")
            .replace("%value%", minutes.toNiceString())
    }

    lst += plugin.configYml.getFormattedString("time-format.seconds")
        .replace("%value%", seconds.toNiceString())

    // Format the result as a string
    return lst.joinToString(plugin.configYml.getFormattedString("time-format.split"))
}

class ConfiguredSound(private val sound: net.kyori.adventure.sound.Sound, private val enabled: Boolean = true) {
    constructor(from: Config) : this(
        net.kyori.adventure.sound.Sound.sound(
        Key.key(from.getString("name")), net.kyori.adventure.sound.Sound.Source.AMBIENT,
        from.getDouble("volume").toFloat(), from.getDouble("pitch").toFloat()),
        from.getBool("enabled"))

    fun play(player: Player) {
        if (enabled) player.playSound(sound)
    }
}