package com.goldensands.sponge;

//import com.goldensands.sponge.commands.TechpointsCommand;

import com.goldensands.sponge.commands.ConfigCommand;
import com.goldensands.sponge.commands.VersionCommand;
import com.goldensands.sponge.commands.WailaCommand;
import com.goldensands.sponge.config.SpongeConfigManager;
import com.goldensands.sponge.modules.ModuleHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Objects;

@Plugin(id="techpoints", name="Techpoints", version = "1.2.0")
public class Techpoints
{
    PluginContainer plugin = Objects.requireNonNull(Sponge.getPluginManager()
            .getPlugin("techpoints").orElse(null));
    SpongeConfigManager configManager;
    ModuleHandler moduleHandler;

    CommandSpec versionCommand = CommandSpec.builder()
            .description(Text.of("Techpoints version command."))
            .permission("techpoints.techversion")
            .executor(new VersionCommand())
            .build();
    CommandSpec wailaCommand = CommandSpec.builder()
            .description(Text.of("gives info on the block you are looking at."))
            .permission("techpoints.techwaila")
            .executor(new WailaCommand(this))
            .build();
    CommandSpec configCommand = CommandSpec.builder()
            .description(Text.of("controls the config."))
            .permission("techpoints.techconfig")
            .executor(new ConfigCommand(this))
            .arguments(
                    GenericArguments.onlyOne(GenericArguments.string(Text.of("type")))
            )
            .build();
/*
    CommandSpec techpointsCommand = CommandSpec.builder()
            .description(Text.of("main techpoints command."))
            .permission("techpoints.techpoints")
            .executor(new TechpointsCommand(this))
            .child(wailaCommand, "waila")
            .build();
*/


    @Listener
    public void onServerInitilization(GameInitializationEvent event)
    {
        Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.AQUA, "Techpoints started."));
        Sponge.getCommandManager().register(this, versionCommand, "techversion", "tversion", "tv");
        Sponge.getCommandManager().register(this, wailaCommand, "techwaila", "twaila", "tw");
        Sponge.getCommandManager().register(this, configCommand, "techconfig", "tconfig", "tcfg");
        //Sponge.getCommandManager().register(this, techpointsCommand, "techpoints", "tpoints", "tpts");
        configManager = new SpongeConfigManager(plugin);
        configManager.setup();
        moduleHandler = new ModuleHandler(this);
        moduleHandler.setup();
    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event)
    {

    }

    public PluginContainer getPluginContainer()
    {
        return plugin;
    }

    public SpongeConfigManager getConfigManager()
    {
        return configManager;
    }

    public ModuleHandler getModuleHandler()
    {
        return moduleHandler;
    }
}
