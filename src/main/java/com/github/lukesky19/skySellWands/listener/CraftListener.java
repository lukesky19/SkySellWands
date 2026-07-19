/*
    SkySellWands adds sell wands that uses SkyShop's API selling.
    Copyright (C) 2024 lukeskywlker19

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

import com.github.lukesky19.skySellWands.SkySellWandsAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Objects;

/**
 * This class listens to when an item is crafted prevents the crafting if a sell wand is in the crafting matrix.
 */
public class CraftListener implements Listener {
    private final @NonNull SkySellWandsAPI skySellWandsAPI;

    /**
     * Constructor.
     * @param skySellWandsAPI A {@link SkySellWandsAPI} instance.
     */
    public CraftListener(@NonNull SkySellWandsAPI skySellWandsAPI) {
        this.skySellWandsAPI = skySellWandsAPI;
    }

    /**
     * Listens for an {@link CraftItemEvent} and cancels it if the crafting matrix contains any sell wands.
     * @param craftItemEvent An {@link CraftItemEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onItemCraft(CraftItemEvent craftItemEvent) {
        CraftingInventory craftingInventory = craftItemEvent.getInventory();
        ItemStack[] inputs = craftingInventory.getMatrix();

        if(Arrays.stream(inputs).filter(Objects::nonNull).anyMatch(skySellWandsAPI::isItemStackSellWand)) {
            craftItemEvent.setCancelled(true);
        }
    }
}