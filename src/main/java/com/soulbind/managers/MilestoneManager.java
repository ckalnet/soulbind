package com.soulbind.managers;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MilestoneManager {
    
    private final JavaPlugin plugin;
    private final SoulFragmentManager soulFragmentManager;
    private final Map<UUID, Long> bloodrageCooldowns;
    
    public MilestoneManager(JavaPlugin plugin, SoulFragmentManager soulFragmentManager) {
        this.plugin = plugin;
        this.soulFragmentManager = soulFragmentManager;
        this.bloodrageCooldowns = new HashMap<>();
    }
    
    public void updatePlayerBuffs(Player player) {
        int fragments = soulFragmentManager.getStatFragments(player);
        
        // Remove existing milestone effects
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.RESISTANCE);
        
        // Apply milestone buffs based on fragment count
        if (fragments >= 10) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0, true, false));
        }
        
        if (fragments >= 20) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, Integer.MAX_VALUE, 0, true, false));
        }
        
        // Update max health
        updateMaxHealth(player, fragments);
    }
    
    private void updateMaxHealth(Player player, int fragments) {
        double baseHealth = 20.0; // Base 10 hearts
        double bonusHealth = 0.0;
        
        // +5 max HP at 25 fragments
        if (fragments >= 25) {
            bonusHealth += 10.0; // 5 hearts = 10 health points
        }
        
        double newMaxHealth = baseHealth + bonusHealth;
        player.getAttribute(Attribute.MAX_HEALTH).setBaseValue(newMaxHealth);
        
        // Heal player if their current health exceeds new max
        if (player.getHealth() > newMaxHealth) {
            player.setHealth(newMaxHealth);
        }
    }
    
    public boolean canUseBloodrage(Player player) {
        int fragments = soulFragmentManager.getStatFragments(player);
        if (fragments < 30) return false;
        
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long lastUse = bloodrageCooldowns.getOrDefault(uuid, 0L);
        
        return (currentTime - lastUse) >= 120000; // 2 minutes in milliseconds
    }
    
    public boolean useBloodrage(Player player) {
        if (!canUseBloodrage(player)) return false;
        
        // Apply Strength III for 30 seconds
        player.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 600, 2)); // 30 seconds, Strength III
        
        // Set cooldown
        bloodrageCooldowns.put(player.getUniqueId(), System.currentTimeMillis());
        
        return true;
    }
    
    public long getBloodrageRemainingCooldown(Player player) {
        UUID uuid = player.getUniqueId();
        long currentTime = System.currentTimeMillis();
        long lastUse = bloodrageCooldowns.getOrDefault(uuid, 0L);
        long cooldownEnd = lastUse + 120000; // 2 minutes
        
        return Math.max(0, cooldownEnd - currentTime);
    }
    
    public String getPlayerStatus(Player player) {
        int fragments = soulFragmentManager.getStatFragments(player);
        StringBuilder status = new StringBuilder();
        
        status.append("§5§lSoul Fragment Status\n");
        status.append("§7Stat Fragments: §e").append(fragments).append("\n\n");
        status.append("§6Active Buffs:\n");
        
        if (fragments >= 10) {
            status.append("§a✓ Speed I\n");
        } else {
            status.append("§c✗ Speed I §7(Need ").append(10 - fragments).append(" more)\n");
        }
        
        if (fragments >= 20) {
            status.append("§a✓ Resistance I\n");
        } else {
            status.append("§c✗ Resistance I §7(Need ").append(20 - fragments).append(" more)\n");
        }
        
        if (fragments >= 25) {
            status.append("§a✓ +5 Max HP\n");
        } else {
            status.append("§c✗ +5 Max HP §7(Need ").append(25 - fragments).append(" more)\n");
        }
        
        if (fragments >= 30) {
            long cooldown = getBloodrageRemainingCooldown(player);
            if (cooldown > 0) {
                long seconds = cooldown / 1000;
                status.append("§e⏳ Bloodrage §7(").append(seconds).append("s cooldown)\n");
            } else {
                status.append("§a✓ Bloodrage Available\n");
            }
        } else {
            status.append("§c✗ Bloodrage §7(Need ").append(30 - fragments).append(" more)\n");
        }
        
        return status.toString();
    }
}