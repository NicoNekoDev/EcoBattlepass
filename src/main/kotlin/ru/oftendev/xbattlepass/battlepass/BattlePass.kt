package ru.oftendev.xbattlepass.battlepass

import com.willfp.eco.core.data.keys.PersistentDataKey
import com.willfp.eco.core.data.keys.PersistentDataKeyType
import ru.oftendev.xbattlepass.plugin

object BattlePass {
    val tierKey = PersistentDataKey(
        plugin.createNamespacedKey("bp_tier"),
        PersistentDataKeyType.INT, 0
    )


}