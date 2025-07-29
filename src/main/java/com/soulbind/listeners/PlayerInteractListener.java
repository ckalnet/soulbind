package com.soulbind.listeners;

import com.soulbind.managers.InfusedGearManager;
import com.soulbind.managers.SoulFragmentManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerInteractListener implements Listener {
    
    private final InfusedGearManager infusedGearManager;
    
    public PlayerInteractListener(InfusedGearManager infusedGearManager) {
        this.infusedGearManager = infusedGearManager;
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        
        if (item == null) return;
        
        // Check if player is consuming a soul fragment
        if (((com.soulbind.SoulbindPlugin)infusedGearManager.getPlugin()).getSoulFragmentManager().isSoulFragment(item)) {
            consumeSoulFragment(player, item);
            event.setCancelled(true);
        }
    }
    
    private void consumeSoulFragment(Player player, ItemStack fragment) {
        // Add permanent heart (+2 health points = +1 heart)
        double currentMaxHealth = player.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(currentMaxHealth + 2.0);
        
        // Heal the player by the amount gained
        double currentHealth = player.getHealth();
        player.setHealth(Math.min(currentHealth + 2.0, player.getAttribute(Attribute.MAX_HEALTH).getValue()));
        
        // Remove one fragment from the stack
        if (fragment.getAmount() > 1) {
            fragment.setAmount(fragment.getAmount() - 1);
        } else {
            player.getInventory().removeItem(fragment);
        }
        
        player.sendMessage("§5✦ §7You consumed a Soul Fragment and gained §c+1 permanent heart§7!");
    }
}