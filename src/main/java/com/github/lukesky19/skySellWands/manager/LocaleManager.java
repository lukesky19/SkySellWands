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
import com.github.lukesky19.skySellWands.configuration.Locale;
import com.github.lukesky19.skySellWands.configuration.Settings;
import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.configuration.abstracts.SimpleConfigManager;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.ConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.serialize.SerializationException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * This class manages the plugin's locale configuration.
 */
public class LocaleManager extends SimpleConfigManager<Locale> {
    private final @NonNull SettingsManager settingsManager;
    private final @NonNull Locale DEFAULT_LOCALE = new Locale(
            5,
            "<aqua><bold>SkySellWands</bold></aqua><gray> ▪ </gray>",
            List.of(
                    "<aqua>SkySellWands is developed by <white><bold>lukeskywlker19</bold></white>.</aqua>",
                    "<aqua>Source code is released on GitHub: <click:OPEN_URL:https://github.com/lukesky19><yellow><underlined><bold>https://github.com/lukesky19</bold></underlined></yellow></click></aqua>",
                    " ",
                    "<aqua><bold>List of Commands:</bold></aqua>",
                    "<white>/<aqua>sellwand</aqua> <yellow>help</yellow></white>",
                    "<white>/<aqua>sellwand</aqua> <yellow>reload</yellow></white>",
                    "<white>/<aqua>sellwand</aqua> <yellow>give</yellow> <yellow><player_name></yellow> <yellow><# of uses></yellow> <yellow><amount></yellow></white>"),
            "<aqua>Configuration files have been reloaded.</aqua>",
            "<aqua>You have been given a sellwand with <yellow><uses></yellow> uses.</aqua>",
            "<white>Sold all items in the container. Balance: <yellow><bal></yellow></white>",
            "<red>No items were sold as the container's inventory is empty.</red>",
            "<red>No items were sold as the container's inventory contained no items that could be sold.</red>",
            "<dark_purple>POOF!</dark_purple> <red>Your sellwand ran out of uses.</red>",
            "<red>You do not have access to this container to sell the items inside.</red>");

    /**
     * Constructor
     * @param skySellWands The plugin's instance.
     * @param settingsManager A settings manager instance.
     */
    public LocaleManager(
            @NonNull SkySellWands skySellWands,
            @NonNull SettingsManager settingsManager) {
        super(skySellWands, Locale.class);
        this.settingsManager = settingsManager;
    }

    /**
     * Gets the plugin's locale or default locale.
     * If the plugin's locale config failed to load, the default locale will be provided.
     * @return The plugin's {@link Locale}.
     */
    @Override
    public @NonNull Locale getConfiguration() {
        if(configuration == null) return DEFAULT_LOCALE;
        return configuration;
    }

    @Override
    public void loadConfiguration() {
        configuration = null;

        Settings settings = settingsManager.getConfiguration();
        if(settings == null) return;
        if(settings.locale() == null) return;
        configurationPath = Path.of(plugin.getDirectoryFile() + File.separator + "locale" + File.separator + (settings.locale() + ".yml"));

        YamlConfigurationLoader loader = createLoader(configurationPath);
        try {
            ConfigurationNode root = loader.load();
            int version = getVersion(root);

            boolean saveConfiguration = false;
            Locale locale;
            switch(version) {
                case 5 -> {
                    locale = root.get(Locale.class);
                    if(locale == null) {
                        logger.warn(AdventureUtility.plain("Failed to load version " + version + " for locale " + settings.locale() + "."));
                        return;
                    }
                }

                // 4 -> 5
                case 4 -> {
                    locale = root.get(Locale.class);
                    if(locale == null) {
                        logger.warn(AdventureUtility.plain("Failed to load version " + version + " for locale " + settings.locale() + "."));
                        return;
                    }

                    locale = new Locale(
                            5,
                            locale.prefix(),
                            locale.help(),
                            locale.configReload(),
                            locale.givenWand(),
                            locale.sellSuccess(),
                            locale.containerInventoryEmpty(),
                            locale.noItemsSold(),
                            locale.wandUsedUp(),
                            locale.noAccess());

                    saveConfiguration = true;
                }

                // 3 -> 5
                case 3 -> {
                    locale = root.get(Locale.class);
                    if(locale == null) {
                        logger.warn(AdventureUtility.plain("Failed to load version " + version + " for locale " + settings.locale() + "."));
                        return;
                    }

                    List<String> help = locale.help();
                    help.removeLast();
                    help.add("<white>/<aqua>sellwand</aqua> <yellow>give</yellow> <yellow><player_name></yellow> <yellow><# of uses></yellow> <yellow><amount></yellow></white>");

                    locale = new Locale(
                            5,
                            locale.prefix(),
                            help,
                            locale.configReload(),
                            locale.givenWand(),
                            locale.sellSuccess(),
                            locale.containerInventoryEmpty(),
                            locale.noItemsSold(),
                            locale.wandUsedUp(),
                            locale.noAccess());

                    saveConfiguration = true;
                }

                // 2 -> 5
                case 2 -> {
                    locale = root.get(Locale.class);
                    if(locale == null) {
                        logger.warn(AdventureUtility.plain("Failed to load version " + version + " for locale " + settings.locale() + "."));
                        return;
                    }

                    List<String> help = locale.help();
                    help.removeLast();
                    help.add("<white>/<aqua>sellwand</aqua> <yellow>give</yellow> <yellow><player_name></yellow> <yellow><# of uses></yellow> <yellow><amount></yellow></white>");

                    locale = new Locale(
                            5,
                            locale.prefix(),
                            help,
                            locale.configReload(),
                            locale.givenWand(),
                            locale.sellSuccess(),
                            locale.containerInventoryEmpty(),
                            locale.noItemsSold(),
                            locale.wandUsedUp(),
                            "<red>You do not have access to this container to sell the items inside.</red>");

                    saveConfiguration = true;
                }

                // 1 -> 5
                case 1 -> {
                    locale = root.get(Locale.class);
                    if(locale == null) {
                        logger.warn(AdventureUtility.plain("Failed to load version " + version + " for locale " + settings.locale() + "."));
                        return;
                    }

                    List<String> help = locale.help();
                    help.removeLast();
                    help.add("<white>/<aqua>sellwand</aqua> <yellow>give</yellow> <yellow><player_name></yellow> <yellow><# of uses></yellow> <yellow><amount></yellow></white>");

                    locale = new Locale(
                            5,
                            locale.prefix(),
                            help,
                            locale.configReload(),
                            locale.givenWand(),
                            locale.sellSuccess(),
                            locale.containerInventoryEmpty(),
                            locale.noItemsSold(),
                            locale.wandUsedUp(),
                            "<red>You do not have access to this container to sell the items inside.</red>");

                    saveConfiguration = true;
                }

                default -> {
                    logger.warn(AdventureUtility.plain("Failed to load version " + version + " for locale " + settings.locale() + " due to an unsupported config version."));
                    return;
                }
            }

            // Check if the configuration is invalid
            if(!validateConfiguration(locale)) {
                logger.warn(AdventureUtility.plain("Configuration validation failed for locale " + settings.locale() + "."));
                return;
            }

            // Save configuration if migrated
            if(saveConfiguration) saveConfiguration(locale);

            // Set the configuration
            configuration = locale;
        } catch (ConfigurateException configurateException) {
            logger.error(AdventureUtility.plain("Failed to load the plugin's locale configuration. Error: " + configurateException.getMessage()));
        }
    }

