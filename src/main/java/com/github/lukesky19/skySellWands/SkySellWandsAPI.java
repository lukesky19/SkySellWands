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
package com.github.lukesky19.skySellWands;

import com.github.lukesky19.skySellWands.util.WandKeys;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

/**
 * This class provides the SkySellWands API.
 */
public class SkySellWandsAPI {
    /**
     * Default Constructor.
     */
    public SkySellWandsAPI() {}

    /**
     * Is the provided {@link ItemStack} a sell wand?
     * @param itemStack The {@link ItemStack} to check.
     * @return true if a sell wand, otherwise false.
     */
    public boolean isItemStackSellWand(@NotNull ItemStack itemStack) {
        PersistentDataContainer pdc = itemStack.getItemMeta().getPersistentDataContainer();
        return pdc.has(WandKeys.USES.getKey());
    }
}
