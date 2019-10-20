package com.goldensands.main;

import com.goldensands.config.BasicTechPointItem;
import com.goldensands.modules.TechChunk;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

public class Commands implements Listener, CommandExecutor
{
    private Techpoints plugin;
    //TODO:Should these command names be hard-coded?
    String techPoints = "techpoints";
    String techList = "techlist";
    String techLimit = "techlimit";
    String techConfig = "techconfig";

    Commands(Techpoints plugin)
    {
        this.plugin = plugin;
    }

    /**
     * main command method.
     * @param sender - sender of the command.
     * @param command - the command executed.
     * @param s -
     * @param args - the arguments.
     * @return true of the command was executed successfully, otherwise false.
     */
    @Override
    @SuppressWarnings({"deprecation"})
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args)
    {
        HashSet<Byte> airBlocks = new HashSet<>();
        airBlocks.add((byte) 0);
        airBlocks.add((byte) 1316);
        //techconfig
        if (command.getName().equalsIgnoreCase(techConfig))
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
        //player check
        else if(!(sender instanceof Player))
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
                    TechChunk techChunk = plugin.getModuleHandler().getTechpointsModule()
                            .techPoints((Player)sender);
                    plugin.getModuleHandler().getTechpointsModule()
                            .techPointsMessages(techChunk, (Player)sender, 1);
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
        //techlimit
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
        //techlist
        else if (command.getName().equalsIgnoreCase(techList))
        {
            if (sender.hasPermission("techpoints.techlist"))
            {
                TechChunk techChunk = plugin.getModuleHandler().getTechpointsModule()
                        .techPoints((Player)sender);
                plugin.getModuleHandler().getTechpointsModule()
                        .techPointsMessages(techChunk, (Player)sender, 2);
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
