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
import com.github.lukesky19.skySellWands.listener.ItemSoldListener;
import com.github.lukesky19.skySellWands.listener.PlayerClickListener;
import com.github.lukesky19.skySellWands.listener.ShopCreationListener;
import com.github.lukesky19.skySellWands.manager.HookManager;
import com.github.lukesky19.skySellWands.manager.WandManager;
import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skyshop.SkyShopAPI;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class SkySellWands extends JavaPlugin {
    private SettingsManager settingsManager;
    private LocaleManager localeManager;
    private HookManager hookManager;
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
        boolean econResult = setupEconomy();
        if(!econResult) return;
        boolean skyShopAPIResult = setupSkyShopAPI();
        if(!skyShopAPIResult) return;

        // Create class instances
        hookManager = new HookManager(this);
        settingsManager = new SettingsManager(this);
        localeManager = new LocaleManager(this, settingsManager);
        WandManager wandManager = new WandManager(settingsManager, localeManager);
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

        this.getComponentLogger().error(FormatUtil.format("Failed to retrieve economy, disabling plugin."));
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

        this.getComponentLogger().error(FormatUtil.format("Failed to retrieve SkyShop's API, disabling plugin."));
        this.getServer().getPluginManager().disablePlugin(this);
        return false;
    }
}
