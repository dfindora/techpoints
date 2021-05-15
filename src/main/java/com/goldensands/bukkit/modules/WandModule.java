package com.goldensands.bukkit.modules;

import com.goldensands.bukkit.main.Techpoints;
import com.goldensands.util.Vector3d;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class WandModule
{
    private final Techpoints plugin;
    HashMap<UUID, Vector3d> firstPositions;
    HashMap<UUID, Vector3d> secondPositions;

    public WandModule(Techpoints plugin)
    {
        this.plugin = plugin;
        firstPositions = new HashMap<>();
        secondPositions = new HashMap<>();
    }

    public void pos(double x, double y, double z, UUID uuid, boolean isLeft)
    {
        Vector3d vector3d = new Vector3d(x, y, z);
        if(isLeft)
        {
            if(firstPositions.containsKey(uuid))
            {
                firstPositions.replace(uuid, vector3d);
            }
            else
            {
                firstPositions.put(uuid, vector3d);
            }
        }
        else
        {
            if(secondPositions.containsKey(uuid))
            {
                secondPositions.replace(uuid, vector3d);
            }
            else
            {
                secondPositions.put(uuid, vector3d);
            }
        }
    }

    public ArrayList<Chunk> getChunks(Player player, World world)
    {
        ArrayList<Chunk> chunks = new ArrayList<>();
        Vector3d pos1 = firstPositions.getOrDefault(player.getUniqueId(), null);
        Vector3d pos2 = secondPositions.getOrDefault(player.getUniqueId(), null);
        //if both positions are set
        if(pos1 != null && pos2 != null)
        {
            double pos1x = Math.min(pos1.getX(), pos2.getX());
            double pos2x = Math.max(pos1.getX(), pos2.getX());
            double pos1z = Math.min(pos1.getZ(), pos2.getZ());
            double pos2z = Math.max(pos1.getZ(), pos2.getZ());
            if (pos1x <= pos2x && pos1z <= pos2z)
            {
                for (double x = pos1x; x <= pos2x; x += 16)
                {
                    for (double z = pos1z; z <= pos2z; z += 16)
                    {
                        Location location = new Location(world, x, pos1.getY(), z);
                        chunks.add(world.getChunkAt(location));
                        //check if pos2z is in a different chunk and would be passed up normally
                        if (z != pos2z && z + 16 > pos2z && (int) (pos2z / 16) != (int) (z / 16))
                        {
                            z = pos2z - 16;
                        }
                    }
                    //check if pos2.getX is in a different chunk and would be passed up normally
                    if (x != pos2x && x + 16 > pos2x && (int) (pos2x / 16) != (int) (x / 16))
                    {
                        x = pos2x - 16;
                    }
                }
            }
            return chunks;
        }
        else
        {
            player.sendMessage(ChatColor.RED + "Both positions have not been set.");
            return null;
        }
    }

    public void regionTechpoints(Player sender)
    {
        ArrayList<Chunk> chunks = getChunks(sender, sender.getWorld());
        ArrayList<TechChunk> techChunks = new ArrayList<>();
        for(Chunk chunk : chunks)
        {
            techChunks.add(plugin.getModuleHandler().getTechpointsModule().techPoints(chunk));
        }
        sender.sendMessage("list of chunks: ");
        for(TechChunk techChunk : techChunks)
        {
            if(techChunk.getMinTechPoints() > plugin.getConfig().getInt("MaxTechPoints")
               && techChunk.getMaxTechPoints() > plugin.getConfig().getInt("MaxTechPoints"))
            {
                sender.sendMessage(ChatColor.RED + "Techpoints for chunk (" + techChunk.getChunk().getX() + ", "
                                   + techChunk.getChunk().getZ() + "): "
                                   + techChunk.getMinTechPoints() + " to " + techChunk.getMaxTechPoints());

            }
            else if(techChunk.getMaxTechPoints() > plugin.getConfig().getInt("MaxTechPoints"))
            {
                sender.sendMessage(ChatColor.YELLOW + "Techpoints for chunk (" + techChunk.getChunk().getX() + ", "
                                   + techChunk.getChunk().getZ() + "): "
                                   + techChunk.getMinTechPoints() + " to " + techChunk.getMaxTechPoints());
            }
            else if(techChunk.getMaxTechPoints() > 0)
            {
                sender.sendMessage("Techpoints for chunk (" + techChunk.getChunk().getX() + ", "
                                   + techChunk.getChunk().getZ() + "): "
                                   + techChunk.getMinTechPoints() + " to " + techChunk.getMaxTechPoints());
            }
        }
    }
}
