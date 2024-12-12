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
package com.github.lukesky19.skySellWands.configuration.manager;

import com.github.lukesky19.skySellWands.SkySellWands;
import com.github.lukesky19.skySellWands.configuration.record.Locale;
import com.github.lukesky19.skySellWands.configuration.record.Settings;
import com.github.lukesky19.skylib.config.ConfigurationUtility;
import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.ConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class LocaleManager {
    private final SkySellWands skySellWands;
    private final SettingsManager settingsManager;

    Locale locale;
    private final Locale DEFAULT_LOCALE = new Locale(
            "1.1.0",
            "<aqua><bold>SkySellWands</bold></aqua><gray> ▪ </gray>",
            List.of(
                    "<aqua>SkySellWands is developed by <white><bold>lukeskywlker19</bold></white>.</aqua>",
                    "<aqua>Source code is released on GitHub: <click:OPEN_URL:https://github.com/lukesky19><yellow><underlined><bold>https://github.com/lukesky19</bold></underlined></yellow></click></aqua>",
                    " ",
                    "<aqua><bold>List of Commands:</bold></aqua>",
                    "<white>/<aqua>sellwand</aqua> <yellow>help</yellow></white>",
                    "<white>/<aqua>sellwand</aqua> <yellow>reload</yellow></white>",
                    "<white>/<aqua>sellwand</aqua> <yellow>give</yellow> <yellow><player_name></yellow> <yellow><# of uses | unlimited | infinite | inf></yellow> <yellow><amount></yellow></white>"),
            "<red>You do not have permission for this command.</red>",
            "<red>Unknown argument.</red>",
            "<aqua>Configuration files have been reloaded.</aqua>",
            "<red>The player is invalid. Are they online?</red>",
            "<red>The number of uses provided is not a valid integer.</red>",
            "<red>The amount of sellwands to give is not a valid integer.</red>",
            "<aqua>You have been given a sellwand with <yellow><uses></yellow> uses.</aqua>",
            "<white>Sold all items in the container. Balance: <yellow><bal></yellow></white>",
            "<red>No items were sold as the container's inventory is empty.</red>",
            "<red>No items were sold as the container's inventory contained no items that could be sold.</red>",
            "<dark_purple>POOF!</dark_purple> <red>Your sellwand ran out of uses.</red>");

    /**
     * Constructor
     * @param skySellWands The plugin's instance.
     * @param settingsManager A settings manager instance.
     */
    public LocaleManager(SkySellWands skySellWands, SettingsManager settingsManager) {
        this.skySellWands = skySellWands;
        this.settingsManager = settingsManager;
    }

    /**
     * Gets the plugin's locale or default locale.
     * If the plugin's locale config failed to load, the default locale will be provided.
     * @return A Locale object.
     */
    public Locale getLocale() {
        if(locale == null) return DEFAULT_LOCALE;

        return locale;
    }

    /**
     * A method to reload the plugin's locale config.
     */
    public void reload() {
        final Settings settings = settingsManager.getSettings();

        locale = null;

        if(settings == null) return;

        copyDefaultLocales();

        String localeString = settings.locale();

        if(localeString != null) {
            Path path = Path.of(skySellWands.getDataFolder() + File.separator + "locale" + File.separator + (localeString + ".yml"));

            YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
            try {
                locale = loader.load().get(Locale.class);
            } catch (ConfigurateException e) {
                throw new RuntimeException(e);
            }

            migrateLocale();
            checkLocale();
        }
    }

    /**
     * Copies the default locale files that come bundled with the plugin, if they do not exist at least.
     */
    private void copyDefaultLocales() {
        Path path = Path.of(skySellWands.getDataFolder() + File.separator + "locale" + File.separator + "en_US.yml");
        if (!path.toFile().exists()) {
            skySellWands.saveResource("locale" + File.separator + "en_US.yml", false);
        }
    }

    /**
     * Checks if any locale strings are missing (null).
     * Sets locale to null if so, resulting in the default locale being used.
     */
    private void checkLocale() {
        final ComponentLogger logger = skySellWands.getComponentLogger();
        if(locale == null) return;

        if (locale.configVersion() == null
                || locale.prefix() == null
                || locale.help() == null
                || locale.noPermission() == null
                || locale.unknownArgument() == null
                || locale.configReload() == null
                || locale.invalidPlayer() == null
                || locale.invalidUses() == null
                || locale.invalidAmount() == null
                || locale.givenWand() == null
                || locale.sellSuccess() == null
                || locale.containerInventoryEmpty() == null
                || locale.noItemsSold() == null
                || locale.wandUsedUp() == null) {
            locale = null;

            logger.warn(FormatUtil.format("<yellow>Your locale configuration is invalid. The plugin will use an internal locale instead."));
        }
    }

    private void migrateLocale() {
        if(locale == null) return;

        switch(locale.configVersion()) {
            case "1.1.0" -> {
                // Current version, do nothing
            }

            case "1.0.0" -> {
                List<String> help = locale.help();
                help.removeLast();
                help.add("<white>/<aqua>sellwand</aqua> <yellow>give</yellow> <yellow><player_name></yellow> " +
                        "<yellow><# of uses | unlimited | infinite | inf></yellow> <yellow><amount></yellow></white>");

                locale = new Locale(
                        "1.1.0",
                        locale.prefix(), help,
                        locale.noPermission(),
                        locale.unknownArgument(),
                        locale.configReload(),
                        locale.invalidPlayer(),
                        locale.invalidUses(),
                        locale.invalidAmount(),
                        locale.givenWand(),
                        locale.sellSuccess(),
                        locale.containerInventoryEmpty(),
                        locale.noItemsSold(),
                        locale.wandUsedUp());

                saveLocale(locale);
            }
        }
    }

    private void saveLocale(Locale locale) {
        Settings settings = settingsManager.getSettings();
        if(settings == null) return;

        String localeString = settings.locale();
        Path path = Path.of(skySellWands.getDataFolder() + File.separator + "locale" + File.separator + (localeString + ".yml"));

        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        ConfigurationNode node = loader.createNode();

        try {
            node.set(locale);
            loader.save(node);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }
}
