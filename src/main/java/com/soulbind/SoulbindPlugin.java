package com.soulbind;

import org.bukkit.plugin.java.JavaPlugin;
import com.soulbind.commands.*;
import com.soulbind.listeners.*;
import com.soulbind.managers.*;

public class SoulbindPlugin extends JavaPlugin {
    
    private SoulFragmentManager soulFragmentManager;
    private MilestoneManager milestoneManager;
    private InfusedGearManager infusedGearManager;
    private CursedMaceManager cursedMaceManager;
    
    @Override
    public void onEnable() {
        // Create plugin data folder if it doesn't exist
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        
        // Initialize managers
        soulFragmentManager = new SoulFragmentManager(this);
        milestoneManager = new MilestoneManager(this, soulFragmentManager);
        infusedGearManager = new InfusedGearManager(this);
        cursedMaceManager = new CursedMaceManager(this, soulFragmentManager);
        
        // Register commands
        getCommand("bindsoul").setExecutor(new BindSoulCommand(soulFragmentManager, milestoneManager));
        getCommand("withdrawsoul").setExecutor(new WithdrawSoulCommand(soulFragmentManager, milestoneManager));
        getCommand("soulsight").setExecutor(new SoulSightCommand(soulFragmentManager, milestoneManager));
        getCommand("bloodrage").setExecutor(new BloodrageCommand(milestoneManager));
        getCommand("usemace").setExecutor(new UseMaceCommand(cursedMaceManager));
        
        // Register event listeners
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(soulFragmentManager, cursedMaceManager), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(infusedGearManager), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(milestoneManager), this);
        getServer().getPluginManager().registerEvents(new CursedMaceListener(cursedMaceManager), this);
        getServer().getPluginManager().registerEvents(new InfusedGearListener(infusedGearManager), this);
        
        getLogger().info("Soulbind SMP Plugin has been enabled!");
    }
    
    @Override
    public void onDisable() {
        if (soulFragmentManager != null) {
            soulFragmentManager.saveAllData();
        }
        if (cursedMaceManager != null) {
            cursedMaceManager.saveAllData();
        }
        getLogger().info("Soulbind SMP Plugin has been disabled!");
    }
    
    public SoulFragmentManager getSoulFragmentManager() {
        return soulFragmentManager;
    }
    
    public MilestoneManager getMilestoneManager() {
        return milestoneManager;
    }
    
    public InfusedGearManager getInfusedGearManager() {
        return infusedGearManager;
    }
    
    public CursedMaceManager getCursedMaceManager() {
        return cursedMaceManager;
    }
}