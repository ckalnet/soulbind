package com.soulbind.managers;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

public class SoulFragmentManager {
    
    private final JavaPlugin plugin;
    private final NamespacedKey soulFragmentKey;
    private final Map<UUID, Integer> statFragments;
    private File dataFile;
    private FileConfiguration dataConfig;
    
    public SoulFragmentManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.soulFragmentKey = new NamespacedKey(plugin, "soul_fragment");
        this.statFragments = new HashMap<>();
        setupDataFile();
        loadData();
    }
    
    private void setupDataFile() {
        dataFile = new File(plugin.getDataFolder(), "soul_fragments.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create soul fragments data file!");
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
    
    private void loadData() {
        for (String key : dataConfig.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                int fragments = dataConfig.getInt(key);
                statFragments.put(uuid, fragments);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in soul fragments data: " + key);
            }
        }
    }
    
    public void saveAllData() {
        for (Map.Entry<UUID, Integer> entry : statFragments.entrySet()) {
            dataConfig.set(entry.getKey().toString(), entry.getValue());
        }
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save soul fragments data!");
        }
    }
    
    public ItemStack createSoulFragment() {
        ItemStack fragment = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = fragment.getItemMeta();
        meta.setDisplayName("§5§lSoul Fragment");
        meta.setLore(Arrays.asList(
            "§7A fragment of a defeated player's soul.",
            "§7Right-click to consume for +1 permanent heart.",
            "§7Use /bindsoul to convert to stat fragments."
        ));
        meta.getPersistentDataContainer().set(soulFragmentKey, PersistentDataType.BYTE, (byte) 1);
        fragment.setItemMeta(meta);
        return fragment;
    }
    
    public boolean isSoulFragment(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(soulFragmentKey, PersistentDataType.BYTE);
    }
    
    public int getStatFragments(Player player) {
        return statFragments.getOrDefault(player.getUniqueId(), 0);
    }
    
    public void addStatFragments(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int current = statFragments.getOrDefault(uuid, 0);
        statFragments.put(uuid, current + amount);
        savePlayerData(uuid);
    }
    
    public boolean removeStatFragments(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        int current = statFragments.getOrDefault(uuid, 0);
        if (current < amount) return false;
        
        statFragments.put(uuid, current - amount);
        savePlayerData(uuid);
        return true;
    }
    
    public void setStatFragments(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        statFragments.put(uuid, Math.max(0, amount));
        savePlayerData(uuid);
    }
    
    private void savePlayerData(UUID uuid) {
        dataConfig.set(uuid.toString(), statFragments.get(uuid));
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save data for player " + uuid);
        }
    }
    
    public int convertItemsToStats(Player player) {
        int converted = 0;
        ItemStack[] contents = player.getInventory().getContents();
        
        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (isSoulFragment(item)) {
                converted += item.getAmount();
                contents[i] = null;
            }
        }
        
        player.getInventory().setContents(contents);
        
        if (converted > 0) {
            addStatFragments(player, converted);
        }
        
        return converted;
    }
    
    public boolean withdrawFragments(Player player, int amount) {
        if (!removeStatFragments(player, amount)) return false;
        
        while (amount > 0) {
            int stackSize = Math.min(amount, 64);
            ItemStack fragment = createSoulFragment();
            fragment.setAmount(stackSize);
            
            Map<Integer, ItemStack> excess = player.getInventory().addItem(fragment);
            if (!excess.isEmpty()) {
                for (ItemStack excessItem : excess.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), excessItem);
                }
            }
            
            amount -= stackSize;
        }
        
        return true;
    }
}