package com.goldensands.sponge.config;


import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializers;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class SpongeConfigManager
{
    private final PluginContainer plugin;
    private final Path privateConfigDir;
    ConfigurationNode configRoot;
    List<TechPointItem> techPointItems;
    List<MultiBlock> multiBlocks;

    public SpongeConfigManager(PluginContainer plugin)
    {
        this.plugin = plugin;
        privateConfigDir = Sponge.getConfigManager().getPluginConfig(plugin).getConfigPath().getParent();
    }

    public void setup()
    {
        //register types
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(TechPointItem.class), new TechPointItemSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(MultiBlock.class), new MultiBlockSerializer());
        TypeSerializers.getDefaultSerializers().registerType(TypeToken.of(Trait.class), new TraitSerializer());

        //config paths
        Path mainConfigPath = new File(privateConfigDir.toAbsolutePath().toString() + File.separator
                + "config.conf").toPath();
        Path techpointsConfigPath = new File(privateConfigDir.toAbsolutePath().toString() + File.separator
                + "techpoints.conf").toPath();

        Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.AQUA, "config directory = "
                + privateConfigDir.toAbsolutePath()));
        try
        {
            //pull default configs from plugin and create if none exist
            Objects.requireNonNull(Sponge.getAssetManager()
                    .getAsset(plugin, "config.conf")
                    .orElse(null))
                    .copyToFile(mainConfigPath, false, true);
            Objects.requireNonNull(Sponge.getAssetManager()
                    .getAsset(plugin, "techpoints.conf")
                    .orElse(null))
                    .copyToFile(techpointsConfigPath, false, true);

            loadTechPoints(mainConfigPath, techpointsConfigPath);
            Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.AQUA, "Config.conf loaded."));
            Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.AQUA, "Techpoints.conf loaded."));

            //print config to console
            Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.LIGHT_PURPLE, "number of techPointItems = "
                    + techPointItems.size()));
            Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.LIGHT_PURPLE, "number of multiBlocks = "
                    + multiBlocks.size()));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void reloadTechPoints(Path mainConfigPath, Path techpointsConfigPath)
    {
        loadTechPoints(mainConfigPath, techpointsConfigPath);
        Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.AQUA, "Config.conf reloaded."));
        Sponge.getServer().getConsole().sendMessage(Text.of(TextColors.AQUA, "Techpoints.conf reloaded."));
    }

    private void loadTechPoints(Path mainConfigPath, Path techpointsConfigPath)
    {
        try
        {
            //build configs
            ConfigurationLoader<CommentedConfigurationNode> mainConfig =
                    HoconConfigurationLoader.builder().setPath(mainConfigPath).build();
            ConfigurationLoader<CommentedConfigurationNode> techpointsConfig =
                    HoconConfigurationLoader.builder().setPath(techpointsConfigPath).build();

            //get root nodes
            configRoot = mainConfig.load();
            ConfigurationNode techpointsRoot = techpointsConfig.load();
            techPointItems = techpointsRoot.getNode("techPointItems").getList(TypeToken.of(TechPointItem.class));
            multiBlocks = techpointsRoot.getNode("multiBlocks").getList(TypeToken.of(MultiBlock.class));
        } catch (IOException | ObjectMappingException e)
        {
            e.printStackTrace();
        }
    }

    public int getMaxTechPoints()
    {
        return configRoot.getNode("maxTechPoints").getInt();
    }

    public TechPointItem getTechPointItem(BlockState block)
    {
        for(TechPointItem techPointItem : techPointItems)
        {
            if(isBlock(block, techPointItem))
            {
                return techPointItem;
            }
        }
        for(TechPointItem multiBlock : multiBlocks)
        {
            if(isBlock(block, multiBlock))
            {
                return multiBlock;
            }
        }
        return null;
    }

    private boolean isBlock(BlockState block, TechPointItem techPointItem)
    {
        Logger logger = plugin.getLogger();
        logger.info("block: " + block.getType().getId());
        for(BlockTrait<?> blockTrait : block.getTraits())
        {
            logger.info("trait: " + blockTrait.getName());
            logger.info("value: " + block.getTraitValue(blockTrait).orElse(null));
        }
        if (block.getType().getId().equals(techPointItem.getMaterial()))
        {
            int traitCount = 0;
            for(Trait trait : techPointItem.getTraits())
            {
                if(block.getTrait(trait.getName()).isPresent())
                {
                    //if the trait has a value and it is equal to the trait value
                    if(block.getTraitValue(block.getTrait(trait.getName()).get()).isPresent()
                            && block.getTraitValue(block.getTrait(trait.getName()).get())
                            .get().toString().compareTo(trait.getValue()) == 0)
                    {
                        traitCount++;
                    }
                }
            }
            return traitCount == techPointItem.getTraits().size();
        }
        return false;
    }
}
