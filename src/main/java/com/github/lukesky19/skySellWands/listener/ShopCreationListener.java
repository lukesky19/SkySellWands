package com.github.lukesky19.skySellWands.listener;

import com.ghostchu.quickshop.api.event.ShopPreCreateEvent;
import com.github.lukesky19.skySellWands.manager.WandKeys;
import com.github.lukesky19.skylib.format.FormatUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.Optional;

public class ShopCreationListener implements Listener {
    @EventHandler
    public void onShopPreCreate(ShopPreCreateEvent event) {
        // Get the player involved with the event
        Optional<Player> optionalPlayer = event.getCreator().getBukkitPlayer();
        if(optionalPlayer.isEmpty()) return;
        Player player = optionalPlayer.get();

        // Get the item the player's main hand
        ItemStack handItem = player.getInventory().getItemInMainHand().clone();
        // Get the item's ItemMeta
        ItemMeta itemMeta = handItem.getItemMeta();
        if(itemMeta != null) {
            // Get the PersistentDataContainer of the item
            PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
            // Check if it is a sellwand
            if (pdc.has(WandKeys.USES.getKey())) {
                // Cancel the shop creation event
                event.setCancelled(true, FormatUtil.format("Item is a sell wand."));
            }
        }
    }
}
