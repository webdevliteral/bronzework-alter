package org.alter.plugins.content.npcs.combat

import org.alter.api.cfg.Animation
import org.alter.api.cfg.Sound
import org.alter.api.dsl.setCombatDef
import org.alter.game.Server
import org.alter.game.model.World
import org.alter.game.plugin.KotlinPlugin
import org.alter.game.plugin.PluginRepository

/**
 * Per-NPC combat definition for imps. Same fix pattern as goblins/spiders --
 * the human-default NpcCombatDef.DEFAULT animations deform the imp's small,
 * winged skeleton, so we wire up the cache-correct IMP_* anims/sounds.
 *
 * The cache only has imp variants under the imp_NNN naming, no bare "imp"
 * entry. id 5007 is the one that spawns around Lumbridge.
 */
class ImpCombatPlugin(
    r: PluginRepository,
    world: World,
    server: Server
) : KotlinPlugin(r, world, server) {

    init {
        val imps = listOf(
            "npc.imp_3134",
            "npc.imp_5007",
            "npc.imp_5728",
        )

        imps.forEach { imp ->
            setCombatDef(imp) {
                configs {
                    attackSpeed = 5
                    respawnDelay = 30
                    poisonChance = 0.0
                    venomChance = 0.0
                }
                stats {
                    hitpoints = 8
                    attack = 5
                    strength = 1
                    defence = 5
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
                    attack = Animation.IMP_ATTACK
                    block = Animation.IMP_DEFEND
                    death = Animation.IMP_DEATH
                }
                sound {
                    attackSound = Sound.IMP_ATTACK
                    blockSound = Sound.IMP_HIT
                    deathSound = Sound.IMP_DEATH
                }
            }
        }
    }
}
