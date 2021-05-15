package com.goldensands.sponge.modules;

import com.flowpowered.math.vector.Vector3i;
import com.goldensands.sponge.Techpoints;
import com.goldensands.sponge.config.MultiBlock;
import com.goldensands.sponge.config.TechPointItem;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;
import java.util.Map;

public class TechpointsModule
{
    private final Techpoints plugin;

    public TechpointsModule(Techpoints plugin)
    {
        this.plugin = plugin;
    }

    public TechChunk techpoints(Vector3i chunk, World world)
    {
        int maxX = chunk.getX() * 16 + 16;
        int maxY = 256;
        int maxZ = chunk.getZ() * 16 + 16;
        int minTechPoints = 0;
        int maxTechPoints = 0;
        HashMap<Location<World>, TechPointItem> techPointBlocks = new HashMap<>();
        HashMap<MultiBlock, Integer> multiBlockCounts = new HashMap<>();
        for (int y = 0; y < maxY; y++)
        {
            for (int x = chunk.getX() * 16; x < maxX; x++)
            {
                for (int z = chunk.getZ() * 16; z < maxZ; z++)
                {
                    Location<World> location = world.getLocation(x, y, z);
                    TechPointItem techPointItem = plugin.getConfigManager().getTechPointItem(location.getBlock());
                    if(techPointItem != null)
                    {
                        techPointBlocks.put(location, techPointItem);
                        minTechPoints += techPointItem.getMinTechPoints();
                        maxTechPoints += techPointItem.getMaxTechPoints();
                        if(techPointItem instanceof MultiBlock)
                        {
                            if(multiBlockCounts.containsKey(techPointItem))
                            {
                                multiBlockCounts.put((MultiBlock) techPointItem, 1);
                            }
                            else
                            {
                                multiBlockCounts.replace((MultiBlock) techPointItem,
                                        multiBlockCounts.get(techPointItem) + 1);
                            }
                        }
                    }
                }
            }
        }
        return new TechChunk(minTechPoints, maxTechPoints, chunk, techPointBlocks, multiBlockCounts);
    }

    public void techPointsMessages(TechChunk techChunk, Player sender, boolean isTechList)
    {
        //MultiBlock crossing chunks check
        boolean multiBlockCrossingChunks = false;
        for(Map.Entry<MultiBlock, Integer> entry : techChunk.getMultiBlockCounts().entrySet())
        {
            double multiBlockTechPointCount = entry.getKey().getMinTechPoints() * entry.getValue()
                    / (entry.getKey().getCount() + 0.0);
            if(multiBlockTechPointCount % entry.getKey().getMinTechPoints() != 0)
            {
                multiBlockCrossingChunks = true;
                break;
            }
        }
        boolean containsVariedTechPointItems = techChunk.getMinTechPoints() != techChunk.getMaxTechPoints();

        if (multiBlockCrossingChunks)
        {
            sender.sendMessage(Text.of(TextColors.RED, "This chunk contains MultiBlocks that are crossing chunks."));
        }
        if(containsVariedTechPointItems)
        {
            sender.sendMessage(Text.of(TextColors.RED, "This chunk contains items with varied techpoints."));
        }

        if(isTechList)
        {
            for(Map.Entry<Location<World>, TechPointItem> blocks : techChunk.getTechPointBlocks().entrySet())
            {
                Text blockMessage = Text.builder(blocks.getValue().getMaterial() + ": ")
                        .color(TextColors.GREEN)
                        .append(Text.builder(blocks.getKey().getBlockX() + ", " + blocks.getKey().getBlockY()
                                + ", " + blocks.getKey().getBlockZ())
                                .color(TextColors.YELLOW)
                                .append(Text.builder(" (" + blocks.getValue().getMinTechPoints() + " - "
                                        + blocks.getValue().getMaxTechPoints() + ") points")
                                        .color(TextColors.GRAY)
                                        .build())
                                .build())
                        .build();
                sender.sendMessage(blockMessage);
            }
        }

        //final techpoints
        if (techChunk.getMaxTechPoints() <= plugin.getConfigManager().getMaxTechPoints())
        {
            sender.sendMessage(Text.of(TextColors.AQUA, "Total tech points for chunk (" + techChunk.getChunk().getX()
                    + ", " + techChunk.getChunk().getZ() + "): " + techChunk.getMinTechPoints() + " to "
                    + techChunk.getMaxTechPoints()));
        }
        else if (techChunk.getMinTechPoints() <= plugin.getConfigManager().getMaxTechPoints()
                && techChunk.getMaxTechPoints() > plugin.getConfigManager().getMaxTechPoints())
        {
            sender.sendMessage(Text.of(TextColors.YELLOW, "Total tech points for chunk (" + techChunk.getChunk().getX()
                    + ", " + techChunk.getChunk().getZ() + "): " + techChunk.getMinTechPoints()
                    + " to " + techChunk.getMaxTechPoints()));
            sender.sendMessage(Text.of(TextColors.YELLOW, "This chunk contains varied techpoint items that might put "
                    + "this chunk  over the techpoint limit of " + plugin.getConfigManager().getMaxTechPoints() + "!"));
        }
        else
        {
            sender.sendMessage(Text.of(TextColors.RED, "Total tech points for chunk (" + techChunk.getChunk().getX()
                    + ", " + techChunk.getChunk().getZ() + "): " + techChunk.getMinTechPoints() + " to "
                    + techChunk.getMaxTechPoints()));
            sender.sendMessage(Text.of(TextColors.RED, "This chunk is over the techpoint limit of "
                    + plugin.getConfigManager().getMaxTechPoints() + "!"));
        }

        if(sender.hasPermission("techpoints.techlist") && !isTechList)
        {
            sender.sendMessage(Text.of(TextColors.AQUA, "for a full list of items in this chunk with techpoints, "
                    + "type /techlist."));
        }

    }
}
