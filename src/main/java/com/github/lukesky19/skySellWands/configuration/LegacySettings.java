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
package com.github.lukesky19.skySellWands.configuration;

import com.github.lukesky19.skylib.libs.configurate.objectmapping.ConfigSerializable;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This record contains the plugin's configuration.
 * @param configVersion The config version.
 * @param locale The plugin's locale.
 * @param item The {@link Item} config for the sell wand.
 */
@ConfigSerializable
public record LegacySettings(
        @Nullable String configVersion,
        @Nullable String locale,
        @NotNull Item item) {
    /**
     * This record contains the configuration to create the sell wand {@link ItemStack}.
     * @param material The {@link Material} name for the ItemStack.
     * @param name The name to use for the ItemStack.
     * @param lore The ItemStack's lore.
     * @param enchanted Whether the ItemStack is enchanted or not.
     */
    @ConfigSerializable
    public record Item(
            @Nullable String material,
            @Nullable String name,
            @NotNull List<String> lore,
            boolean enchanted) {}
}
