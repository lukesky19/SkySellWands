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

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * This class hooks into WorldGuard to check if a player can open containers at a particular location.
 */
public class WorldGuardHook implements ProtectionHook {
    private final @NotNull WorldGuard worldGuard = WorldGuard.getInstance();

    /**
     * Default Constructor.
     */
    public WorldGuardHook() {}

    @Override
    public boolean canPlayerOpen(@NotNull Player player, @NotNull Location location) {
        RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
        com.sk89q.worldedit.util.Location wgLocation = BukkitAdapter.adapt(location);
        LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        World world = BukkitAdapter.adapt(location.getWorld());

        if(worldGuard.getPlatform().getSessionManager().hasBypass(wgPlayer, world)) {
            return true;
        }

        return query.testState(wgLocation, WorldGuardPlugin.inst().wrapPlayer(player), Flags.CHEST_ACCESS);
    }
}
