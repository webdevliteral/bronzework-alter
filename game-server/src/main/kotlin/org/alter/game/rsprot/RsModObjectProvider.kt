package org.alter.game.rsprot

import net.rsprot.protocol.common.game.outgoing.inv.InventoryObject
import net.rsprot.protocol.game.outgoing.inv.UpdateInvFull
import org.alter.game.model.item.Item

class RsModObjectProvider(val items: Array<Item?>) : UpdateInvFull.ObjectProvider {
    override fun provide(slot: Int): Long {
        val item = items[slot] ?: return InventoryObject.NULL
        // Bronzework fix: bank placeholders are stored internally as
        // Item(placeholderLink, amount = -2) in Alter's container model. The OSRS
        // wire protocol uses amount = 0 to indicate a greyed-out placeholder
        // slot. rsprot's UpdateInv packet validator rejects any negative count
        // ("Obj count cannot be below zero"), so the -2 sentinel must be
        // translated here at the model->wire boundary.
        val amount = if (item.amount == -2) 0 else item.amount
        return InventoryObject(slot, item.id, amount)
    }
}
