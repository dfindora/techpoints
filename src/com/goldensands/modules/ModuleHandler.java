package com.goldensands.modules;

import com.goldensands.main.Techpoints;

/**
 * The main hub that initializes all the modules.
 */
public class ModuleHandler
{
    private Techpoints plugin;
    private FileModule fileModule;
    private TechpointsModule techpointsModule;
    private WandModule wandModule;
    private DatabaseModule databaseModule;

    public ModuleHandler(Techpoints plugin)
    {
        this.plugin = plugin;
    }

    /**
     * initializes the modules.
     */
    public void setup()
    {
        fileModule = new FileModule(plugin);
        fileModule.setup();
        databaseModule = new DatabaseModule(plugin);
        databaseModule.setup();
        techpointsModule = new TechpointsModule(plugin);
        wandModule = new WandModule(plugin);
    }

    /**
     *
     * @return the file module
     */
    public FileModule getFileModule()
    {
        return fileModule;
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
}
