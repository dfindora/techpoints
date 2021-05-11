package com.goldensands.sponge;

//import com.goldensands.sponge.commands.TechpointsCommand;
import com.goldensands.sponge.commands.VersionCommand;
import com.goldensands.sponge.commands.WailaCommand;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@Plugin(id="techpoints", name="Techpoints", version = "1.2.0")
public class Techpoints
{
    PluginContainer plugin = Objects.requireNonNull(Sponge.getPluginManager()
            .getPlugin("techpoints").orElse(null));
    private final Path privateConfigDir = Sponge.getConfigManager().getPluginConfig(plugin).getConfigPath().getParent();
    private YAMLConfigurationLoader mainConfig;
    private YAMLConfigurationLoader techpointsConfig;
    ConfigurationNode mainNode;
    ConfigurationNode techpointsNode;

    CommandSpec versionCommand = CommandSpec.builder()
            .description(Text.of("Techpoints version command."))
            .permission("techpoints.techversion")
            .executor(new VersionCommand())
            .build();
    CommandSpec wailaCommand = CommandSpec.builder()
            .description(Text.of("gives info on the block you are looking at."))
            .permission("techpoints.techpoints.waila")
            .executor(new WailaCommand())
            .build();
/*
    CommandSpec techpointsCommand = CommandSpec.builder()
            .description(Text.of("main techpoints command."))
            .permission("techpoints.techpoints")
            .executor(new TechpointsCommand())
            .child(wailaCommand, "waila")
            .build();
*/


    @Listener
    public void onServerInitilization(GameInitializationEvent event)
    {
        Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.AQUA, "Techpoints started."));
        Sponge.getCommandManager().register(this, versionCommand, "techversion", "tversion", "tv");
        //Sponge.getCommandManager().register(this, techpointsCommand, "techpoints", "tpoints", "tpts");
        Sponge.getCommandManager().register(this, wailaCommand, "techwaila", "twaila", "tw");
        Path mainConfigPath = new File(privateConfigDir.toAbsolutePath().toString() + File.separator
                + "config.yml").toPath();
        Path techpointsConfigPath = new File(privateConfigDir.toAbsolutePath().toString() + File.separator
                + "techpoints.yml").toPath();
        Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.AQUA, privateConfigDir.toAbsolutePath()));
        try
        {
            Objects.requireNonNull(Sponge.getAssetManager()
                    .getAsset(plugin, "config.yml")
                    .orElse(null))
                    .copyToFile(mainConfigPath, false, true);
            Objects.requireNonNull(Sponge.getAssetManager()
                    .getAsset(plugin, "techpoints.yml")
                    .orElse(null))
                    .copyToFile(techpointsConfigPath, false, true);
            this.mainConfig = YAMLConfigurationLoader.builder().setPath(mainConfigPath).build();
            this.techpointsConfig = YAMLConfigurationLoader.builder().setPath(techpointsConfigPath).build();
            mainNode = this.mainConfig.createEmptyNode(ConfigurationOptions.defaults());
            techpointsNode = this.techpointsConfig.createEmptyNode(ConfigurationOptions.defaults());
            mainNode = this.mainConfig.load();
            techpointsNode = this.techpointsConfig.load();
            //SpigotConfigManager configManager = new SpigotConfigManager(techpointsConfigPath.toFile());
            List<String> basicTechPointItems = techpointsNode.getNode("BasicTechPointItems")
                    .getList(TypeToken.of(String.class));
            Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.AQUA, basicTechPointItems.size()));
            for (String basicTechPointItem : basicTechPointItems)
            {
                Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.AQUA, basicTechPointItem));
            }
        }
        catch(IOException | ObjectMappingException e)
        {
            e.printStackTrace();
        }
        Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.AQUA, this.mainConfig.toString()));

    }

    @Listener
    public void onServerStop(GameStoppedServerEvent event)
    {

    }
}
