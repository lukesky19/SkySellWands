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

import com.github.lukesky19.skySellWands.configuration.manager.LocaleManager;
import com.github.lukesky19.skySellWands.configuration.manager.SettingsManager;
import com.github.lukesky19.skySellWands.configuration.record.Locale;
import com.github.lukesky19.skySellWands.configuration.record.Settings;
import com.github.lukesky19.skylib.format.FormatUtil;
import com.github.lukesky19.skylib.player.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WandManager {
    private final SettingsManager settingsManager;
    private final LocaleManager localeManager;

    public WandManager(SettingsManager settingsManager, LocaleManager localeManager) {
        this.settingsManager = settingsManager;
        this.localeManager = localeManager;
    }

    /**
     * Gives a sellwand to a player with a limited number of uses.
     * @param sender The CommandSender who ran the command to give a sellwand to the target.
     * @param target The player to give the sellwand to.
     * @param uses The number of uses for this sellwand.
     * @param amount The number of sellwands to give.
     */
    public void giveWand(CommandSender sender, Player target, int uses, int amount) {
        // Get the plugin's settings
        Locale locale = localeManager.getLocale();
        Settings settings = settingsManager.getSettings();
        if(settings == null) return;

        // Get the configured material
        Material material = Material.getMaterial(settings.item().material());
        if(material == null) return;

        // Create the ItemStack
        ItemStack itemStack = new ItemStack(material);
        // Get the ItemMeta
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Set the item name
        if(settings.item().name() != null) {
            itemMeta.displayName(FormatUtil.format(settings.item().name()));
        }

        // Set the lore
        List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("uses", String.valueOf(uses)), Placeholder.parsed("player_name", target.getName()));
        List<Component> lore = settings.item().lore().stream().map(string -> FormatUtil.format(string, placeholders)).toList();
        itemMeta.lore(lore);

        // Save the number of uses to the PDC
        @NotNull PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(WandKeys.USES.getKey(), PersistentDataType.INTEGER, uses);

        // Set if the item is enchanted or not
        itemMeta.setEnchantmentGlintOverride(settings.item().enchanted());

        itemMeta.setMaxStackSize(1);

        // Set the item's ItemMeta
        itemStack.setItemMeta(itemMeta);

        // Set the number of sellwands to give.
        itemStack.setAmount(amount);

        // Give the target the item.
        PlayerUtil.giveItem(target, itemStack, amount);

        // Send the target a message that a sellwand was given
        target.sendMessage(FormatUtil.format(locale.prefix() + locale.givenWand(), placeholders));

        // Send the sender a message that a sellwand was given
        sender.sendMessage(FormatUtil.format(locale.prefix() + locale.playerGivenWand(), placeholders));
    }

    /**
     * Gives a sellwand to a player with an unlimited number of uses.
     * @param sender The CommandSender who ran the command to give a sellwand to the target.
     * @param target The player to give the sellwand to.
     * @param amount The number of sellwands to give.
     */
    public void giveUnlimitedWand(CommandSender sender, Player target, int amount) {
        // Get the plugin's settings
        Locale locale = localeManager.getLocale();
        Settings settings = settingsManager.getSettings();
        if(settings == null) return;

        // Get the configured material
        Material material = Material.getMaterial(settings.item().material());
        if(material == null) return;

        // Create the ItemStack
        ItemStack itemStack = new ItemStack(material);
        // Get the ItemMeta
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Set the item name
        if(settings.item().name() != null) {
            itemMeta.displayName(FormatUtil.format(settings.item().name()));
        }

        // Set the lore
        List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("uses", "unlimited"), Placeholder.parsed("player_name", target.getName()));
        List<Component> lore = settings.item().lore().stream().map(string -> FormatUtil.format(string, placeholders)).toList();
        itemMeta.lore(lore);

        // Save the number of uses to the PDC
        @NotNull PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(WandKeys.USES.getKey(), PersistentDataType.INTEGER, -1);

        // Set if the item is enchanted or not
        itemMeta.setEnchantmentGlintOverride(settings.item().enchanted());

        itemMeta.setMaxStackSize(1);

        // Set the item's ItemMeta
        itemStack.setItemMeta(itemMeta);

        // Set the number of sellwands to give.
        itemStack.setAmount(amount);

        // Give the target the item.
        PlayerUtil.giveItem(target, itemStack, amount);

        // Send the target a message that a sellwand was given
        target.sendMessage(FormatUtil.format(locale.prefix() + locale.givenWand(), placeholders));

        // Send the sender a message that a sellwand was given
        sender.sendMessage(FormatUtil.format(locale.prefix() + locale.playerGivenWand(), placeholders));
    }
}
