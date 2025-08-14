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
import com.github.lukesky19.skySellWands.util.WandKeys;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import com.github.lukesky19.skylib.api.itemstack.ItemStackBuilder;
import com.github.lukesky19.skylib.api.player.PlayerUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * This class manages the creation of sell wands.
 */
public class WandManager {
    private final @NotNull ComponentLogger logger;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull LocaleManager localeManager;

    /**
     * Constructor
     * @param skySellWands A {@link SkySellWands} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     */
    public WandManager(
            @NotNull SkySellWands skySellWands,
            @NotNull SettingsManager settingsManager,
            @NotNull LocaleManager localeManager) {
        this.logger = skySellWands.getComponentLogger();
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
    }

    /**
     * Gives a sellwand to a player with a limited number of uses.
     * @param player The player to give the sellwand to.
     * @param uses The number of uses for this sellwand.
     * @param amount The number of sellwands to give.
     */
    public void giveWand(Player player, int uses, int amount) {
        // Get the plugin's settings
        Locale locale = localeManager.getLocale();
        Settings settings = settingsManager.getSettings();
        if(settings == null) return;

        // Create the placeholders list
        List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("uses", String.valueOf(uses)));

        // Create the ItemStack
        Optional<ItemStack> optionalItemStack = new ItemStackBuilder(logger).fromItemStackConfig(settings.item(), null, null, placeholders).setMaxStackSize(1).buildItemStack();
        if(optionalItemStack.isEmpty()) {
            logger.error(AdventureUtil.serialize("Failed to create the ItemStack for the sell wand. Double-check your configuration."));
            player.sendMessage(AdventureUtil.serialize(locale.prefix() + "<red>Failed to create the ItemStack for the sell wand. Double-check your configuration.</red>"));
            return;
        }

        ItemStack itemStack = optionalItemStack.get();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) {
            logger.error(AdventureUtil.serialize("Failed to get the ItemStack's ItemMeta for the sell wand. Double-check your configuration."));
            player.sendMessage(AdventureUtil.serialize(locale.prefix() + "<red>Failed to get the ItemStack's ItemMeta for the sell wand. Double-check your configuration.</red>"));
            return;
        }

        // Save the number of uses to the PDC
        @NotNull PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(WandKeys.USES.getKey(), PersistentDataType.INTEGER, uses);

        // Set the ItemStack's ItemMeta
        itemStack.setItemMeta(itemMeta);

        // Give the Player the item.
        PlayerUtil.giveItem(player.getInventory(), itemStack, amount, player.getLocation());

        // Send the player a message that a sellwand was given
        player.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.givenWand(), placeholders));
    }

    /**
     * Gives a sellwand to a player with an unlimited number of uses.
     * @param player The player to give the sellwand to.
     * @param amount The number of sellwands to give.
     */
    public void giveUnlimitedWand(Player player, int amount) {
        // Get the plugin's settings
        Locale locale = localeManager.getLocale();
        Settings settings = settingsManager.getSettings();
        if(settings == null) return;

        // Create the placeholders list
        List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("uses", "unlimited"));

        // Create the ItemStack
        Optional<ItemStack> optionalItemStack = new ItemStackBuilder(logger).fromItemStackConfig(settings.item(), null, null, placeholders).setMaxStackSize(1).buildItemStack();
        if(optionalItemStack.isEmpty()) {
            logger.error(AdventureUtil.serialize("Failed to create the ItemStack for the sell wand. Double-check your configuration."));
            player.sendMessage(AdventureUtil.serialize(locale.prefix() + "<red>Failed to create the ItemStack for the sell wand. Double-check your configuration.</red>"));
            return;
        }

        ItemStack itemStack = optionalItemStack.get();
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta == null) {
            logger.error(AdventureUtil.serialize("Failed to get the ItemStack's ItemMeta for the sell wand. Double-check your configuration."));
            player.sendMessage(AdventureUtil.serialize(locale.prefix() + "<red>Failed to get the ItemStack's ItemMeta for the sell wand. Double-check your configuration.</red>"));
            return;
        }

        // Save the number of uses to the PDC
        @NotNull PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(WandKeys.USES.getKey(), PersistentDataType.INTEGER, -1);

        // Set the ItemStack's ItemMeta
        itemStack.setItemMeta(itemMeta);

        // Give the Player the item.
        PlayerUtil.giveItem(player.getInventory(), itemStack, amount, player.getLocation());

        // Send the player a message that a sellwand was given
        player.sendMessage(AdventureUtil.serialize(locale.prefix() + locale.givenWand(), placeholders));
    }
}
