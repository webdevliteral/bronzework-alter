package org.alter.plugins.content.npcs.combat

import org.alter.api.cfg.Animation
import org.alter.api.cfg.Sound
import org.alter.api.dsl.setCombatDef
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository

/**
 * Per-NPC combat definition for the Lumbridge-era goblins.
 *
 * Why this file exists: NpcCombatDef.DEFAULT hardcodes the human player's
 * unarmed animations (attack=422, block=424, death=836). Those bone IDs
 * don't exist on a goblin's skeleton, so without an explicit override the
 * goblin's mesh deforms the moment it tries to attack/block/die. The OSRS
 * cache stores walk/stand per NPC (which is why goblins walk fine), but
 * combat animations live in OSRS server scripts we don't have -- so each
 * non-human NPC needs its combat anims authored as content.
 *
 * The goblin animation/sound constants are already in api/cfg from a prior
 * import, so we just wire them up here. For NPCs that don't have constants
 * yet, use the ::npcanim and ::tryanim admin commands to discover the IDs.
 *
 * Lumbridge/Goblin Village goblins use cache IDs in two contiguous ranges:
 *   655..668, 674, 677, 678  (regular goblins, varied appearances)
 * They share the same combat profile -- the visual variants are just
 * cosmetic recolours/equipment.
 */
class GoblinCombatPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {

    init {
        val goblins = listOf(
            "npc.goblin",
            "npc.goblin_656", "npc.goblin_657", "npc.goblin_658",
            "npc.goblin_659", "npc.goblin_660", "npc.goblin_661",
            "npc.goblin_662", "npc.goblin_663", "npc.goblin_664",
            "npc.goblin_665", "npc.goblin_666", "npc.goblin_667",
            "npc.goblin_668", "npc.goblin_674", "npc.goblin_677",
            "npc.goblin_678",
        )

        goblins.forEach { goblin ->
            setCombatDef(goblin) {
                configs {
                    attackSpeed = 4
                    respawnDelay = 30
                    poisonChance = 0.0
                    venomChance = 0.0
                }
                stats {
                    hitpoints = 5
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
                    attack = Animation.GOBLIN_PUNCH
                    block = Animation.GOBLIN_DEFEND
                    death = Animation.GOBLIN_DEATH
                }
                sound {
                    attackSound = Sound.GOBLIN_ATTACK
                    blockSound = Sound.GOBLIN_HIT
                    deathSound = Sound.GOBLIN_DEATH
                }
            }
        }
    }
}
