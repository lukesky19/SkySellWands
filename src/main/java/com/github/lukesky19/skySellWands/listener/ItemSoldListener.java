package com.github.lukesky19.skySellWands.listener;

import com.github.lukesky19.skySellWands.manager.WandKeys;
import com.github.lukesky19.skyshop.event.ItemSoldEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;

public class ItemSoldListener implements Listener {
    @EventHandler
    public void onItemSold(ItemSoldEvent event) {
        PersistentDataContainer pdc = event.getItemStack().getItemMeta().getPersistentDataContainer();
        // Check if it is a sellwand
        if (pdc.has(WandKeys.USES.getKey())) {
            // Cancel selling sell wands
            event.setCancelled(true);
        }
    }
}
