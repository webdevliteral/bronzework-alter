package org.alter.plugins.content.npcs.combat

import org.alter.api.cfg.Animation
import org.alter.api.cfg.Sound
import org.alter.api.dsl.setCombatDef
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository

/**
 * Per-NPC combat definition for giant spiders (the level-2 critters that
 * crawl around Lumbridge swamp / Stronghold of Security entrance).
 *
 * Same story as the goblins: NpcCombatDef.DEFAULT bakes in the human
 * unarmed animation IDs, which deform a spider's mesh because spiders have
 * no humanoid skeleton. Wiring up the cache-correct giant spider animations
 * (already mapped as Animation.GIANT_SPIDER_*) makes them stop folding in
 * half on attack/block/death.
 *
 * Only "giant spider" variants are covered here; smaller / coloured /
 * elemental spiders (jungle, poison, ice, shadow, deadly red, etc.) use
 * different animation sets and will get their own defs as we hit them.
 */
class SpiderCombatPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {

    init {
        val giantSpiders = listOf(
            "npc.giant_spider",        // 2477 -- canonical
            "npc.giant_spider_3017",   // 3017 -- the Lumbridge spawn
            "npc.giant_spider_3018",   // 3018
        )

        giantSpiders.forEach { spider ->
            setCombatDef(spider) {
                configs {
                    attackSpeed = 5
                    respawnDelay = 30
                    poisonChance = 0.0
                    venomChance = 0.0
                }
                stats {
                    hitpoints = 5
                    attack = 2
                    strength = 2
                    defence = 2
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
                    attack = Animation.GIANT_SPIDER_ATTACK
                    block = Animation.GIANT_SPIDER_DEFEND
                    death = Animation.GIANT_SPIDER_DEATH
                }
                sound {
                    attackSound = Sound.BIG_SPIDER_ATTACK
                    blockSound = Sound.BIG_SPIDER_HIT
                    deathSound = Sound.BIG_SPIDER_DEATH
                }
            }
        }
    }
}
