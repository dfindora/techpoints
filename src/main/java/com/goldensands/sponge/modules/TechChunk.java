package com.goldensands.sponge.modules;

import com.flowpowered.math.vector.Vector3i;
import com.goldensands.sponge.config.MultiBlock;
import com.goldensands.sponge.config.TechPointItem;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.HashMap;

public class TechChunk
{
    private final int minTechPoints;
    private final int maxTechPoints;
    private final Vector3i chunk;
    private final HashMap<Location<World>, TechPointItem> techPointBlocks;
    private final HashMap<MultiBlock, Integer> multiBlockCounts;

    public TechChunk(int minTechPoints, int maxTechPoints, Vector3i chunk, HashMap<Location<World>, TechPointItem> techPointBlocks, HashMap<MultiBlock, Integer> multiBlockCounts)
    {
        this.minTechPoints = minTechPoints;
        this.maxTechPoints = maxTechPoints;
        this.chunk = chunk;
        this.techPointBlocks = techPointBlocks;
        this.multiBlockCounts = multiBlockCounts;
    }

    public int getMinTechPoints()
    {
        return minTechPoints;
    }

    public int getMaxTechPoints()
    {
        return maxTechPoints;
    }

    public Vector3i getChunk()
    {
        return chunk;
    }

    public HashMap<Location<World>, TechPointItem> getTechPointBlocks()
    {
        return techPointBlocks;
    }

    public HashMap<MultiBlock, Integer> getMultiBlockCounts()
    {
        return multiBlockCounts;
    }
}
