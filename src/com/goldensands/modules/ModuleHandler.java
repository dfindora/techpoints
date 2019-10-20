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
        techpointsModule = new TechpointsModule(plugin);
    }

    /**
     *
     * @return the file module
     */
    public FileModule getFileModule()
    {
        return fileModule;
    }

    public TechpointsModule getTechpointsModule()
    {
        return techpointsModule;
    }
}
