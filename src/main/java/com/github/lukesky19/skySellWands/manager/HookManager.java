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
package com.github.lukesky19.skySellWands.manager;

import com.github.lukesky19.skySellWands.SkySellWands;
import com.github.lukesky19.skySellWands.hooks.BentoBoxHook;
import com.github.lukesky19.skySellWands.hooks.ProtectionHook;
import com.github.lukesky19.skySellWands.hooks.WorldGuardHook;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages hooks into different plugins.
 */
public class HookManager {
    private final @NotNull SkySellWands skySellWands;
    private final @NotNull List<ProtectionHook> protectionHooks = new ArrayList<>();

    /**
     * Constructor
     * @param skySellWands A {@link SkySellWands} instance.
     */
    public HookManager(@NotNull SkySellWands skySellWands) {
        this.skySellWands = skySellWands;
    }

    /**
     * Reload plugin hooks.
     */
    public void reload() {
        protectionHooks.clear();

        PluginManager pluginManager = skySellWands.getServer().getPluginManager();

        Plugin bentoBox = pluginManager.getPlugin("BentoBox");
        if(bentoBox != null && bentoBox.isEnabled()) {
            protectionHooks.add(new BentoBoxHook());
        }

        Plugin worldGuard = pluginManager.getPlugin("WorldGuard");
        if(worldGuard != null && worldGuard.isEnabled()) {
            protectionHooks.add(new WorldGuardHook());
        }
    }

    /**
     * Can the Player open containers at the given Location?
     * @param player   The Player who is attempting to access the container.
     * @param location The Location of the container that is being accessed.
     * @return true If the Player can open the container.
     */
    public boolean canPlayerOpen(Player player, Location location) {
        for(ProtectionHook hook : this.protectionHooks) {
            if(hook instanceof BentoBoxHook) {
                if(hook.canPlayerOpen(player, location)) return true;
            } else {
                if(!hook.canPlayerOpen(player, location)) return false;
            }
        }

        return true;
    }
}
