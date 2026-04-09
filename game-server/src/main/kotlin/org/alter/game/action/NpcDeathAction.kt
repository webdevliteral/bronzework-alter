package org.alter.game.action

import dev.openrune.cache.CacheManager.getAnim
import org.alter.game.action.NpcDeathAction.reset
import org.alter.game.info.NpcInfo
import org.alter.game.model.LockState
import org.alter.game.model.attr.KILLER_ATTR
import org.alter.game.model.entity.AreaSound
import org.alter.game.model.entity.GroundItem
import org.alter.game.model.entity.Npc
import org.alter.game.model.entity.Pawn
import org.alter.game.model.entity.Player
import org.alter.game.model.move.moveTo
import org.alter.game.model.move.stopMovement
import org.alter.game.model.queue.QueueTask
import org.alter.game.model.queue.TaskPriority
import org.alter.game.model.weightedTableBuilder.roll
import org.alter.game.plugin.Plugin
import org.alter.game.service.log.LoggerService
import java.lang.ref.WeakReference

/**
 * This class is responsible for handling npc death events.
 *
 * @author Tom <rspsmods@gmail.com>
 */
object NpcDeathAction {
    var deathPlugin: Plugin.() -> Unit = {
        val npc = ctx as Npc
        if (!npc.world.plugins.executeNpcFullDeath(npc)) {
            npc.interruptQueues()
            npc.stopMovement()
            npc.lock()
            npc.queue(TaskPriority.STRONG) {
                death(npc)
            }
        }
    }

    suspend fun QueueTask.death(npc: Npc) {
        val world = npc.world
        val deathAnimation = npc.combatDef.deathAnimation
        val deathSound = npc.combatDef.defaultDeathSound
        val respawnDelay = npc.combatDef.respawnDelay
        var killer: Pawn? = null
        npc.damageMap.getMostDamage()?.let {
            if (it is Player) {
                killer = it
                world.getService(LoggerService::class.java, searchSubclasses = true)?.logNpcKill(it, npc)
            }
            npc.attr[KILLER_ATTR] = WeakReference(it)
        }
        NpcInfo(npc).setAllOpsInvisible()
        world.plugins.executeNpcPreDeath(npc)
        npc.resetFacePawn()
        if (npc.combatDef.defaultDeathSoundArea) {
            world.spawn(AreaSound(npc.tile, deathSound, npc.combatDef.defaultDeathSoundRadius, npc.combatDef.defaultDeathSoundVolume))
        } else {
            (killer as? Player)?.playSound(deathSound, npc.combatDef.defaultDeathSoundVolume)
        }

        /**
         * @TODO add interruption for this block if we would want to execute a plugin during it's death animation
         */
        deathAnimation.forEach { anim ->
            val def = getAnim(anim)
            npc.animate(def.id, def.cycleLength)
            wait(def.cycleLength)
        }
        world.plugins.executeNpcDeath(npc)
        world.plugins.anyNpcDeath.forEach {
            npc.executePlugin(it)
        }

        // Bronzework: spawn loot drops at the NPC's death tile.
        // Alter ships the loot table infrastructure (LootTable, roll(), etc.) and
        // an NpcCombatDef.LootTables field, but the death sequence never actually
        // calls roll() and no NPC has loot data populated. Result: every kill
        // dropped nothing. This block fills that gap.
        //
        // Behavior:
        // - If the killer is a Player AND combatDef.LootTables is populated,
        //   roll the loot table and spawn each resulting drop at the NPC's tile.
        // - If the killer is a Player AND combatDef.LootTables is null (the
        //   default for every NPC currently), spawn a fallback bones drop so
        //   testing combat actually produces something. This is a placeholder
        //   until we populate real per-NPC drop tables -- bones are item id 526
        //   in OSRS and reliably exist in the rev 228 cache.
        //
        // Drops are owned by the killer with standard public/despawn timers.
        (killer as? Player)?.let { p ->
            val deathTile = npc.tile
            val combatDef = npc.combatDef
            val drops = if (combatDef.LootTables != null) {
                roll(p, combatDef.LootTables)
            } else {
                // Fallback: every player kill drops bones until real drop tables
                // exist. Item 526 = bones in standard OSRS rev 228 cache.
                setOf(GroundItem(526, 1, deathTile, p))
            }
            drops.forEach { rolled ->
                // The roll() helper creates GroundItems with tile = (0,0,0) and
                // no owner; rebuild each one at the death tile owned by the killer
                // so the drop is visible/pickupable in the right place.
                val floor = GroundItem(rolled.item, rolled.amount, deathTile, p)
                floor.timeUntilPublic = world.gameContext.gItemPublicDelay
                floor.timeUntilDespawn = world.gameContext.gItemDespawnDelay
                floor.ownerShipType = 1
                world.spawn(floor)
            }
        }
        if (npc.respawns) {
            NpcInfo(npc).setInaccessible(true)
            npc.reset()
            wait(respawnDelay)
            NpcInfo(npc).setAllOpsVisible()
            NpcInfo(npc).setInaccessible(false)
            world.plugins.executeNpcSpawn(npc)
        } else {
            world.remove(npc)
        }
    }
    private fun Npc.reset() {
        lock = LockState.NONE
        moveTo(spawnTile)
        attr.clear()
        timers.clear()
        world.setNpcDefaults(this)
    }
}
