package com.goldensands.modules;

import com.goldensands.main.Techpoints;

/**
 * The main hub that initializes all the modules.
 */
public class ModuleHandler
{
    private Techpoints plugin;
    private TechpointsModule techpointsModule;
    private WandModule wandModule;
    private DatabaseModule databaseModule;
    private TechLimitModule techLimitModule;

    public ModuleHandler(Techpoints plugin)
    {
        this.plugin = plugin;
    }

    /**
     * initializes the modules.
     */
    public void setup()
    {
        plugin.getLogger().info("file module loaded.");
        techpointsModule = new TechpointsModule(plugin);
        plugin.getLogger().info("techpoints module loaded.");
        wandModule = new WandModule(plugin);
        plugin.getLogger().info("wand module loaded.");
        techLimitModule = new TechLimitModule(plugin);
        plugin.getLogger().info("techlimit module loaded.");
        databaseModule = new DatabaseModule(plugin);
        databaseModule.setup();
        plugin.getLogger().info("database module loaded.");
    }

    public DatabaseModule getDatabaseModule()
    {
        return databaseModule;
    }

    public TechpointsModule getTechpointsModule()
    {
        return techpointsModule;
    }

    public WandModule getWandModule()
    {
        return wandModule;
    }

    public TechLimitModule getTechLimitModule()
    {
        return techLimitModule;
    }
}
