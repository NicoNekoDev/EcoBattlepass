package ru.oftendev.xbattlepass

import com.willfp.eco.core.config.BaseConfig
import com.willfp.eco.core.config.ConfigType
import com.willfp.libreforge.loader.LibreforgePlugin
import ru.oftendev.xbattlepass.api.updatePremiumPermission

lateinit var plugin: XBattlePass
    private set

class XBattlePass: LibreforgePlugin() {
    val battlePassYml = BattlePassYml(this)

    init {
        plugin = this
        this.configHandler.addConfig(battlePassYml)
    }

    override fun handleEnable() {
        updatePremiumPermission()
    }

    override fun handleReload() {
        updatePremiumPermission()
    }
}

class BattlePassYml(plugin: LibreforgePlugin): BaseConfig(
    "battlepasss",
    plugin,
    true,
    ConfigType.YAML
)