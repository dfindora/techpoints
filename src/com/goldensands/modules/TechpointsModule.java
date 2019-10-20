package com.goldensands.modules;

import com.goldensands.config.BasicTechPointItem;
import com.goldensands.config.MultiBlock;
import com.goldensands.config.UniqueTechPointItem;
import com.goldensands.main.Techpoints;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * This module handles the techpoint calculations.
 */
public class TechpointsModule
{
    private Techpoints plugin;
    TechpointsModule(Techpoints plugin)
    {
        this.plugin = plugin;
    }


    public TechChunk techPoints(Player sender)
    {
        Chunk currentChunk = sender.getLocation().getChunk();
        return techPoints(currentChunk);
    }

    /**
     * Calculates the techpoint value in a specific chunk. Techpoints are assigned in the techpoints.yml file. This
     * method iterates through every block in the chunk, checks if they are in the config, and if they are, adds up
     * the total techpoint value.
     * @param currentChunk - the chunk in which the player activated the command.
     * @return an object that contains the following:
     *  - the total techpoint value
     *  - the chunk in which it was calculated
     *  - a map that matches the TechPointItem from the config with the location where it was found
     *  - a map that matches a MultiBlock with its total count
     */
    private TechChunk techPoints(Chunk currentChunk)
    {
        //Calculate tech points
        int totalTechPoints = 0;
        //The map of TechPointItems mapped to the block in which they are located. Used for /techlist printouts.
        HashMap<Block, BasicTechPointItem> techPointBlocks = new HashMap<>();
        //MultiBlocks with their total amounts. Used for MultiBlock recalculations.
        HashMap<MultiBlock, Integer> multiBlockCounts = new HashMap<>();
        int maxY = currentChunk.getWorld().getMaxHeight();
        for (int y = 0; y < maxY; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                for (int z = 0; z < 16; z++)
                {
                    Block currentBlock = currentChunk.getBlock(x, y, z);
                    if (currentBlock.getType() != Material.AIR)
                    {
                        BasicTechPointItem techPointItem = plugin.getConfigManager().configMatch(currentBlock, null);
                        if (techPointItem != null)
                        {
                            techPointBlocks.put(currentBlock, techPointItem);
                            totalTechPoints += techPointItem.getTechPoints();
                        }
                        if (techPointItem instanceof MultiBlock)
                        {
                            if (multiBlockCounts.containsKey(techPointItem))
                            {
                                int count = multiBlockCounts.get(techPointItem);
                                count++;
                                multiBlockCounts.replace((MultiBlock) techPointItem, count);
                            }
                            else
                            {
                                multiBlockCounts.put((MultiBlock) techPointItem, 1);
                            }
                        }
                    }
                }
            }
        }
        //multiblock recalculation
        for (Map.Entry<MultiBlock, Integer> multiBlockCount : multiBlockCounts.entrySet())
        {
            totalTechPoints = totalTechPoints - multiBlockCount.getValue() * multiBlockCount.getKey().getTechPoints();
            double multiBlockTechPoints = multiBlockCount.getValue() * multiBlockCount.getKey().getTechPoints()
                                          / (multiBlockCount.getKey().getNumOfBlocks() + 0.0);
            totalTechPoints += Math.floor(multiBlockTechPoints);
        }
        return new TechChunk(totalTechPoints, currentChunk, techPointBlocks, multiBlockCounts);
    }

    //TODO: handle in the individual commands.
    /**
     * sends messages to the sender based on the specified message level.
     * @param techChunk - the output object from techPoints().
     * @param sender - the player who activated the command.
     * @param messageLevel - this indicates the messages it should send to the player.
     * message level 0 = no messages
     * message level 1 = critical messages - unique tech point item messages and multiblock crossing chunk messages
     * message level 2 = all messages
     */
    public void techPointsMessages(TechChunk techChunk, Player sender, int messageLevel)
    {
        //MultiBlock crossing chunks notification
        for(Map.Entry<MultiBlock, Integer> multiBlockCount : techChunk.getMultiBlockCounts().entrySet())
        {
            double multiBlockTechPoints = multiBlockCount.getValue() * multiBlockCount.getKey().getTechPoints()
                                          / (multiBlockCount.getKey().getNumOfBlocks() + 0.0);
            if (multiBlockTechPoints % multiBlockCount.getKey().getTechPoints() != 0 && messageLevel >= 1)
            {
                sender.sendMessage(ChatColor.RED + "WARNING: There is a multiblock structure crossing chunks in " +
                                   "this chunk. Tech point values may not be accurate.");
            }
        }
        //unique tech point item messages
        if (messageLevel >= 1)
        {
            boolean sentUniqueItemMessage = false;
            for (Map.Entry<Block, BasicTechPointItem> techPointBlock : techChunk.getTechPointBlocks().entrySet())
            {
                Block currentBlock = techPointBlock.getKey();
                BasicTechPointItem techPointItem = techPointBlock.getValue();
                if (techPointBlock.getValue() instanceof UniqueTechPointItem)
                {
                    if (!sentUniqueItemMessage)
                    {
                        sentUniqueItemMessage = true;
                        sender.sendMessage(ChatColor.RED + "This chunk contains blocks with varying tech points.\n");
                        if (messageLevel >= 2)
                        {
                            sender.sendMessage(ChatColor.RED + "these blocks include: \n");
                        }
                    }

                    if (messageLevel >= 2)
                    {
                        sender.sendMessage(ChatColor.GREEN + techPointItem.getName() + ": " + ChatColor.YELLOW +
                                           +currentBlock.getX() + ", " + currentBlock.getY() + ", " + currentBlock.getZ()
                                           + ChatColor.GRAY + "(" + techPointItem.getTechPoints() + " points)\n"
                                           + ChatColor.RED + "\n\tReason: "
                                           + ((UniqueTechPointItem) techPointItem).getReason());
                    }
                }
            }
        }
        //techlist messages
        if (messageLevel >= 2)
        {
            for (Map.Entry<Block, BasicTechPointItem> techPointBlock : techChunk.getTechPointBlocks().entrySet())
            {
                BasicTechPointItem techPointItem = techPointBlock.getValue();
                Block currentBlock = techPointBlock.getKey();
                if (!(techPointItem instanceof UniqueTechPointItem))
                {
                    sender.sendMessage(ChatColor.GREEN + techPointItem.getName() + ": " + ChatColor.YELLOW +
                                       +currentBlock.getX() + ", " + currentBlock.getY() + ", " + currentBlock.getZ()
                                       + ChatColor.GRAY + "(" + techPointItem.getTechPoints() + " points)\n");
                }
            }
        }
        //techpoints messages
        if (messageLevel >= 1)
        {
            if (techChunk.getTechPoints() < (int) plugin.getConfig().get("MaxTechPoints"))
            {
                sender.sendMessage("Total tech points for chunk (" + techChunk.getChunk().getX() + ", "
                                   + techChunk.getChunk().getZ()
                                   + "): " + techChunk.getTechPoints());
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "Total tech points for chunk (" + techChunk.getChunk().getX() + ", "
                                   + techChunk.getChunk().getZ() + "): " + techChunk.getTechPoints());
                sender.sendMessage(ChatColor.RED + "This chunk is over the techpoint limit of "
                                   + plugin.getConfig().get("MaxTechPoints") + "!");
            }
            if (sender.hasPermission("techpoints.techlist"))
            {
                sender.sendMessage("for a full list of items in this chunk with techpoints, type /techlist");
            }
        }
    }
}