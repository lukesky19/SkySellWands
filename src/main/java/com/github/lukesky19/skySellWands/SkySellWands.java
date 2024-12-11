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
import com.github.lukesky19.skySellWands.configuration.manager.LocaleManager;
import com.github.lukesky19.skySellWands.configuration.manager.SettingsManager;
import com.github.lukesky19.skySellWands.listener.PlayerClickListener;
import com.github.lukesky19.skySellWands.manager.WandManager;
import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skyshop.SkyShopAPI;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public final class SkySellWands extends JavaPlugin {
    private SettingsManager settingsManager;
    private LocaleManager localeManager;
    private SkyShopAPI skyShopAPI;
    private Economy economy;

    public SkyShopAPI getSkyShopAPI() {
        return skyShopAPI;
    }

    public Economy getEconomy() {
        return economy;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        setupEconomy();
        setupSkyShopAPI();

        // Create class instances
        settingsManager = new SettingsManager(this);
        localeManager = new LocaleManager(this, settingsManager);
        WandManager wandManager = new WandManager(settingsManager, localeManager);
        PlayerClickListener playerClickListener = new PlayerClickListener(this, settingsManager, localeManager);
        SellWandCommand sellWandCommand = new SellWandCommand(this, localeManager, wandManager);

        // Register Listener
        this.getServer().getPluginManager().registerEvents(playerClickListener, this);

        // Register Command Executor and TabCompleter
        PluginCommand skySellWands = this.getServer().getPluginCommand("skysellwands");
        if(skySellWands != null) {
            skySellWands.setExecutor(sellWandCommand);
            skySellWands.setTabCompleter(sellWandCommand);
        }

        // Reload the plugin
        reload();
    }

    /**
     * Method to reload the plugin.
     */
    public void reload() {
        settingsManager.reload();
        localeManager.reload();
    }

    /**
     * Checks for Vault as a dependency and sets up the Economy instance.
     */
    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") != null) {
            RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
            if (rsp != null) {
                this.economy = rsp.getProvider();
            }
        }

        if(economy == null) {
            this.getComponentLogger().error(FormatUtil.format("Failed to retrieve economy, disabling plugin."));
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    /**
     * Checks for SkyShop as a dependency and gets SkyShop's API
     */
    private void setupSkyShopAPI() {
        if (this.getServer().getPluginManager().getPlugin("SkyShop") != null) {
            @Nullable RegisteredServiceProvider<SkyShopAPI> rsp = this.getServer().getServicesManager().getRegistration(SkyShopAPI.class);
            if (rsp != null) {
                skyShopAPI = rsp.getProvider();
            }
        }

        if(skyShopAPI == null) {
            this.getComponentLogger().error(FormatUtil.format("Failed to retrieve SkyShop's API, disabling plugin."));
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }
}
