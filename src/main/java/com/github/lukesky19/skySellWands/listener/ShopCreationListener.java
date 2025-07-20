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

import com.ghostchu.quickshop.api.event.modification.ShopPreCreateEvent;
import com.github.lukesky19.skySellWands.util.WandKeys;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Optional;

/**
 * This class prevents the selling of sell wands through QuickShop-Hikari.
 */
public class ShopCreationListener implements Listener {
    /**
     * Default Constructor.
     */
    public ShopCreationListener() {}

    /**
     * This event listens for a {@link ShopPreCreateEvent} and cancels it if the item is a sell wand.
     * @param shopPreCreateEvent A {@link ShopPreCreateEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onShopPreCreate(ShopPreCreateEvent shopPreCreateEvent) {
        // Get the player involved with the event
        Optional<Player> optionalPlayer = shopPreCreateEvent.getCreator().getBukkitPlayer();
        if(optionalPlayer.isEmpty()) return;
        Player player = optionalPlayer.get();

        // Get the item the player's main hand
        ItemStack handItem = player.getInventory().getItemInMainHand().clone();
        if(handItem.isEmpty()) return;
        // Get the item's ItemMeta
        ItemMeta itemMeta = handItem.getItemMeta();
        if(itemMeta == null) return;

        // Get the PersistentDataContainer of the item
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        // Check if it is a sellwand
        if(pdc.has(WandKeys.USES.getKey())) {
            // Cancel the shop creation event
            shopPreCreateEvent.setCancelled(true, AdventureUtil.serialize("Item is a sell wand."));
        }
    }
}
