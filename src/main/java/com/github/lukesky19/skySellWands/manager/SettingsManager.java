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
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.configurate.ConfigurationUtility;
import com.github.lukesky19.skylib.api.itemstack.ItemStackConfig;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.ConfigurationNode;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * This class manages the plugin's settings.
 */
public class SettingsManager {
    private final @NotNull SkySellWands skySellWands;
    private final @NotNull ComponentLogger logger;
    private @Nullable Settings settings;

    /**
     * Constructor
     * @param skySellWands A {@link SkySellWands} instance.
     */
    public SettingsManager(@NotNull SkySellWands skySellWands) {
        this.skySellWands = skySellWands;
        this.logger = skySellWands.getComponentLogger();
    }

    /**
     * Get the plugin's settings.
     * @return The plugin's {@link Settings}.
     */
    public @Nullable Settings getSettings() {
        return settings;
    }

    /**
     * A method to reload the plugin's settings.
     */
    public void reload() {
        settings = null;
        Path path = Path.of(skySellWands.getDataFolder() + File.separator + "settings.yml");
        if(!path.toFile().exists()) {
            skySellWands.saveResource("settings.yml", false);
        }

        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        try {
            settings = loader.load().get(Settings.class);
        } catch (ConfigurateException e) {
            logger.error(AdventureUtil.serialize("Failed to load plugin settings: " + e.getMessage()));
            return;
        }

        migrateSettings();
    }

    /**
     * Migrates the plugin's settings from any legacy versions.
     */
    private void migrateSettings() {
        if(settings == null) return;

        switch(settings.configVersion()) {
            case "1.1.0.0" -> {
                // Current version, do nothing
            }

            // 1.1.0 -> 1.1.0.0
            case "1.0.0" -> {
                LegacySettings legacySettings = loadLegacySettings();
                if(legacySettings == null) return;

                if(legacySettings.item().material() == null) {
                    logger.error(AdventureUtil.serialize("Unable to migrate legacy settings due to an invalid material."));
                    settings = null;
                    return;
                }

                Material material = Material.getMaterial(legacySettings.item().material());
                if(material == null) {
                    logger.error(AdventureUtil.serialize("Unable to migrate legacy settings due to no material found for " + legacySettings.item().material() + "."));
                    settings = null;
                    return;
                }

                ItemType itemType = material.asItemType();
                if(itemType == null) {
                    logger.error(AdventureUtil.serialize("Unable to migrate legacy settings as there was no ItemType found for " + legacySettings.item().material() + "."));
                    settings = null;
                    return;
                }

                ItemStackConfig itemStackConfig = new ItemStackConfig(
                        itemType.getKey().getKey(),
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


                settings = new Settings("1.1.0.0", legacySettings.locale(), itemStackConfig);

                saveSettings(settings);
            }

            case null, default -> throw new IllegalStateException("Unexpected value: " + settings.configVersion());
        }
    }

    /**
     * Load the plugin's settings to {@link LegacySettings} for migration.
     * @return The {@link LegacySettings} or null.
     */
    private @Nullable LegacySettings loadLegacySettings() {
        Path path = Path.of(skySellWands.getDataFolder() + File.separator + "settings.yml");

        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        try {
            return loader.load().get(LegacySettings.class);
        } catch (ConfigurateException e) {
            logger.error(AdventureUtil.serialize("Failed to load legacy plugin settings: " + e.getMessage()));
            return null;
        }
    }

    /**
     * Saves the provided {@link Settings} to the disk.
     * @param settings The {@link Settings} to save.
     */
    private void saveSettings(@NotNull Settings settings) {
        Path path = Path.of(skySellWands.getDataFolder() + File.separator + "settings.yml");

        YamlConfigurationLoader loader = ConfigurationUtility.getYamlConfigurationLoader(path);
        ConfigurationNode node = loader.createNode();

        try {
            node.set(settings);
            loader.save(node);
        } catch (ConfigurateException e) {
            throw new RuntimeException(e);
        }
    }
}
