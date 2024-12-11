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
import com.github.lukesky19.skySellWands.configuration.record.Settings;
import com.github.lukesky19.skylib.config.ConfigurationUtility;
import com.github.lukesky19.skylib.libs.configurate.ConfigurateException;
import com.github.lukesky19.skylib.libs.configurate.yaml.YamlConfigurationLoader;

import javax.annotation.CheckForNull;
import java.io.File;
import java.nio.file.Path;

public class SettingsManager {
    private final SkySellWands skySellWands;
    private Settings settings;

    /**
     * Constructor
     * @param skySellWands The plugin's instance.
     */
    public SettingsManager(SkySellWands skySellWands) {
        this.skySellWands = skySellWands;
    }

    /**
     * A getter to get the settings configuration.
     * @return A Settings object.
     */
    @CheckForNull
    public Settings getSettings() {
        return settings;
    }

    /**
     * A method to reload the plugin's settings config.
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
            throw new RuntimeException(e);
        }
    }
}
