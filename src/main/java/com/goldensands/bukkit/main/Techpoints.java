package com.goldensands.bukkit.main;

import com.goldensands.config.SpigotConfigManager;
import com.goldensands.bukkit.events.EventListener;
import com.goldensands.bukkit.modules.ModuleHandler;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Techpoints extends JavaPlugin
{
    //ConfigManager for techpoints.yml.
    private SpigotConfigManager configManager;
    //Main command handler.
    private final Commands commands = new Commands(this);
    //Module handler - used to process most things
    private ModuleHandler moduleHandler;

    @Override
    public void onEnable()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");
            getLogger().info("org.sqlite.JDBC loaded.");
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        getLogger().info("registering commands...");
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        getCommand(commands.techPoints).setExecutor(commands);
        getCommand(commands.techList).setExecutor(commands);
        getCommand(commands.techLimit).setExecutor(commands);
        getCommand(commands.techConfig).setExecutor(commands);
        getCommand(commands.techWand).setExecutor(commands);
        getCommand(commands.techChunk).setExecutor(commands);
        getCommand(commands.techVersion).setExecutor(commands);
        getCommand(commands.techWaila).setExecutor(commands);
        getCommand(commands.techHotbar).setExecutor(commands);
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
        saveDefaultConfig();
        configManager = new SpigotConfigManager(this);
        configManager.setup();
        moduleHandler = new ModuleHandler(this);
        moduleHandler.setup();
        saveConfig();
    }

    /**
     *
     * @return the techpoints.yml ConfigManager.
     */
    public SpigotConfigManager getConfigManager()
    {
        return configManager;
    }

    public ModuleHandler getModuleHandler()
    {
        return moduleHandler;
    }
}
