package com.goldensands.bukkit.main;

import com.goldensands.bukkit.config.BasicTechPointItem;
import com.goldensands.bukkit.util.TechChunk;
import com.goldensands.bukkit.util.Vector2d;
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

import javax.annotation.Nonnull;
import java.util.*;

public class Commands implements Listener, CommandExecutor
{
    private final Techpoints plugin;
    //TODO:Should these command names be hard-coded?
    String techPoints = "techpoints";
    String techList = "techlist";
    String techLimit = "techlimit";
    String techConfig = "techconfig";
    String techWand = "techwand";
    String techChunk = "techchunk";
    String techVersion = "techversion";
    String techWaila = "techwaila";
    String techHotbar = "techhotbar";

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
    public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String s,
                             @Nonnull String[] args)
    {
        List<String> airBlockList = (List<String>) plugin.getConfig().getList("WailaBlacklist");
        HashSet<Material> airBlocks = new HashSet<>();
        if (airBlockList != null)
        {
            for (String airBlock : airBlockList)
            {
                Material material = Material.getMaterial(airBlock);
                if (material != null)
                {
                    airBlocks.add(material);
                }
            }
        }
        airBlocks.add(Material.AIR);
        //techversion
        if (command.getName().equalsIgnoreCase(techVersion))
        {
            sender.sendMessage(ChatColor.GREEN + "Techpoints Version: " + plugin.getDescription().getVersion());
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
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "invalid arguments.");
                }
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "" + command.getPermissionMessage());
            }
        }
        //techchunk
        else if (command.getName().equalsIgnoreCase(techChunk))
        {
            if (!(sender instanceof Player))
            {
                Chunk chunk = Objects.requireNonNull(plugin.getServer().getWorld("world"))
                        .getChunkAt(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                TechChunk techChunk = plugin
                        .getModuleHandler()
                        .getTechpointsModule()
                        .techPoints(chunk);
                plugin.getModuleHandler().getDatabaseModule().addChunk(techChunk.getChunk().getX(),
                        techChunk.getChunk().getZ(),
                        techChunk.getMinTechPoints(),
                        techChunk.getMaxTechPoints());
            }
            else
            {
                sender.sendMessage(ChatColor.RED + command.getPermissionMessage() + ".");
            }
        }
        //techlimit
        else if (command.getName().equalsIgnoreCase(techLimit))
        {
            if (sender.hasPermission("techpoints.techlimit"))
            {
                //command /techlimit
                if (args.length == 0)
                {
                    plugin.getModuleHandler().getTechLimitModule().techLimit(sender);
                }
                //command /techlimit page next
                if (args.length == 2
                        && (args[0].equalsIgnoreCase("page") || args[0].equalsIgnoreCase("pg"))
                        && (args[1].equalsIgnoreCase("next")))
                {
                    plugin.getModuleHandler().getTechLimitModule().pageNext(sender);
                }
                //command /techlimit page previous
                else if (args.length == 2
                        && (args[0].equalsIgnoreCase("page") || args[0].equalsIgnoreCase("pg"))
                        && (args[1].equalsIgnoreCase("previous")))
                {
                    plugin.getModuleHandler().getTechLimitModule().pagePrevious(sender);
                }
                //command /techlimit page <number>
                else if (args.length == 2
                        && (args[0].equalsIgnoreCase("page") || args[0].equalsIgnoreCase("pg"))
                        && (args[1].matches("\\d*")))
                {
                    plugin.getModuleHandler().getTechLimitModule().setPage(sender, Integer.parseInt(args[1]));
                }
                //check page is not out of bounds
                int pageIndex = plugin.getModuleHandler().getTechLimitModule().getPage(sender);
                ArrayList<ArrayList<Map.Entry<Vector2d, Vector2d>>> query =
                        plugin.getModuleHandler().getTechLimitModule().getQuery(sender);
                //check if page is not out of bounds
                if (pageIndex <= query.size() && pageIndex > 0)
                {
                    sender.sendMessage(ChatColor.AQUA + "Showing page " + pageIndex + " of " + query.size());

                    for (Map.Entry<Vector2d, Vector2d> entry : query.get(pageIndex - 1))
                    {
                        if (entry.getValue().getX() > 200)
                        {
                            sender.sendMessage(ChatColor.AQUA + "chunk " + entry.getKey() + " has " + ChatColor.RED
                                    + entry.getValue().getX() + " to " + entry.getValue().getZ() +
                                    ChatColor.AQUA + " techpoints.");
                        }
                        else
                        {
                            sender.sendMessage(ChatColor.AQUA + "chunk " + entry.getKey() + " has " + ChatColor.YELLOW
                                    + entry.getValue().getX() + " to " + entry.getValue().getZ() +
                                    ChatColor.AQUA + " techpoints.");
                        }
                    }
                }
                else if (pageIndex > query.size())
                {
                    sender.sendMessage(ChatColor.RED + "there is no next page.");
                    plugin.getModuleHandler().getTechLimitModule().setPage(sender, query.size());
                }
                else
                {
                    sender.sendMessage(ChatColor.RED + "there is no previous page.");
                    plugin.getModuleHandler().getTechLimitModule().setPage(sender, 1);
                }
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "" + command.getPermissionMessage());
            }
        }
        //player check
        else if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
        }
        //techpoints commands
        else if (command.getName().equalsIgnoreCase(techPoints))
        {
            //main techpoints command
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
            }
            else
            {
                sender.sendMessage(ChatColor.RED + command.getPermissionMessage());
            }
        }
        //techwaila
        else if (command.getName().equalsIgnoreCase(techWaila))
        {
            if (sender.hasPermission("techpoints.waila"))
            {
                Block waila = ((Player) sender).getTargetBlock(airBlocks, 5);
                sender.sendMessage("name: " + waila.getType().name() + ", ID: "
                        + waila.getType().name() + ":" + waila.getData());
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
            }
            else
            {
                sender.sendMessage(ChatColor.RED + command.getPermissionMessage());
            }
        }
        //techhotbar
        else if (command.getName().equalsIgnoreCase(techHotbar))
        {
            if (sender.hasPermission("techpoints.hotbar"))
            {
                ItemStack hotbar = ((Player) sender).getItemInHand();
                sender.sendMessage("name: " + hotbar.getType().name() + ", ID: " + hotbar.getType().name()
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
            }
            else
            {
                sender.sendMessage(ChatColor.RED + command.getPermissionMessage());
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
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "" + command.getPermissionMessage());
            }
        }
        //techwand
        else if (command.getName().equalsIgnoreCase(techWand))
        {
            ItemStack wand = new ItemStack(Objects.requireNonNull(Material.getMaterial(
                    Objects.requireNonNull(plugin.getConfig().getString("techwand.id")))), 1,
                    (short) plugin.getConfig().getInt("techwand.metadata"));
            if (sender.hasPermission("techpoints.techwand"))
            {
                if (args.length > 0 && args[0].equalsIgnoreCase("count"))
                {
                    plugin.getModuleHandler().getWandModule().regionTechpoints((Player) sender);
                }
                else if (!((Player) sender).getInventory().contains(wand.getType()))
                {
                    if (((Player) sender).getItemInHand().getType() == Material.AIR)
                    {
                        ((Player) sender).setItemInHand(wand);
                    }
                    else
                    {
                        ((Player) sender).getInventory().addItem(wand);
                    }
                }
            }
            else
            {
                sender.sendMessage(ChatColor.RED + "" + command.getPermissionMessage());
            }
        }
        else
        {
            return false;
        }
        return true;
    }
}
