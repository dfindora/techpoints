package com.goldensands.sponge.modules;

import com.goldensands.sponge.Techpoints;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class ModuleHandler
{
    private final Techpoints plugin;
    private WailaModule wailaModule;
    private TechpointsModule techpointsModule;

    public ModuleHandler(Techpoints plugin)
    {
        this.plugin = plugin;
    }

    public void setup()
    {
        techpointsModule = new TechpointsModule(plugin);
        Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.AQUA, "Techpoints module loaded."));
        wailaModule = new WailaModule(plugin);
        Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.AQUA, "Waila module loaded."));
    }

    public WailaModule getWailaModule()
    {
        return wailaModule;
    }

    public TechpointsModule getTechpointsModule()
    {
        return techpointsModule;
    }
}
