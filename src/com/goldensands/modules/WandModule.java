package com.goldensands.modules;

import com.goldensands.main.Techpoints;
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
    private Techpoints plugin;
    HashMap<UUID, Coordinate> firstPositions;
    HashMap<UUID, Coordinate> secondPositions;

    public WandModule(Techpoints plugin)
    {
        this.plugin = plugin;
        firstPositions = new HashMap<>();
        secondPositions = new HashMap<>();
    }

    public void pos(double x, double y, double z, UUID uuid, boolean isLeft)
    {
        Coordinate coordinate = new Coordinate(x, y, z);
        if(isLeft)
        {
            if(firstPositions.containsKey(uuid))
            {
                firstPositions.replace(uuid, coordinate);
            }
            else
            {
                firstPositions.put(uuid, coordinate);
            }
        }
        else
        {
            if(secondPositions.containsKey(uuid))
            {
                secondPositions.replace(uuid, coordinate);
            }
            else
            {
                secondPositions.put(uuid, coordinate);
            }
        }
    }

    public ArrayList<Chunk> getChunks(Player player, World world)
    {
        ArrayList<Chunk> chunks = new ArrayList<>();
        Coordinate pos1 = firstPositions.getOrDefault(player.getUniqueId(), null);
        Coordinate pos2 = secondPositions.getOrDefault(player.getUniqueId(), null);
        if(pos1 != null && pos2 != null)
        {
            if (pos1.getX() <= pos2.getX() && pos1.getZ() <= pos2.getZ())
            {
                for (double x = pos1.getX(); x <= pos2.getX(); x += 16)
                {
                    System.out.println("x = " + x);
                    for (double z = pos1.getZ(); z <= pos2.getZ(); z += 16)
                    {
                        Location location = new Location(world, x, pos1.getY(), z);
                        chunks.add(world.getChunkAt(location));
                        //check if pos2.getZ is in a different chunk and would be passed up normally
                        if (z != pos2.getZ() && z + 16 > pos2.getZ() && (int) (pos2.getZ() / 16) != (int) (pos1.getZ() / 16))
                        {
                            z = pos2.getZ() - 16;
                        }
                    }
                    //check if pos2.getX is in a different chunk and would be passed up normally
                    if (x != pos2.getX() && x + 16 > pos2.getX() && (int) (pos2.getX() / 16) != (int) (pos1.getX() / 16))
                    {
                        x = pos2.getX() - 16;
                    }
                }
            }
            else if (pos1.getX() <= pos2.getX())
            {
                for (double x = pos1.getX(); x <= pos2.getX(); x += 16)
                {
                    System.out.println("x = " + x);
                    for (double z = pos1.getZ(); z >= pos2.getZ(); z -= 16)
                    {
                        Location location = new Location(world, x, pos1.getY(), z);
                        chunks.add(world.getChunkAt(location));
                        //check if pos2.getZ is in a different chunk and would be passed up normally
                        if (z != pos2.getZ() && z - 16 < pos2.getZ() && (int) (pos2.getZ() / 16) != (int) (pos1.getZ() / 16))
                        {
                            z = pos2.getZ() + 16;
                        }
                    }
                    //check if pos2.getX is in a different chunk and would be passed up normally
                    if (x != pos2.getX() && x + 16 > pos2.getX() && (int) (pos2.getX() / 16) != (int) (pos1.getX() / 16))
                    {
                        x = pos2.getX() - 16;
                    }
                }
            }
            else if(pos1.getZ() <= pos2.getZ())
            {
                for (double x = pos1.getX(); x >= pos2.getX(); x -= 16)
                {
                    for (double z = pos1.getZ(); z <= pos2.getZ(); z += 16)
                    {
                        Location location = new Location(world, x, pos1.getY(), z);
                        chunks.add(world.getChunkAt(location));
                        //check if pos2.getZ is in a different chunk and would be passed up normally
                        if (z != pos2.getZ() && z + 16 > pos2.getZ() && (int) (pos2.getZ() / 16) != (int) (pos1.getZ() / 16))
                        {
                            z = pos2.getZ() - 16;
                        }
                    }
                    //check if pos2.getX is in a different chunk and would be passed up normally
                    if (x != pos2.getX() && x - 16 < pos2.getX() && (int) (pos2.getX() / 16) != (int) (pos1.getX() / 16))
                    {
                        x = pos2.getX() + 16;
                    }
                }
            }
            else
            {
                for (double x = pos1.getX(); x >= pos2.getX(); x -= 16)
                {
                    for (double z = pos1.getZ(); z >= pos2.getZ(); z -= 16)
                    {
                        Location location = new Location(world, x, pos1.getY(), z);
                        chunks.add(world.getChunkAt(location));
                        //check if pos2.getZ is in a different chunk and would be passed up normally
                        if (z != pos2.getZ() && z - 16 < pos2.getZ() && (int) (pos2.getZ() / 16) != (int) (pos1.getZ() / 16))
                        {
                            z = pos2.getZ() + 16;
                        }
                    }
                    //check if pos2.getX is in a different chunk and would be passed up normally
                    if (x != pos2.getX() && x - 16 < pos2.getX() && (int) (pos2.getX() / 16) != (int) (pos1.getX() / 16))
                    {
                        x = pos2.getX() + 16;
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
            techChunks.add(plugin.getModuleHandler().getTechpointsModule().techPoints(chunk, sender));
        }
        sender.sendMessage("list of chunks: ");
        for(TechChunk techChunk : techChunks)
        {
            if(techChunk.getTechPoints() > plugin.getConfig().getInt("MaxTechPoints"))
            {
                sender.sendMessage(ChatColor.RED + "Techpoints for chunk (" + techChunk.getChunk().getX() + ", "
                                   + techChunk.getChunk().getZ() + "): " + techChunk.getTechPoints());

            }
            else
            {
                sender.sendMessage("Techpoints for chunk (" + techChunk.getChunk().getX() + ", "
                                   + techChunk.getChunk().getZ() + "): " + techChunk.getTechPoints());
            }
        }
    }
}
