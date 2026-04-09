package org.alter.plugins.content.npcs.combat

import org.alter.api.cfg.Animation
import org.alter.api.cfg.Sound
import org.alter.api.dsl.setCombatDef
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository

/**
 * Per-NPC combat definition for the basic level-1 rats. Wires the
 * cache-correct RAT_* animations to stop the human-default deformation.
 *
 * Covers the canonical "rat" plus the variants that share the same combat
 * profile (the Lumbridge swamp / Stronghold of Security level-1 rats).
 * Angry/giant/diseased rats are NOT included -- they have different stats
 * and will get their own defs if they're already mapped in the cache.
 */
class RatCombatPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {

    init {
        val rats = listOf(
            "npc.rat",          // 1020 -- canonical
            "npc.rat_1021",
            "npc.rat_1022",
            "npc.rat_2492",
            "npc.rat_2513",
            "npc.rat_2854",     // the Lumbridge spawn
            "npc.rat_2855",
        )

        rats.forEach { rat ->
            setCombatDef(rat) {
                configs {
                    attackSpeed = 4
                    respawnDelay = 30
                    poisonChance = 0.0
                    venomChance = 0.0
                }
                stats {
                    hitpoints = 2
                    attack = 1
                    strength = 1
                    defence = 1
                    magic = 1
                    ranged = 1
                }
                bonuses {
                    defenceStab = -21
                    defenceSlash = -21
                    defenceCrush = -21
                    defenceMagic = -21
                    defenceRanged = -21
                }
                anims {
                    attack = Animation.RAT_ATTACK
                    block = Animation.RAT_DEFEND
                    death = Animation.RAT_DEATH
                }
                sound {
                    attackSound = Sound.RAT_ATTACK
                    blockSound = Sound.RAT_HIT
                    deathSound = Sound.RAT_DEATH
                }
            }
        }
    }
}
