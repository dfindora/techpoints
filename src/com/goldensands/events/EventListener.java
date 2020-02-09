package com.goldensands.events;

import com.goldensands.config.BasicTechPointItem;
import com.goldensands.main.Techpoints;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
        BasicTechPointItem techPointItem = plugin.getConfigManager().configMatch(event.getBlock(), null);
        if (techPointItem != null)
        {
            //get the techpoint value in this chunk
            int totalTechPoints = plugin.getModuleHandler().getTechpointsModule()
                    .techPoints(event.getPlayer()).getTechPoints();
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

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if(event.getItem().getTypeId() == plugin.getConfig().getInt("techwand.id")
           && event.getItem().getDurability() == plugin.getConfig().getInt("techwand.metadata")
           && event.getPlayer().hasPermission("techpoints.techwand"))
        {
            Block block = event.getClickedBlock();
            if(event.getAction().equals(Action.LEFT_CLICK_BLOCK))
            {
                plugin.getModuleHandler().getWandModule().pos(block.getX(), block.getY(), block.getZ(),
                                                               event.getPlayer().getUniqueId(), true);
                event.getPlayer().sendMessage("position 1 set at " + block.getX() + ", " + block.getY() + ", " + block.getZ() + ".");
            }
            else if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                plugin.getModuleHandler().getWandModule().pos(block.getX(), block.getY(), block.getZ(),
                                                               event.getPlayer().getUniqueId(), false);
                event.getPlayer().sendMessage("position 2 set at " + block.getX() + ", " + block.getY() + ", " + block.getZ() + ".");
            }
        }
    }
}
