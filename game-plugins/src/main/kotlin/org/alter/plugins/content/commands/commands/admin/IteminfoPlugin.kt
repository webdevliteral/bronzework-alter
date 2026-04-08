package org.alter.plugins.content.commands.commands.admin

import dev.openrune.cache.CacheManager.getItem
import org.alter.api.ext.getCommandArgs
import org.alter.api.ext.message
import org.alter.api.ext.player
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.model.priv.Privilege
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository

/**
 * Bronzework debug command. Prints an item's interfaceOptions array exactly
 * as the cache reports it. Useful for diagnosing "Unhandled item action"
 * errors -- compare the printed indices to the option number reported by
 * the InventoryPlugin's debug message and the +1 offset used by onItemOption.
 *
 * Usage: ::iteminfo <item-id>
 * Example: ::iteminfo 2309   (bread)
 */
class IteminfoPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {

    init {
        onCommand("iteminfo", Privilege.ADMIN_POWER, description = "Print interfaceOptions for an item id") {
            val args = player.getCommandArgs()
            if (args.isEmpty()) {
                player.message("Usage: ::iteminfo <item-id>")
                return@onCommand
            }
            val id = args[0].toIntOrNull()
            if (id == null) {
                player.message("Invalid item id: ${args[0]}")
                return@onCommand
            }
            try {
                val def = getItem(id)
                player.message("Item $id: name='${def.name}'")
                player.message("interfaceOptions (${def.interfaceOptions.size} slots):")
                def.interfaceOptions.forEachIndexed { index, opt ->
                    val display = if (opt.isNullOrBlank()) "null" else "'$opt'"
                    player.message("  [$index] = $display")
                }
            } catch (e: Exception) {
                player.message("Error: ${e.message}")
            }
        }
    }
}
