package com.soulbind.managers;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public class InfusedGearManager {
    
    private final JavaPlugin plugin;
    private final NamespacedKey swordOfDrainedKey;
    private final NamespacedKey bootsOfEchoesKey;
    
    public InfusedGearManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.swordOfDrainedKey = new NamespacedKey(plugin, "sword_of_drained");
        this.bootsOfEchoesKey = new NamespacedKey(plugin, "boots_of_echoes");
    }
    
    public JavaPlugin getPlugin() {
        return plugin;
    }
    
    public ItemStack createSwordOfTheDrained() {
        ItemStack sword = new ItemStack(Material.NETHERITE_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.setDisplayName("§4§lSword of the Drained");
        meta.setLore(Arrays.asList(
            "§7A blade that hungers for life essence.",
            "§7Killing a player steals §c1 permanent heart§7.",
            "§c§oInfused with dark magic..."
        ));
        meta.getPersistentDataContainer().set(swordOfDrainedKey, PersistentDataType.BYTE, (byte) 1);
        sword.setItemMeta(meta);
        return sword;
    }
    
    public ItemStack createBootsOfEchoes() {
        ItemStack boots = new ItemStack(Material.NETHERITE_BOOTS);
        ItemMeta meta = boots.getItemMeta();
        meta.setDisplayName("§b§lBoots of Echoes");
        meta.setLore(Arrays.asList(
            "§7These boots echo with ancient speed.",
            "§7Grants permanent §bSpeed II§7 while worn.",
            "§b§oInfused with wind magic..."
        ));
        meta.getPersistentDataContainer().set(bootsOfEchoesKey, PersistentDataType.BYTE, (byte) 1);
        boots.setItemMeta(meta);
        return boots;
    }
    
    public boolean isSwordOfTheDrained(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(swordOfDrainedKey, PersistentDataType.BYTE);
    }
    
    public boolean isBootsOfEchoes(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(bootsOfEchoesKey, PersistentDataType.BYTE);
    }
    
    public void handleSwordOfDrainedKill(Player killer, Player victim) {
        if (!isSwordOfTheDrained(killer.getInventory().getItemInMainHand())) return;
        
        // Steal 1 permanent heart from victim
        double victimMaxHealth = victim.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
        if (victimMaxHealth > 2.0) { // Don't let them go below 1 heart
            victim.getAttribute(Attribute.MAX_HEALTH).setBaseValue(victimMaxHealth - 2.0);
            
            // Heal victim if their current health exceeds new max
            if (victim.getHealth() > victimMaxHealth - 2.0) {
                victim.setHealth(victimMaxHealth - 2.0);
            }
        }
        
        // Give 1 permanent heart to killer
        double killerMaxHealth = killer.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
        killer.getAttribute(Attribute.MAX_HEALTH).setBaseValue(killerMaxHealth + 2.0);
        
        // Heal killer by the amount gained
        double killerCurrentHealth = killer.getHealth();
        killer.setHealth(Math.min(killerCurrentHealth + 2.0, killer.getAttribute(Attribute.MAX_HEALTH).getValue()));
        
        killer.sendMessage("§4⚔ §7The Sword of the Drained steals §c1 heart §7from your victim!");
        victim.sendMessage("§4💀 §7The Sword of the Drained has stolen §c1 heart §7from you!");
    }
    
    public void updateBootsOfEchoesEffect(Player player) {
        ItemStack boots = player.getInventory().getBoots();
        
        if (isBootsOfEchoes(boots)) {
            // Apply Speed II effect
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1, true, false));
        } else {
            // Remove Speed II from boots (but keep milestone Speed I if applicable)
            if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                PotionEffect currentSpeed = player.getPotionEffect(PotionEffectType.SPEED);
                if (currentSpeed != null && currentSpeed.getAmplifier() >= 1) {
                    player.removePotionEffect(PotionEffectType.SPEED);
                    // Re-apply milestone Speed I if they have 10+ fragments
                    if (((com.soulbind.SoulbindPlugin)plugin).getSoulFragmentManager().getStatFragments(player) >= 10) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, true, false));
                    }
                }
            }
        }
    }
}