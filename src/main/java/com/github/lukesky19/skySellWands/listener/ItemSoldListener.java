/*
    SkySellWands adds sell wands that uses SkyShop's API selling.
    Copyright (C) 2024  lukeskywlker19

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.lukesky19.skySellWands.listener;

import com.github.lukesky19.skySellWands.util.WandKeys;
import com.github.lukesky19.skyshop.event.ItemSoldEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;

/**
 * This class listens to when an item is sold through SkyShop and prevents the selling of sell wands.
 */
public class ItemSoldListener implements Listener {
    /**
     * Default Constructor.
     */
    public ItemSoldListener() {}

    /**
     * Listens for an {@link ItemSoldEvent} and cancels it if the item is a sell wand.
     * @param itemSoldEvent An {@link ItemSoldEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemSold(ItemSoldEvent itemSoldEvent) {
        PersistentDataContainer pdc = itemSoldEvent.getItemStack().getItemMeta().getPersistentDataContainer();
        // Check if it is a sellwand
        if (pdc.has(WandKeys.USES.getKey())) {
            // Cancel selling sell wands
            itemSoldEvent.setCancelled(true);
        }
    }
}
