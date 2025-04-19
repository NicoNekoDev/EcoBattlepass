package com.exanthiax.xbattlepass.gui

import com.github.benmanes.caffeine.cache.Caffeine
import com.willfp.eco.core.EcoPlugin
import com.willfp.eco.core.gui.menu
import com.willfp.eco.core.gui.menu.Menu
import com.willfp.eco.core.gui.menu.MenuLayer
import com.willfp.eco.core.gui.page.PageChanger
import com.willfp.eco.core.gui.slot
import com.willfp.eco.core.gui.slot.ConfigSlot
import com.willfp.eco.core.gui.slot.FillerMask
import com.willfp.eco.core.gui.slot.MaskItems
import com.willfp.eco.core.items.Items
import com.willfp.eco.core.items.builder.ItemStackBuilder
import com.willfp.eco.core.map.nestedMap
import com.willfp.eco.core.placeholder.context.placeholderContext
import com.willfp.eco.util.NumberUtils.evaluateExpression
import com.willfp.eco.util.formatEco
import com.willfp.eco.util.openMenu
import com.willfp.ecomponent.components.LevelState
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import com.exanthiax.xbattlepass.api.getTier
import com.exanthiax.xbattlepass.api.hasReceivedTier
import com.exanthiax.xbattlepass.api.receiveTier
import com.exanthiax.xbattlepass.battlepass.BattlePass
import com.exanthiax.xbattlepass.gui.components.ProperLevelComponent
import com.exanthiax.xbattlepass.plugin
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

object BattleTiersGUI {
    fun createAndOpen(player: Player, pass: BattlePass, backButton: Boolean = false) {
        val maskPattern = plugin.configYml.getStrings("tiers-gui.mask.pattern").toTypedArray()
        val maskItems = MaskItems.fromItemNames(plugin.configYml.getStrings("tiers-gui.mask.materials"))

        val levelComponent = BattleTierComponent(plugin, pass)

        val menu = menu(maskPattern.size) {
            title = plugin.configYml.getString("tiers-gui.title")
                .replace("%pass%", pass.name)
                .formatEco()

            maxPages(levelComponent.pages)

            setMask(
                FillerMask(
                    maskItems,
                    *maskPattern
                )
            )

            addComponent(1, 1, levelComponent)

            defaultPage {
                levelComponent.getPageOf(it.getTier(pass)).coerceAtLeast(1)
            }

            // Instead of the page changer, this will show up when on the first page
            if (backButton) {
                addComponent(
                    MenuLayer.LOWER,
                    plugin.configYml.getInt("tiers-gui.buttons.prev-page.location.row"),
                    plugin.configYml.getInt("tiers-gui.buttons.prev-page.location.column"),
                    slot(
                        ItemStackBuilder(Items.lookup(plugin.configYml.getString("tiers-gui.buttons.prev-page.material")))
                            .setDisplayName(plugin.configYml.getString("tiers-gui.buttons.prev-page.name"))
                            .build()
                    ) {
                        onLeftClick { _, _ -> BattlePassGUI.createAndOpen(player, pass) }
                    }
                )
            }

            addComponent(
                plugin.configYml.getInt("tiers-gui.buttons.prev-page.location.row"),
                plugin.configYml.getInt("tiers-gui.buttons.prev-page.location.column"),
                PageChanger(
                    ItemStackBuilder(Items.lookup(plugin.configYml.getString("tiers-gui.buttons.prev-page.material")))
                        .setDisplayName(plugin.configYml.getString("tiers-gui.buttons.prev-page.name"))
                        .build(),
                    PageChanger.Direction.BACKWARDS
                )
            )

            addComponent(
                plugin.configYml.getInt("tiers-gui.buttons.next-page.location.row"),
                plugin.configYml.getInt("tiers-gui.buttons.next-page.location.column"),
                PageChanger(
                    ItemStackBuilder(Items.lookup(plugin.configYml.getString("tiers-gui.buttons.next-page.material")))
                        .setDisplayName(plugin.configYml.getString("tiers-gui.buttons.next-page.name"))
                        .build(),
                    PageChanger.Direction.FORWARDS
                )
            )

            setSlot(
                plugin.configYml.getInt("tiers-gui.buttons.close.location.row"),
                plugin.configYml.getInt("tiers-gui.buttons.close.location.column"),
                slot(
                    ItemStackBuilder(Items.lookup(plugin.configYml.getString("tiers-gui.buttons.close.material")))
                        .setDisplayName(plugin.configYml.getString("tiers-gui.buttons.close.name"))
                        .build()
                ) {
                    onLeftClick { event, _ ->
                        event.whoClicked.closeInventory()
                    }
                }
            )

            for (config in plugin.configYml.getSubsections("tiers-gui.buttons.custom-slots")) {
                setSlot(
                    config.getInt("row"),
                    config.getInt("column"),
                    ConfigSlot(config)
                )
            }
        }

        menu.open(player)
    }
}

