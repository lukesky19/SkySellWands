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
package com.github.lukesky19.skySellWands.util;

import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * This enum contains the {@link NamespacedKey} names for the plugin.
 */
public enum WandKeys {
    /**
     * The name for the key that stores the number of uses for the sell wand.
     */
    USES;

    /**
     * The {@link NamespacedKey}.
     */
    private final @NotNull NamespacedKey key;

    /**
     * Constructor that creates the {@link NamespacedKey}.
     */
    WandKeys() {
        this.key = new NamespacedKey("skysellwands", this.name().toLowerCase());
    }

    /**
     * Get the {@link NamespacedKey}.
     * @return The {@link NamespacedKey} that stores the amount of uses on a sell wand.
     */
    public @NotNull NamespacedKey getKey() {
        return key;
    }
}
