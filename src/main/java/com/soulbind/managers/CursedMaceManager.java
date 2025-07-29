package com.soulbind.managers;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CursedMaceManager {
    
    private final JavaPlugin plugin;
    private final SoulFragmentManager soulFragmentManager;
    private final NamespacedKey cursedMaceKey;
    private final Map<UUID, Long> lastKillTimes;
    private final Map<UUID, Boolean> hasCursedMace;
    private File dataFile;
    private FileConfiguration dataConfig;
    
    private static final long KILL_REQUIREMENT_TIME = 24 * 60 * 60 * 1000L; // 24 hours in milliseconds
    
    public CursedMaceManager(JavaPlugin plugin, SoulFragmentManager soulFragmentManager) {
        this.plugin = plugin;
        this.soulFragmentManager = soulFragmentManager;
        this.cursedMaceKey = new NamespacedKey(plugin, "cursed_mace");
        this.lastKillTimes = new HashMap<>();
        this.hasCursedMace = new HashMap<>();
        setupDataFile();
        loadData();
        startCurseTask();
    }
    
    private void setupDataFile() {
        dataFile = new File(plugin.getDataFolder(), "cursed_mace.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create cursed mace data file!");
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
    
    private void loadData() {
        for (String key : dataConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                long lastKill = dataConfig.getLong(key + ".lastKill", 0);
                boolean hasMace = dataConfig.getBoolean(key + ".hasMace", false);
                
                lastKillTimes.put(uuid, lastKill);
                hasCursedMace.put(uuid, hasMace);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in cursed mace data: " + key);
            }
        }
    }
    
    public void saveAllData() {
        for (UUID uuid : hasCursedMace.keySet()) {
            dataConfig.set(uuid.toString() + ".lastKill", lastKillTimes.getOrDefault(uuid, 0L));
            dataConfig.set(uuid.toString() + ".hasMace", hasCursedMace.get(uuid));
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save cursed mace data!");
        }
    }
    
    public ItemStack createCursedMace() {
        ItemStack mace = new ItemStack(Material.MACE);
        ItemMeta meta = mace.getItemMeta();
        meta.setDisplayName("§4§lCursed Mace of Malediction");
        meta.setLore(Arrays.asList(
            "§7A weapon bound by dark curses.",
            "§c30% chance to permanently shatter 1 heart on hit.",
            "§4§lCURSE: §7Cannot be dropped once picked up.",
            "§4§lCURSE: §7Must kill a player every 24 hours.",
            "§4§lCURSE: §7Dying consumes a soul fragment.",
            "§8§oUse /usemace to activate its power..."
        ));
        meta.getPersistentDataContainer().set(cursedMaceKey, PersistentDataType.BYTE, (byte) 1);
        mace.setItemMeta(meta);
        return mace;
    }
    
    public boolean isCursedMace(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(cursedMaceKey, PersistentDataType.BYTE);
    }
    
    public boolean hasCursedMace(Player player) {
        return hasCursedMace.getOrDefault(player.getUniqueId(), false);
    }
    
    public void bindCursedMace(Player player) {
        UUID uuid = player.getUniqueId();
        hasCursedMace.put(uuid, true);
        lastKillTimes.put(uuid, System.currentTimeMillis());
        savePlayerData(uuid);
    }
    
    public void removeCursedMace(Player player) {
        UUID uuid = player.getUniqueId();
        hasCursedMace.put(uuid, false);
        lastKillTimes.remove(uuid);
        savePlayerData(uuid);
    }
    
    public void recordKill(Player player) {
        if (!hasCursedMace(player)) return;
        
        UUID uuid = player.getUniqueId();
        lastKillTimes.put(uuid, System.currentTimeMillis());
        savePlayerData(uuid);
    }
    
    public boolean useMaceAbility(Player player) {
        if (!hasCursedMace(player)) return false;
        
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (!isCursedMace(mainHand)) return false;
        
        // 30% chance to permanently shatter 1 heart
        if (Math.random() <= 0.3) {
            // Find target player in front of the player (simplified - just check nearby players)
            Player target = null;
            double closestDistance = Double.MAX_VALUE;
            
            for (Player nearbyPlayer : player.getWorld().getPlayers()) {
                if (nearbyPlayer.equals(player)) continue;
                
                double distance = player.getLocation().distance(nearbyPlayer.getLocation());
                if (distance <= 4.0 && distance < closestDistance) {
                    closestDistance = distance;
                    target = nearbyPlayer;
                }
            }
            
            if (target != null) {
                // Permanently remove 1 heart
                double currentMaxHealth = target.getAttribute(Attribute.MAX_HEALTH).getBaseValue();
                if (currentMaxHealth > 2.0) { // Don't let them go below 1 heart
                    target.getAttribute(Attribute.MAX_HEALTH).setBaseValue(currentMaxHealth - 2.0);
                    
                    // Heal target if their current health exceeds new max
                    if (target.getHealth() > currentMaxHealth - 2.0) {
                        target.setHealth(currentMaxHealth - 2.0);
                    }
                    
                    target.sendMessage("§4💀 §7The Cursed Mace has §4permanently shattered §7one of your hearts!");
                    player.sendMessage("§4⚡ §7The mace's curse takes effect! §c1 heart shattered!");
                    return true;
                }
            }
        }
        
        player.sendMessage("§7The mace thirsts for blood but finds no target...");
        return false;
    }
    
    private void savePlayerData(UUID uuid) {
        dataConfig.set(uuid.toString() + ".lastKill", lastKillTimes.getOrDefault(uuid, 0L));
        dataConfig.set(uuid.toString() + ".hasMace", hasCursedMace.getOrDefault(uuid, false));
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save cursed mace data for player " + uuid);
        }
    }
    
    private void startCurseTask() {
        // Run every minute to check curse requirements
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            long currentTime = System.currentTimeMillis();
            
            for (UUID uuid : new HashMap<>(hasCursedMace).keySet()) {
                if (!hasCursedMace.get(uuid)) continue;
                
                Player player = plugin.getServer().getPlayer(uuid);
                if (player == null) continue;
                
                long lastKill = lastKillTimes.getOrDefault(uuid, currentTime);
                long timeSinceKill = currentTime - lastKill;
                
                if (timeSinceKill >= KILL_REQUIREMENT_TIME) {
                    // Silently consume 1 stat fragment
                    if (soulFragmentManager.getStatFragments(player) > 0) {
                        soulFragmentManager.removeStatFragments(player, 1);
                    }
                    
                    // Reset the timer
                    lastKillTimes.put(uuid, currentTime);
                    savePlayerData(uuid);
                }
            }
        }, 1200L, 1200L); // Check every minute (1200 ticks)
    }
}