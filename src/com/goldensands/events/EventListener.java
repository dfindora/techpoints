package com.goldensands.events;

import com.goldensands.config.BasicTechPointItem;
import com.goldensands.main.Techpoints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class EventListener implements Listener
{
    private Techpoints plugin;

    public EventListener(Techpoints plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event)
    {
        Chunk currentChunk = event.getBlock().getLocation().getChunk();
        BasicTechPointItem techPointItem = plugin.getCommands().getBasicTechPointItem(event.getBlock(), null);
        if (techPointItem != null)
        {
            int totalTechPoints = plugin.getCommands().techPoints(currentChunk, event.getPlayer(), 0);
            if (totalTechPoints > (int) plugin.getConfig().get("MaxTechPoints") && !event.getPlayer().hasPermission("techpoints.limit.bypass"))
            {
                event.getPlayer().sendMessage(ChatColor.RED + "This chunk has exceeded the tech point limit!");
                event.getPlayer().sendMessage(ChatColor.RED + "The tech point limit is "
                                              + plugin.getConfig().get("MaxTechPoints") + ". This chunk is now at " + totalTechPoints + ".");
                //logToFile("Chunk " + currentChunk.getX() + ", " + currentChunk.getZ() + " has " + totalTechPoints + " tech points");
            }
        }
    }

    private void logToFile(String message)
    {
        //file creation
        File logFile = new File(plugin.getDataFolder(), "techlimit.log");
        if (!logFile.exists())
        {
            try
            {
                boolean isCreated = logFile.createNewFile();
                if (isCreated)
                {
                    Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN
                                                                      + "techlimit.log has been created.");
                }
            }
            catch (IOException e)
            {
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED
                                                                  + "Unable to create log file.");
            }
        }

        try
        {
            FileWriter fw = new FileWriter(logFile);
            PrintWriter pw = new PrintWriter(fw);

            pw.println(message);
            pw.close();
            fw.close();
        }
        catch (IOException e)
        {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.RED
                                                              + "Error in writing to the log file.");
            e.printStackTrace();
        }
    }
}
