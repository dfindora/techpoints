package com.goldensands.main;

import com.goldensands.config.BasicTechPointItem;
import com.goldensands.util.TechChunk;
import com.goldensands.util.Vector2d;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Commands implements Listener, CommandExecutor
{
    private Techpoints plugin;
    //TODO:Should these command names be hard-coded?
    String techPoints = "techpoints";
    String techList = "techlist";
    String techLimit = "techlimit";
    String techConfig = "techconfig";
    String techWand = "techwand";
    String techChunk = "techchunk";
    String techVersion = "techVersion";

    Commands(Techpoints plugin)
    {
        this.plugin = plugin;
    }

    /**
     * main command method.
     *
     * @param sender  - sender of the command.
     * @param command - the command executed.
     * @param s       -
     * @param args    - the arguments.
     * @return true of the command was executed successfully, otherwise false.
     */
    @Override
    @SuppressWarnings({"deprecation", "unchecked"})
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        List<Integer> airBlockList = (List<Integer>) plugin.getConfig().getList("WailaBlacklist");
        HashSet<Byte> airBlocks = new HashSet<>();
        for (int airBlock : airBlockList)
        {
            airBlocks.add((byte) airBlock);
        }
        airBlocks.add((byte) 0);
        airBlocks.add((byte) 1316);
        //techversion
        if (command.getName().equalsIgnoreCase(techVersion))
        {
            sender.sendMessage(ChatColor.GREEN + "Techpoints Version: " + plugin.getDescription().getVersion());
            return true;
        }
        //techconfig
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
        //techchunk
        else if (command.getName().equalsIgnoreCase(techChunk))
        {
            if (!(sender instanceof Player))
            {
                Chunk chunk = plugin.getServer().getWorld("world").getChunkAt(Integer.parseInt(args[0]),
                                                                              Integer.parseInt(args[1]));
                TechChunk techChunk = plugin
                        .getModuleHandler()
                        .getTechpointsModule()
                        .techPoints(chunk);
                plugin.getModuleHandler().getDatabaseModule().addChunk(techChunk.getChunk().getX(),
                                                                       techChunk.getChunk().getZ(),
                                                                       techChunk.getMinTechPoints(),
                                                                       techChunk.getMaxTechPoints());
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + command.getPermissionMessage() + ".");
                return true;
            }
        }
        //techlimit
        else if (command.getName().equalsIgnoreCase(techLimit))
        {
            if (sender.hasPermission("techpoints.techlimit"))
            {
                if (args.length == 0)
                {
                    plugin.getModuleHandler().getTechLimitModule().techLimit(sender);
                }
                if (args.length == 2
                    && (args[0].equalsIgnoreCase("page") || args[0].equalsIgnoreCase("pg"))
                    && (args[1].equalsIgnoreCase("next")))
                {
                    plugin.getModuleHandler().getTechLimitModule().pageNext(sender);
                }
                else if (args.length == 2
                         && (args[0].equalsIgnoreCase("page") || args[0].equalsIgnoreCase("pg"))
                         && (args[1].equalsIgnoreCase("previous")))
                {
                    plugin.getModuleHandler().getTechLimitModule().pagePrevious(sender);
                }
                else if (args.length == 2
                         && (args[0].equalsIgnoreCase("page") || args[0].equalsIgnoreCase("pg"))
                         && (args[1].matches("\\d*")))
                {
                    plugin.getModuleHandler().getTechLimitModule().setPage(sender, Integer.parseInt(args[1]));
                }
                int pageIndex = plugin.getModuleHandler().getTechLimitModule().getPage(sender);
                ArrayList<ArrayList<Map.Entry<Vector2d, Integer>>> query =
                        plugin.getModuleHandler().getTechLimitModule().getQuery(sender);
                sender.sendMessage("Showing page " + pageIndex + "of " + query.size());
                if (pageIndex <= query.size() && pageIndex > 0)
                {
                    for (Map.Entry<Vector2d, Integer> entry : query.get(pageIndex - 1))
                    {
                        sender.sendMessage("chunk " + entry.getKey() + "has " + entry.getValue() + " techpoints.");
                    }
                }
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "" + command.getPermissionMessage());
                return true;
            }
        }
        //player check
        else if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        //techpoints commands
        else if (command.getName().equalsIgnoreCase(techPoints))
        {
            //main techpoints command
            if (args.length == 0)
            {
                if (sender.hasPermission("techpoints.techpoints"))
                {
                    TechChunk techChunk = plugin
                            .getModuleHandler()
                            .getTechpointsModule()
                            .techPoints((Player) sender);
                    plugin.getModuleHandler().getTechpointsModule()
                            .techPointsMessages(techChunk, (Player) sender, 1);
                    plugin.getModuleHandler().getDatabaseModule().addChunk(techChunk.getChunk().getX(),
                                                                           techChunk.getChunk().getZ(),
                                                                           techChunk.getMinTechPoints(),
                                                                           techChunk.getMaxTechPoints());
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
                    //techpoints waila
                    case "waila":
                        if (sender.hasPermission("techpoints.techpoints.waila"))
                        {
                            Block waila = ((Player) sender).getTargetBlock(airBlocks, 5);
                            sender.sendMessage("name: " + waila.getType().name() + ", ID: " + waila.getTypeId()
                                               + ":" + waila.getData());
                            BasicTechPointItem techPointItem = plugin.getConfigManager().configMatch(waila, null);
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
                            sender.sendMessage(ChatColor.RED + command.getPermissionMessage() + " waila.");
                            return true;
                        }
                        //techpoints hotbar
                    case "hotbar":
                        if (sender.hasPermission("techpoints.techpoints.hotbar"))
                        {
                            ItemStack hotbar = ((Player) sender).getItemInHand();
                            sender.sendMessage("name: " + hotbar.getType().name() + ", ID: " + hotbar.getTypeId()
                                               + ":" + hotbar.getDurability());
                            BasicTechPointItem techPointItem = plugin.getConfigManager().configMatch(null, hotbar);
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
                            sender.sendMessage(ChatColor.RED + command.getPermissionMessage() + " hotbar.");
                            return true;
                        }
                    default:
                        sender.sendMessage(ChatColor.RED + "invalid syntax.");
                        return true;
                }
            }
        }
        //techlist
        else if (command.getName().equalsIgnoreCase(techList))
        {
            if (sender.hasPermission("techpoints.techlist"))
            {
                TechChunk techChunk = plugin.getModuleHandler().getTechpointsModule()
                        .techPoints((Player) sender);
                plugin.getModuleHandler().getTechpointsModule()
                        .techPointsMessages(techChunk, (Player) sender, 2);
                plugin.getModuleHandler().getDatabaseModule().addChunk(techChunk.getChunk().getX(),
                                                                       techChunk.getChunk().getZ(),
                                                                       techChunk.getMinTechPoints(),
                                                                       techChunk.getMaxTechPoints());
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "" + command.getPermissionMessage());
                return true;
            }
        }
        //techwand
        else if (command.getName().equalsIgnoreCase(techWand))
        {
            if (sender.hasPermission("techpoints.techwand"))
            {
                if (args.length > 0 && args[0].equalsIgnoreCase("count"))
                {
                    plugin.getModuleHandler().getWandModule().regionTechpoints((Player) sender);
                }
                else
                {
                    ItemStack wand = new ItemStack(plugin.getConfig().getInt("techwand.id"), 1,
                                                   (short) plugin.getConfig().getInt("techwand.metadata"));
                    if (((Player) sender).getItemInHand().getType() == Material.AIR)
                    {
                        ((Player) sender).setItemInHand(wand);
                    }
                    else
                    {
                        ((Player) sender).getInventory().addItem(wand);
                    }
                }
                return true;
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "" + command.getPermissionMessage());
                return true;
            }
        }

        return false;
    }
}
