package com.goldensands.main;

import com.goldensands.config.ConfigManager;
import com.goldensands.events.EventListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Techpoints extends JavaPlugin
{
    private ConfigManager configManager;
    private Commands commands = new Commands(this);
    @Override
    public void onEnable()
    {
        getLogger().info("registering commands...");
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        getCommand(commands.techPoints).setExecutor(commands);
        getCommand(commands.techList).setExecutor(commands);
        getCommand(commands.techLimit).setExecutor(commands);
        getCommand(commands.techConfig).setExecutor(commands);
        getLogger().info("commands registered.");
        getLogger().info("loading configs...");
        loadConfiguration();
    }

    @Override
    public void onDisable()
    {
        getLogger().info(ChatColor.RED + "Techpoints disabled.");
    }

    private void loadConfiguration()
    {
        int techPointMax = 200;
        getConfig().addDefault("MaxTechPoints", techPointMax);
        getConfig().options().copyDefaults(true);
        configManager = new ConfigManager(this);
        configManager.setup();
        saveConfig();
    }

    ConfigManager getConfigManager() {
        return configManager;
    }

    public Commands getCommands()
    {
        return commands;
    }
}
