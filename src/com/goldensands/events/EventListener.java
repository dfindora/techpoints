package com.goldensands.events;

import com.goldensands.config.BasicTechPointItem;
import com.goldensands.main.Techpoints;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

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
        //Checks if the block being placed in this chunk is a TechPointItem
        Chunk currentChunk = event.getBlock().getLocation().getChunk();
        BasicTechPointItem techPointItem = plugin.getCommands().getBasicTechPointItem(event.getBlock(), null);
        if (techPointItem != null)
        {
            //get the techpoint value in this chunk
            int totalTechPoints = plugin.getCommands().techPoints(currentChunk, event.getPlayer(), 0);
            //If this chunk is currently over the techpoint limit and the placer does not have the permission to
            // bypass the limit, print out a warning message
            if (totalTechPoints > (int) plugin.getConfig().get("MaxTechPoints") && !event.getPlayer().hasPermission("techpoints.limit.bypass"))
            {
                //TODO: keep a log of the data in chunk files
                event.getPlayer().sendMessage(ChatColor.RED + "This chunk has exceeded the tech point limit!");
                event.getPlayer().sendMessage(ChatColor.RED + "The tech point limit is "
                                              + plugin.getConfig().get("MaxTechPoints") + ". This chunk is now at " + totalTechPoints + ".");
                //logToFile("Chunk " + currentChunk.getX() + ", " + currentChunk.getZ() + " has " + totalTechPoints + " tech points");
            }
        }
    }
}
