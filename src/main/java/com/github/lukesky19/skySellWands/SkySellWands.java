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

import com.github.lukesky19.skySellWands.command.SellWandCommand;
import com.github.lukesky19.skySellWands.listener.ItemSoldListener;
import com.github.lukesky19.skySellWands.listener.PlayerClickListener;
import com.github.lukesky19.skySellWands.listener.ShopCreationListener;
import com.github.lukesky19.skySellWands.manager.HookManager;
import com.github.lukesky19.skySellWands.manager.LocaleManager;
import com.github.lukesky19.skySellWands.manager.SettingsManager;
import com.github.lukesky19.skySellWands.manager.WandManager;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skyshop.SkyShopAPI;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * This class is the entry point to the plugin.
 */
public final class SkySellWands extends JavaPlugin {
    private SettingsManager settingsManager;
    private LocaleManager localeManager;
    private HookManager hookManager;
    private SkyShopAPI skyShopAPI;
    private Economy economy;

    /**
     * Default Constructor.
     */
    public SkySellWands() {}

    /**
     * Get the {@link SkyShopAPI}.
     * @return The {@link SkyShopAPI}.
     */
    public SkyShopAPI getSkyShopAPI() {
        return skyShopAPI;
    }

    /**
     * Get the server's {@link Economy}.
     * @return The {@link Economy}.
     */
    public Economy getEconomy() {
        return economy;
    }

    /**
     * The method ran on plugin startup.
     */
    @Override
    public void onEnable() {
        if(!checkSkyLibVersion()) return;

        // Plugin startup logic
        boolean econResult = setupEconomy();
        if(!econResult) return;
        boolean skyShopAPIResult = setupSkyShopAPI();
        if(!skyShopAPIResult) return;

        // Create class instances
        hookManager = new HookManager(this);
        settingsManager = new SettingsManager(this);
        localeManager = new LocaleManager(this, settingsManager);
        WandManager wandManager = new WandManager(this, settingsManager, localeManager);
        PlayerClickListener playerClickListener = new PlayerClickListener(this, settingsManager, localeManager, hookManager);
        SellWandCommand sellWandCommand = new SellWandCommand(this, localeManager, wandManager);

        // Register Listener
        this.getServer().getPluginManager().registerEvents(playerClickListener, this);

        Plugin quickShop = this.getServer().getPluginManager().getPlugin("QuicKShop-Hikari");
        if(quickShop != null && quickShop.isEnabled()) {
            this.getServer().getPluginManager().registerEvents(new ShopCreationListener(), this);
        }

        this.getServer().getPluginManager().registerEvents(new ItemSoldListener(), this);

        // Register Command
        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                commands -> commands.registrar().register(sellWandCommand.createCommand(),
                        "Command to manage and use the SkySellWand plugin.",
                        List.of("sellwand", "sellwands", "skysellwands")));

        // Register API
        SkySellWandsAPI skySellWandsAPI = new SkySellWandsAPI();
        this.getServer().getServicesManager().register(SkySellWandsAPI.class, skySellWandsAPI, this, ServicePriority.Lowest);

        // Reload the plugin
        reload();
    }

    /**
     * Method to reload the plugin.
     */
    public void reload() {
        settingsManager.reload();
        localeManager.reload();
        hookManager.reload();
    }

    /**
     * Checks for Vault as a dependency and sets up the Economy instance.
     */
    private boolean setupEconomy() {
        if(getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                this.economy = rsp.getProvider();

                return true;
            }
        }

        this.getComponentLogger().error(AdventureUtil.serialize("Failed to retrieve economy, disabling plugin."));
        this.getServer().getPluginManager().disablePlugin(this);
        return false;
    }

    /**
     * Checks for SkyShop as a dependency and gets SkyShop's API
     */
    private boolean setupSkyShopAPI() {
        if (this.getServer().getPluginManager().getPlugin("SkyShop") != null) {
            @Nullable RegisteredServiceProvider<SkyShopAPI> rsp = this.getServer().getServicesManager().getRegistration(SkyShopAPI.class);
            if (rsp != null) {
                skyShopAPI = rsp.getProvider();

                return true;
            }
        }

        this.getComponentLogger().error(AdventureUtil.serialize("Failed to retrieve SkyShop's API, disabling plugin."));
        this.getServer().getPluginManager().disablePlugin(this);
        return false;
    }

    /**
     * Checks if the Server has the proper SkyLib version.
     * @return true if it does, false if not.
     */
    private boolean checkSkyLibVersion() {
        PluginManager pluginManager = this.getServer().getPluginManager();
        Plugin skyLib = pluginManager.getPlugin("SkyLib");
        if (skyLib != null) {
            String version = skyLib.getPluginMeta().getVersion();
            String[] splitVersion = version.split("\\.");
            int second = Integer.parseInt(splitVersion[1]);

            if(second >= 3) {
                return true;
            }
        }

        this.getComponentLogger().error(AdventureUtil.serialize("SkyLib Version 1.3.0.0 or newer is required to run this plugin."));
        this.getServer().getPluginManager().disablePlugin(this);
        return false;
    }
}