private val levelItemCache = Caffeine.newBuilder()
    .expireAfterWrite(plugin.configYml.getInt("gui-cache-ttl").toLong(), TimeUnit.MILLISECONDS)
    .build<Int, ItemStack>()

class BattleTierComponent(
    private val plugin: EcoPlugin,
    private val pass: BattlePass
) : ProperLevelComponent() {
    override val pattern: List<String> = plugin.configYml.getStrings("tiers-gui.mask.progression-pattern")
    override val maxLevel = pass.maxLevel

    private val itemCache = nestedMap<LevelState, Int, ItemStack>()

    override fun getLevelItem(player: Player, menu: Menu, level: Int, levelState: LevelState): ItemStack {
        val key = run {
            if (levelState == LevelState.UNLOCKED && player.hasReceivedTier(pass, level)) {
                "claimed"
            } else levelState.key
        }

        // plugin.logger.info("Level $level, $key")

        fun item() = levelItemCache.get(player.hashCode() xor level.hashCode()) {
            val tier = pass.getTier(level)!!
            
            ItemStackBuilder(Items.lookup(plugin.configYml.getString("tiers-gui.buttons.$key.item")))
                .setDisplayName(
                    plugin.configYml.getString("tiers-gui.buttons.$key.name")
                        .let { tier.format(it, player) }
                )
                .addLoreLines(
                    tier.format(
                        plugin.configYml.getStrings("tiers-gui.buttons.$key.lore"),
                        player,
                    )/*.lineWrap(plugin.configYml.getInt("gui.skill-icon.line-wrap"))*/
                )
                .setAmount(
                    evaluateExpression(
                        plugin.configYml.getString("tiers-gui.buttons.item-amount")
                            .replace("%level%", level.toString()),
                        placeholderContext(
                            player = player
                        )
                    ).roundToInt()
                )
                .build()
        }

        return if (levelState != LevelState.IN_PROGRESS) {
            itemCache[levelState].getOrPut(level) { item() }
        } else {
            item()
        }.apply {
            // "Slot $level item $this"
        }
    }

    override fun getLevelState(player: Player, level: Int): LevelState {
        return when {
            level <= player.getTier(pass) -> LevelState.UNLOCKED
            level == player.getTier(pass) + 1 -> LevelState.IN_PROGRESS
            else -> LevelState.LOCKED
        }
    }

    override fun getLeftClickAction(player: Player, level: Int, levelState: LevelState): () -> Unit {
        val key = run {
            if (levelState == LevelState.UNLOCKED && player.hasReceivedTier(pass, level)) {
                "claimed"
            } else levelState.key
        }

        return if (key == "unlocked") {
            {
                val tier = pass.getTier(level)
                if (tier != null) {
                    levelItemCache.invalidate(level)
                    itemCache[levelState]?.remove(level)
                    player.receiveTier(tier)
                    player.openMenu?.refresh(player)
                    // player.closeInventory()
                }
            }
        } else {
            {}
        }
    }
}