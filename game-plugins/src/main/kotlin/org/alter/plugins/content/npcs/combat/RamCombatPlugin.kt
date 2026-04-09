package org.alter.plugins.content.npcs.combat

import org.alter.api.cfg.Animation
import org.alter.api.cfg.Sound
import org.alter.api.dsl.setCombatDef
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository

/**
 * Per-NPC combat definition for rams (the level-2 livestock around
 * Lumbridge / east of Falador). Wires up RAM_* anims/sounds so they
 * stop deforming when struck.
 *
 * Note: rams use RAM_HIT (not RAM_DEFEND) for the block animation -- the
 * cache constants are inconsistently named across species, so we follow
 * whatever the import generator produced.
 */
class RamCombatPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {

    init {
        val rams = listOf(
            "npc.ram",          // 1261 -- canonical
            "npc.ram_1262",
            "npc.ram_1263",
            "npc.ram_1264",
            "npc.ram_1265",     // the Lumbridge spawn
        )

        rams.forEach { ram ->
            setCombatDef(ram) {
                configs {
                    attackSpeed = 5
                    respawnDelay = 30
                    poisonChance = 0.0
                    venomChance = 0.0
                }
                stats {
                    hitpoints = 6
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
                    attack = Animation.RAM_ATTACK
                    block = Animation.RAM_HIT
                    death = Animation.RAM_DEATH
                }
                sound {
                    attackSound = Sound.RAM_ATTACK
                    blockSound = Sound.RAM_HIT
                    deathSound = Sound.RAM_DEATH
                }
            }
        }
    }
}
