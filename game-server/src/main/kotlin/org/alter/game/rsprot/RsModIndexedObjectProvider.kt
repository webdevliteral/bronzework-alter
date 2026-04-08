package org.alter.game.rsprot

import net.rsprot.protocol.common.game.outgoing.inv.InventoryObject
import net.rsprot.protocol.game.outgoing.inv.UpdateInvPartial
import org.alter.game.model.item.Item

class RsModIndexedObjectProvider(indices: Iterator<Int>, val items: Array<Item?>) : UpdateInvPartial.IndexedObjectProvider(indices) {
    override fun provide(slot: Int): Long {
        // Bronzework fix: was returning InventoryObject(slot, -1, -1), which packs an
        // amount of -1 into the long. rsprot extracts this as a giant negative count
        // (~-7.6 billion) and throws "Obj count cannot be below zero", silently breaking
        // every partial inventory update -- eating, banking, loot pickup, etc.
        // The full provider (RsModObjectProvider) correctly uses InventoryObject.NULL
        // for empty slots; the partial provider should do the same.
        val item = items[slot] ?: return InventoryObject.NULL
        // Bronzework fix: bank placeholders are stored internally as
        // Item(placeholderLink, amount = -2) in Alter's container model. The OSRS
        // wire protocol uses amount = 0 to indicate a greyed-out placeholder
        // slot. Same negative-count rejection applies in partial updates.
        val amount = if (item.amount == -2) 0 else item.amount
        return InventoryObject(slot, item.id, amount)
    }
}
