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
package com.github.lukesky19.skySellWands.listener;

import com.github.lukesky19.skySellWands.SkySellWands;
import com.github.lukesky19.skySellWands.configuration.manager.LocaleManager;
import com.github.lukesky19.skySellWands.configuration.manager.SettingsManager;
import com.github.lukesky19.skySellWands.configuration.record.Locale;
import com.github.lukesky19.skySellWands.configuration.record.Settings;
import com.github.lukesky19.skySellWands.manager.WandKeys;
import com.github.lukesky19.skylib.format.FormatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class PlayerClickListener implements Listener {
    private final SkySellWands skySellWands;
    private final SettingsManager settingsManager;
    private final LocaleManager localeManager;

    public PlayerClickListener(SkySellWands skySellWands, SettingsManager settingsManager, LocaleManager localeManager) {
        this.skySellWands = skySellWands;
        this.localeManager = localeManager;
        this.settingsManager = settingsManager;
    }

    @EventHandler
    public void onWandClick(PlayerInteractEvent event) {
        // Get the player
        final Player player = event.getPlayer();
        // Get the clicked block
        final Block block = event.getClickedBlock();
        // Get the action taken in the event
        final Action action = event.getAction();
        // Get the plugin's locale
        final Locale locale = localeManager.getLocale();
        // Get the plugin's settings
        final Settings settings = settingsManager.getSettings();
        if (settings == null) return;

        // Only try to use a sell wand if the action was a left click
        if (action.isLeftClick()) {
            // Check if the clicked block is a container
            if (block != null && block.getState() instanceof Container container) {
                // Get the item the player's main hand
                ItemStack handItem = player.getInventory().getItemInMainHand().clone();
                // Get the item's ItemMeta
                ItemMeta itemMeta = handItem.getItemMeta();
                if(itemMeta != null) {
                    // Get the PersistentDataContainer of the item
                    PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
                    // Check if it is a sellwand
                    if (pdc.has(WandKeys.USES.getKey())) {
                        // Get the wand's uses
                        Integer uses = pdc.get(WandKeys.USES.getKey(), PersistentDataType.INTEGER);
                        if (uses == null) return; // Should never be null here, but just in-case we return if it is.

                        // Get the container's inventory
                        Inventory inventory = container.getInventory();
                        // Check if the container's inventory is not empty
                        if (!container.getInventory().isEmpty()) {
                            // Sell the container's inventory of items
                            boolean result = skySellWands.getSkyShopAPI().sellInventory(player, inventory);

                            // If at least one item was sold, send a success message
                            if (result) {
                                // Get the player's balance
                                double balance = skySellWands.getEconomy().getBalance(player);
                                // Create placeholders
                                List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("bal", String.valueOf(balance)));

                                // Send player a success message
                                player.sendMessage(FormatUtil.format(locale.prefix() + locale.sellSuccess(), placeholders));

                                // If the uses are not unlimited (-1), update the number of uses
                                if(uses != -1) {
                                    // Update the number of uses on the SellWand
                                    int updatedUses = uses - 1;

                                    // If the uses are 0, remove the item from the player's inventory
                                    // Otherwise we just update the number of uses on the item's lore and in the PersistentDataContainer.
                                    if (updatedUses == 0) {
                                        player.getInventory().remove(handItem);

                                        player.sendMessage(FormatUtil.format(locale.prefix() + locale.wandUsedUp()));
                                    } else {
                                        List<TagResolver.Single> lorePlaceholders = List.of(Placeholder.parsed("uses", String.valueOf(updatedUses)));
                                        List<Component> lore = settings.item().lore().stream().map(string -> FormatUtil.format(string, lorePlaceholders)).toList();

                                        itemMeta.lore(lore);

                                        pdc.set(WandKeys.USES.getKey(), PersistentDataType.INTEGER, updatedUses);

                                        handItem.setItemMeta(itemMeta);

                                        player.getInventory().setItemInMainHand(handItem);
                                    }
                                }
                            } else {
                                // Send a message if no items were sold
                                player.sendMessage(FormatUtil.format(locale.prefix() + locale.noItemsSold()));
                            }
                        } else {
                            // Send a message if the inventory is empty
                            player.sendMessage(FormatUtil.format(locale.prefix() + locale.containerInventoryEmpty()));
                        }
                    }
                }
            }
        }
    }
}
