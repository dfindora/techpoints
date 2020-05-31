package com.goldensands.events;

import com.goldensands.config.BasicTechPointItem;
import com.goldensands.main.Techpoints;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
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
            addToDatabase(event.getPlayer());
            //If this chunk is currently over the techpoint limit and the placer does not have the permission to
            // bypass the limit, print out a warning message
            int minTechPoints = plugin.getModuleHandler().getTechpointsModule()
                    .techPoints(event.getPlayer()).getMinTechPoints();
            if (minTechPoints > (int) plugin.getConfig().get("MaxTechPoints") && !event.getPlayer()
                    .hasPermission("techpoints.limit.bypass"))
            {
                event.getPlayer().sendMessage(ChatColor.RED + "This chunk has exceeded the tech point limit!");
                event.getPlayer().sendMessage(ChatColor.RED + "The tech point limit is "
                                              + plugin.getConfig().get("MaxTechPoints") + ". This chunk is now at "
                                              + minTechPoints + ".");
            }
        }
    }

    private void addToDatabase(Player player)
    {
        if(!player.hasPermission("techpoints.limit.bypass"))
        {
            int minTechPoints = plugin.getModuleHandler().getTechpointsModule()
                    .techPoints(player).getMinTechPoints();
            int maxTechPoints = plugin.getModuleHandler().getTechpointsModule()
                    .techPoints(player).getMaxTechPoints();
            Chunk currentChunk = player.getLocation().getChunk();
            plugin.getModuleHandler().getDatabaseModule().addChunk(currentChunk.getX(), currentChunk.getZ(),
                                                                   minTechPoints, maxTechPoints);
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

    @EventHandler
    public void onBreak(BlockBreakEvent event)
    {
        BasicTechPointItem techPointItem = plugin.getConfigManager().configMatch(event.getBlock(), null);
        if(techPointItem != null)
        {
            addToDatabase(event.getPlayer());
        }
    }
}
