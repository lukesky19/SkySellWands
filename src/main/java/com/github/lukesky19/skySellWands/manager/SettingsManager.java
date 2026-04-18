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
import com.github.lukesky19.skySellWands.configuration.LegacySettings;
import com.github.lukesky19.skySellWands.configuration.Settings;
import com.github.lukesky19.skylib.common.api.adventure.AdventureUtility;
import com.github.lukesky19.skylib.common.api.configuration.abstracts.SimpleConfigManager;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.ConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.serialize.SerializationException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import com.github.lukesky19.skylib.paper.api.itemstack.ItemStackBuilder;
import com.github.lukesky19.skylib.paper.api.itemstack.ItemStackConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * This class manages the plugin's settings.
 */
public class SettingsManager extends SimpleConfigManager<Settings> {
    /**
     * Constructor
     * @param skySellWands A {@link SkySellWands} instance.
     */
    public SettingsManager(@NotNull SkySellWands skySellWands) {
        super(skySellWands, Path.of(skySellWands.getDataFolder() + File.separator + "settings.yml"), Settings.class);
    }

    @Override
    public void loadConfiguration() {
        configuration = null;
        if(configurationPath == null) return;

        YamlConfigurationLoader loader = createLoader(configurationPath);
        try {
            ConfigurationNode root = loader.load();
            int version = getVersion(root);

            Settings settings;
            switch(version) {
                case 3 -> {
                    settings = root.get(Settings.class);
                    if(settings == null) {
                        logger.warn(AdventureUtility.plain("Failed to load version " + version + " plugin settings."));
                        return;
                    }
                }

                case 2 -> {
                    settings = root.get(Settings.class);
                    if(settings == null) {
                        logger.warn(AdventureUtility.plain("Failed to load version " + version + " plugin settings."));
                        return;
                    }

                    settings = new Settings(3, settings.locale(), settings.item());
                }

                case 1 -> {
                    LegacySettings legacySettings = root.get(LegacySettings.class);
                    if(legacySettings == null) {
                        logger.warn(AdventureUtility.plain("Unable to migrate legacy settings due to failure to load."));
                        return;
                    }

                    if(legacySettings.item().material() == null) {
                        logger.warn(AdventureUtility.plain("Unable to migrate legacy settings due to an invalid material."));
                        return;
                    }

                    Material material = Material.getMaterial(legacySettings.item().material());
                    if(material == null) {
                        logger.warn(AdventureUtility.plain("Unable to migrate legacy settings due to no material found for " + legacySettings.item().material() + "."));
                        return;
                    }

                    ItemType itemType = material.asItemType();
                    if(itemType == null) {
                        logger.warn(AdventureUtility.plain("Unable to migrate legacy settings as there was no ItemType found for " + legacySettings.item().material() + "."));
                        return;
                    }

                    ItemStackConfig itemStackConfig = new ItemStackConfig(
                            itemType,
                            null,
                            null,
                            legacySettings.item().name(),
                            legacySettings.item().lore(),
                            null,
                            null,
                            List.of(),
                            new ItemStackConfig.PotionConfig(null, List.of()),
                            new ItemStackConfig.ColorConfig(false, null, null, null),
                            null,
                            List.of(),
                            new ItemStackConfig.DecoratedPotConfig(null, null, null, null),
                            new ItemStackConfig.ArmorTrimConfig(null, null),
                            List.of(),
                            new ItemStackConfig.OptionsConfig(legacySettings.item().enchanted(), null, null, null, null));


                    settings = new Settings(3, legacySettings.locale(), itemStackConfig);
                }

                default -> {
                    logger.warn(AdventureUtility.plain("Failed to load version " + version + " plugin settings due to an unsupported config version."));
                    return;
                }
            }

            // Check if the configuration is invalid
            if(!validateConfiguration(settings)) {
                logger.warn(AdventureUtility.plain("Configuration validation failed for plugin settings"));
                return;
            }

            // Set the configuration
            configuration = settings;
        } catch (ConfigurateException configurateException) {
            logger.error(AdventureUtility.plain("Failed to load the configuration. Error: " + configurateException.getMessage()));
        }
    }

    @Override
    public void saveDefaultConfiguration() {
        plugin.saveResource("settings.yml", false);
    }

    /**
     * Currently no migration exists past version 3. The passed settings are returned.
     * @param settings The {@link Settings} to migrate.
     * @return The {@link Settings} passed.
     */
    @Override
    public @NonNull Settings migrateConfiguration(@NonNull Settings settings) {
        return settings;
    }

    @Override
    public boolean validateConfiguration(@Nullable Settings settings) {
        if(settings == null) return false;
        if(settings.version() != 3) return false;
        if(settings.locale() == null) return false;

        Optional<ItemStack> optionalItemStack = new ItemStackBuilder(logger)
                .fromItemStackConfig(settings.item(), null, List.of())
                .buildItemStack();

        return optionalItemStack.isPresent();
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
                    case "1.1.0.0" -> {
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