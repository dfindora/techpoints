package com.goldensands.main;

import com.goldensands.config.BasicTechPointItem;
import com.goldensands.config.MultiBlock;
import com.goldensands.config.UniqueTechPointItem;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Commands implements Listener, CommandExecutor
{
    private Techpoints plugin;
    String techPoints = "techpoints";
    String techList = "techlist";
    String techLimit = "techlimit";
    String techConfig = "techconfig";

    Commands(Techpoints plugin)
    {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings({"deprecation"})
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        HashSet<Byte> airBlocks = new HashSet<>();
        airBlocks.add((byte) 0);
        airBlocks.add((byte) 1316);
        if (command.getName().equalsIgnoreCase(techPoints))
        {
            if (args.length == 0)
            {
                if (sender.hasPermission("techpoints.techpoints"))
                {
                    techPoints(sender, 1);
                    return true;
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + command.getPermissionMessage() + ".");
                    return true;
                }
            }
            else
            {
                switch (args[0])
                {
                    case "waila":
                        if (sender.hasPermission("techpoints.techpoints.waila"))
                        {
                            if (sender instanceof Player)
                            {
                                Block waila = ((Player) sender).getTargetBlock(airBlocks, 5);
                                sender.sendMessage("name: " + waila.getType().name() + ", ID: " + waila.getTypeId() + ":" + waila.getData());
                                BasicTechPointItem techPointItem = getBasicTechPointItem(waila, null);
                                //debug
                                //ItemStack asItem = waila.getState().getData().toItemStack();
                                //((Player) sender).getInventory().setItemInHand(asItem);
                                int tp = (techPointItem != null) ? techPointItem.getTechPoints() : 0;
                                if (tp == 0)
                                {
                                    sender.sendMessage("this block does not have tech points.");
                                }
                                else
                                {
                                    sender.sendMessage("block tech points: " + tp);
                                }
                                return true;
                            }
                            else
                            {
                                sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
                                return true;
                            }
                        }
                        else
                        {
                            sender.sendMessage(ChatColor.RED + command.getPermissionMessage() + " waila.");
                            return true;
                        }
                    case "hotbar":
                        if (sender.hasPermission("techpoints.techpoints.hotbar"))
                        {
                            if (sender instanceof Player)
                            {
                                ItemStack hotbar = ((Player) sender).getItemInHand();
                                sender.sendMessage("name: " + hotbar.getType().name() + ", ID: " + hotbar.getTypeId() + ":" + hotbar.getDurability());
                                BasicTechPointItem techPointItem = getBasicTechPointItem(null, hotbar);
                                int tp = (techPointItem != null) ? techPointItem.getTechPoints() : 0;
                                if (tp == 0)
                                {
                                    sender.sendMessage("this block does not have tech points.");
                                }
                                else
                                {
                                    sender.sendMessage("block tech points: " + tp);
                                }
                                return true;
                            }
                            else
                            {
                                sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
                                return true;
                            }
                        }
                        else
                        {
                            sender.sendMessage(ChatColor.RED + command.getPermissionMessage() + " hotbar.");
                            return true;
                        }
                    default:
                        sender.sendMessage(ChatColor.RED + "invalid syntax.");
                        return true;
                }
            }
        }
        else if (command.getName().equalsIgnoreCase(techLimit))
        {
            if (sender.hasPermission("techpoints.techlimit"))
            {
                sender.sendMessage("This command hasn't been implemented yet.");
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "" + command.getPermissionMessage());
                return true;
            }
        }
        else if (command.getName().equalsIgnoreCase(techList))
        {
            if (sender instanceof Player)
            {
                if (sender.hasPermission("techpoints.techlist"))
                {
                    techPoints(sender, 2);
                    return true;
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "" + command.getPermissionMessage());
                    return true;
                }
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
                return true;
            }
        }
        else if (command.getName().equalsIgnoreCase(techConfig))
        {
            if (sender.hasPermission("techpoints.techconfig"))
            {
                if (args.length > 0 && args[0].equals("reload"))
                {
                    plugin.reloadConfig();
                    plugin.getConfigManager().reloadTechPoints();
                    sender.sendMessage(ChatColor.GREEN + "config reloaded.");
                    return true;
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "invalid arguments.");
                    return true;
                }
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "" + command.getPermissionMessage());
                return true;
            }
        }

        return false;
    }

    private int techPoints(CommandSender sender, int messageLevel)
    {
        if (sender instanceof Player)
        {
            Chunk currentChunk = ((Player) sender).getLocation().getChunk();
            return techPoints(currentChunk, sender, messageLevel);
        }
        else
        {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return 0;
        }
    }

    //message level 0 = no messages
    //message level 1 = critical messages - unique tech point item messages and multiblock crossing chunk messages
    //message level 2 = all messages
    public int techPoints(Chunk currentChunk, CommandSender sender, int messageLevel)
    {
        //Calculate tech points
        int totalTechPoints = 0;
        HashMap<Block, BasicTechPointItem> techPointBlocks = new HashMap<>();
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
                        BasicTechPointItem techPointItem = getBasicTechPointItem(currentBlock, null);
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
            if (multiBlockTechPoints % multiBlockCount.getKey().getTechPoints() != 0 && messageLevel >= 1)
            {
                sender.sendMessage(ChatColor.RED + "WARNING: There is a multiblock structure crossing chunks in " +
                                   "this chunk. Tech point values may not be accurate.");
            }
            totalTechPoints += Math.floor(multiBlockTechPoints);
        }
        //unique tech point item messages
        if (messageLevel >= 1)
        {
            boolean sentUniqueItemMessage = false;
            for (Map.Entry<Block, BasicTechPointItem> techPointBlock : techPointBlocks.entrySet())
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
                                           + ChatColor.GRAY + "(" + techPointItem.getTechPoints() + " points)\n" + ChatColor.RED
                                           + "\n\tReason: " + ((UniqueTechPointItem) techPointItem).getReason());
                    }
                }
            }
        }
        //techlist messages
        if (messageLevel >= 2)
        {
            for (Map.Entry<Block, BasicTechPointItem> techPointBlock : techPointBlocks.entrySet())
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
        if (messageLevel >= 1)
        {
            if (totalTechPoints < (int) plugin.getConfig().get("MaxTechPoints"))
            {
                sender.sendMessage("Total tech points for chunk (" + currentChunk.getX() + ", " + currentChunk.getZ()
                                   + "): " + totalTechPoints);
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "Total tech points for chunk (" + currentChunk.getX() + ", " + currentChunk.getZ()
                                   + "): " + totalTechPoints);
                sender.sendMessage(ChatColor.RED + "This chunk is over the techpoint limit of "
                                   + plugin.getConfig().get("MaxTechPoints") + "!");
            }
            if (sender.hasPermission("techpoints.techlist"))
            {
                sender.sendMessage("for a full list of items in this chunk with techpoints, type /techlist");
            }
        }
        return totalTechPoints;
    }

    @SuppressWarnings({"deprecation"})
    public BasicTechPointItem getBasicTechPointItem(Block block, ItemStack hotbar)
    {
        BasicTechPointItem btpi = null;
        BasicTechPointItem compareTo = (block != null)
                                       ? new BasicTechPointItem(block.getTypeId(), block.getData(), 0, block.getType().name())
                                       : new BasicTechPointItem(hotbar.getTypeId(), hotbar.getDurability(), 0, hotbar.getType().name());
        boolean found = false;
        if ((block != null && block.getTypeId() != 0) || (hotbar != null && hotbar.getTypeId() != 0))
        {
            for (BasicTechPointItem basicTechPointItem : plugin.getConfigManager().getBasicTechPointItems())
            {
                if (basicTechPointItem.compareTo(compareTo) == 0)
                {
                    btpi = basicTechPointItem;
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                for (UniqueTechPointItem uniqueTechPointItem : plugin.getConfigManager().getUniqueTechPointItems())
                {
                    if (uniqueTechPointItem.compareTo(compareTo) == 0)
                    {
                        btpi = uniqueTechPointItem;
                        found = true;
                        break;
                    }
                }
            }
            if (!found)
            {

                for (MultiBlock multiBlock : plugin.getConfigManager().getMultiBlocks())
                {
                    if (multiBlock.compareTo(compareTo) == 0)
                    {
                        btpi = multiBlock;
                        found = true;
                        break;
                    }
                }
            }
        }
        return (found) ? btpi : null;
    }
}
