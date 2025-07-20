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

import java.util.List;

/**
 * This record contains the plugin's locale config.
 * @param configVersion The version of the config.
 * @param prefix The plugin's prefix.
 * @param help The plugin's help message.
 * @param configReload The message sent when the plugin is reloaded.
 * @param givenWand The message sent when a sell wand is given.
 * @param sellSuccess The message sent when a sell wands successfully sells.
 * @param containerInventoryEmpty The message sent when a container is empty and the sell wand attempts to sell.
 * @param noItemsSold The message sent when no items were sold by the sell wand.
 * @param wandUsedUp The message sent when a sell wand is used up.
 * @param noAccess The message sent when a player can't use a sell wand due to a protection plugin.
 */
@ConfigSerializable
public record Locale(
        String configVersion,
        String prefix,
        List<String> help,
        String configReload,
        String givenWand,
        String sellSuccess,
        String containerInventoryEmpty,
        String noItemsSold,
        String wandUsedUp,
        String noAccess) {}