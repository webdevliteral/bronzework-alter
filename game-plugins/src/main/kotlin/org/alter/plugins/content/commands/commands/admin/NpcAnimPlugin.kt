package org.alter.plugins.content.commands.commands.admin

import dev.openrune.cache.CacheManager.getNpc
import org.alter.api.ext.getCommandArgs
import org.alter.api.ext.message
import org.alter.api.ext.player
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.model.priv.Privilege
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository

/**
 * Bronzework debug commands for finding NPC animation IDs.
 *
 * The OSRS cache stores stand/walk/run/rotate animations per-NPC, but does
 * NOT store combat animations (attack/block/death) -- those live in OSRS
 * server scripts that we don't have. So when we add per-NPC combat data
 * to Alter, we need to discover the correct attack/block/death animation
 * IDs ourselves.
 *
 * The standard rune-server-community technique: combat animations are
 * usually a few IDs above or below the walk/stand animations in the cache
 * (because Jagex defined them all in the same content batch). So:
 *
 *   1. ::npcanim <id>   prints the cache-defined anims for that NPC
 *   2. ::tryanim <id>   makes YOUR character play animation <id> so you
 *                       can visually identify what each animation looks
 *                       like and find the attack/block/death by probing
 *
 * Workflow for adding a new NPC's combat animations:
 *   - Spawn the NPC: ::npc <id>
 *   - Get its base anims: ::npcanim <id>
 *   - Probe nearby IDs: ::tryanim <walk-id +/- N> until something looks
 *     like the attack/block/death
 *   - Record the IDs in a per-NPC NpcCombatBuilder block under the
 *     plugins/content/npcs tree, registered via setCombatDef
 */
class NpcAnimPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {

    init {
        onCommand("npcanim", Privilege.ADMIN_POWER, description = "Print cache animation IDs for an NPC") {
            val args = player.getCommandArgs()
            if (args.isEmpty()) {
                player.message("Usage: ::npcanim <npc-id>")
                return@onCommand
            }
            val id = args[0].toIntOrNull()
            if (id == null) {
                player.message("Invalid NPC id: ${args[0]}")
                return@onCommand
            }
            try {
                val def = getNpc(id)
                player.message("NPC $id: name='${def.name}', size=${def.size}, combatLevel=${def.combatLevel}")
                player.message(" stand=${def.standAnim}, walk=${def.walkAnim}")
                player.message(" rotL=${def.rotateLeftAnim}, rotR=${def.rotateRightAnim}, rotBack=${def.rotateBackAnim}")
                player.message(" walkL=${def.walkLeftAnim}, walkR=${def.walkRightAnim}")
                player.message(" run=${def.runSequence}, runBack=${def.runBackSequence}")
                player.message(" runL=${def.runLeftSequence}, runR=${def.runRightSequence}")
                if (def.crawlSequence != -1) {
                    player.message(" crawl=${def.crawlSequence}")
                }
            } catch (e: Exception) {
                player.message("Error: ${e.message}")
            }
        }

        onCommand("tryanim", Privilege.ADMIN_POWER, description = "Play an animation on yourself for visual ID") {
            val args = player.getCommandArgs()
            if (args.isEmpty()) {
                player.message("Usage: ::tryanim <animation-id>")
                return@onCommand
            }
            val id = args[0].toIntOrNull()
            if (id == null) {
                player.message("Invalid animation id: ${args[0]}")
                return@onCommand
            }
            // animate(-1) clears any current animation, then we play the new one.
            // interruptable=true so probing in quick succession works without
            // having to wait for the previous animation to finish.
            player.animate(-1, interruptable = true)
            player.animate(id, interruptable = true)
            player.message("Playing animation $id")
        }
    }
}
