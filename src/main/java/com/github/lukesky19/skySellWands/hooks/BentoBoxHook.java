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
package com.github.lukesky19.skySellWands.hooks;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.lists.Flags;

import java.util.Optional;

/**
 * This class hooks into BentoBox to check if a player can open containers at a particular location.
 */
public class BentoBoxHook implements ProtectionHook {
    /**
     * Default Constructor.
     */
    public BentoBoxHook() {}

    @Override
    public boolean canPlayerOpen(@NotNull Player player, @NotNull Location location) {
        Optional<Island> optionalIsland = BentoBox.getInstance().getIslands().getIslandAt(location);
        if(optionalIsland.isEmpty()) return true;

        Island island = optionalIsland.orElseThrow();
        User user = BentoBox.getInstance().getPlayers().getUser(player.getUniqueId());
        return island.isAllowed(user, Flags.CONTAINER);
    }
}