    /**
     * Copies the default locale files that come bundled with the plugin, if they do not exist at least.
     */
    @Override
    public void saveDefaultConfiguration() {
        Path path = Path.of(plugin.getDirectoryFile() + File.separator + "locale" + File.separator + "en_US.yml");
        if (!path.toFile().exists()) {
            plugin.saveResource("locale" + File.separator + "en_US.yml", false);
        }
    }

    /**
     * Currently no migration exists past version 5. The passed settings are returned.
     * @param locale The {@link Locale} to migrate.
     * @return The {@link Locale} passed.
     */
    @Override
    public @Nullable Locale migrateConfiguration(@NonNull Locale locale) {
        return locale;
    }

    /**
     * Checks if any locale strings are missing (null).
     * Sets locale to null if so, resulting in the default locale being used.
     */
    @Override
    public boolean validateConfiguration(@Nullable Locale locale) {
        if(locale == null) return false;

        if (locale.version() != 5
                || locale.prefix() == null
                || locale.help() == null
                || locale.configReload() == null
                || locale.givenWand() == null
                || locale.sellSuccess() == null
                || locale.containerInventoryEmpty() == null
                || locale.noItemsSold() == null
                || locale.wandUsedUp() == null
                || locale.noAccess() == null) {
            configuration = null;

            logger.warn(AdventureUtility.deserialize("<yellow>Your locale configuration is invalid. The plugin will use an internal locale instead."));

            return false;
        }

        return true;
    }

    /**
     * Get the version number.
     * @param root The root {@link com.github.lukesky19.skylib.libs.configurate.ConfigurationNode}.
     * @return The config version.
     */
    private int getVersion(@NonNull ConfigurationNode root) {
        com.github.lukesky19.skylib.libs.configurate.ConfigurationNode versionNode = root.node("version");
        int version = versionNode.getInt();

        com.github.lukesky19.skylib.libs.configurate.ConfigurationNode legacyVersionNode = root.node("config-version");
        String legacyVersion = legacyVersionNode.virtual() ? null : legacyVersionNode.getString();
        if(legacyVersion != null) {
            try {
                switch (legacyVersion) {
                    case "1.3.0" -> {
                        versionNode.set(4);
                        version = 4;
                    }

                    case "1.2.0" -> {
                        versionNode.set(3);
                        version = 3;
                    }

                    case "1.1.0" -> {
                        versionNode.set(2);
                        version = 2;
                    }


                    case "1.0.0" -> {
                        versionNode.set(1);
                        version = 1;
                    }

                    default -> {
                        versionNode.set(0);
                        version = 0;
                    }
                }
            } catch (SerializationException e) {
                logger.warn(AdventureUtility.plain("Failed to convert String-based version to numeric version"));
                version = 0;
            }
        }

        return version;
    }
}