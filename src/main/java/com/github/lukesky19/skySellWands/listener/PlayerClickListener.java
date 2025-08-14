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
import com.github.lukesky19.skySellWands.configuration.Locale;
import com.github.lukesky19.skySellWands.configuration.Settings;
import com.github.lukesky19.skySellWands.manager.HookManager;
import com.github.lukesky19.skySellWands.manager.LocaleManager;
import com.github.lukesky19.skySellWands.manager.SettingsManager;
import com.github.lukesky19.skySellWands.util.WandKeys;
import com.github.lukesky19.skylib.api.adventure.AdventureUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

/**
 * This class listens for when a player uses a sell wand.
 */
public class PlayerClickListener implements Listener {
    private final @NotNull SkySellWands skySellWands;
    private final @NotNull SettingsManager settingsManager;
    private final @NotNull LocaleManager localeManager;
    private final @NotNull HookManager hookManager;

    /**
     * Constructor
     * @param skySellWands A {@link SkySellWands} instance.
     * @param settingsManager A {@link SettingsManager} instance.
     * @param localeManager A {@link LocaleManager} instance.
     * @param hookManager A {@link HookManager} instance.
     */
    public PlayerClickListener(
            @NotNull SkySellWands skySellWands,
            @NotNull SettingsManager settingsManager,
            @NotNull LocaleManager localeManager,
            @NotNull HookManager hookManager) {
        this.skySellWands = skySellWands;
        this.localeManager = localeManager;
        this.settingsManager = settingsManager;
        this.hookManager = hookManager;
    }

    /**
     * Listens to when a player clicks a block with a sell wand.
     * @param playerInteractEvent A {@link PlayerInteractEvent}.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onWandClick(PlayerInteractEvent playerInteractEvent) {
        // Get the player
        Player player = playerInteractEvent.getPlayer();
        // Get the clicked block
        Block block = playerInteractEvent.getClickedBlock();
        if(block == null) return;
        // Check if the clicked block is a container
        if(!(block.getState() instanceof Container container)) return;
        // Get the action taken in the event
        Action action = playerInteractEvent.getAction();
        // Only try to use a sell wand if the action was a left click
        if(!action.isLeftClick()) return;
        // Get the plugin's locale
        Locale locale = localeManager.getLocale();
        // Get the plugin's settings
        Settings settings = settingsManager.getSettings();
        if(settings == null) return;

        // Get the item the player's main hand
        ItemStack handItem = player.getInventory().getItemInMainHand().clone();
        if(handItem.isEmpty()) return;
        // Get the item's ItemMeta
        ItemMeta itemMeta = handItem.getItemMeta();
        if(itemMeta == null) return;
        // Get the PersistentDataContainer of the item
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        // Check if it is a sellwand
        if(!pdc.has(WandKeys.USES.getKey())) return;

        // Cancel the event
        playerInteractEvent.setCancelled(true);

        // Check if the player can access the container before selling
        if(!hookManager.canPlayerOpen(player, block.getLocation())) {
            player.sendMessage(AdventureUtil.serialize(player, locale.prefix() + locale.noAccess()));
            return;
        }

        // Get the wand's uses
        Integer uses = pdc.get(WandKeys.USES.getKey(), PersistentDataType.INTEGER);
        if(uses == null) return; // Should never be null here, but just in-case we return if it is.

        // Get the container's inventory
        Inventory inventory = container.getInventory();
        // If the container's inventory is empty, send a message and return.
        if(container.getInventory().isEmpty()) {
            player.sendMessage(AdventureUtil.serialize(player, locale.prefix() + locale.containerInventoryEmpty()));
            return;
        }

        // Sell the container's inventory of items
        boolean result = skySellWands.getSkyShopAPI().sellInventory(player, inventory, false);
        // Send a message if no items were sold and return.
        if(!result) {
            player.sendMessage(AdventureUtil.serialize(player, locale.prefix() + locale.noItemsSold()));
            return;
        }

        // Get the player's balance
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);

        BigDecimal bigBalance = BigDecimal.valueOf(skySellWands.getEconomy().getBalance(player));
        String bal = df.format(bigBalance);

        // Create placeholders
        List<TagResolver.Single> placeholders = List.of(Placeholder.parsed("bal", bal));

        // Send player a success message
        player.sendMessage(AdventureUtil.serialize(player,locale.prefix() + locale.sellSuccess(), placeholders));

        // If the uses are not unlimited (-1), update the number of uses
        if(uses != -1) {
            // Update the number of uses on the SellWand
            int updatedUses = uses - 1;

            // If the uses are 0, remove the item from the player's inventory
            // Otherwise we just update the number of uses on the item's lore and in the PersistentDataContainer.
            if(updatedUses == 0) {
                player.getInventory().remove(handItem);

                player.sendMessage(AdventureUtil.serialize(player, locale.prefix() + locale.wandUsedUp()));
            } else {
                List<TagResolver.Single> lorePlaceholders = List.of(Placeholder.parsed("uses", String.valueOf(updatedUses)));
                List<Component> lore = settings.item().lore().stream().map(string -> AdventureUtil.serialize(player, string, lorePlaceholders)).toList();

                itemMeta.lore(lore);

                pdc.set(WandKeys.USES.getKey(), PersistentDataType.INTEGER, updatedUses);

                handItem.setItemMeta(itemMeta);

                player.getInventory().setItemInMainHand(handItem);
            }
        }
    }
}
