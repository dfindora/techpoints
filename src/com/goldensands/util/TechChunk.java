package com.goldensands.util;

import com.goldensands.config.BasicTechPointItem;
import com.goldensands.config.MultiBlock;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

import java.util.HashMap;

/**
 * TechPoints output object.
 */
public class TechChunk
{
    private int minTechPoints;
    private int maxTechPoints;
    private Chunk chunk;
    private HashMap<Block, BasicTechPointItem> techPointBlocks;
    private HashMap<MultiBlock, Integer> multiBlockCounts;

    public TechChunk(int minTechPoints, int maxTechPoints, Chunk chunk,
                     HashMap<Block, BasicTechPointItem> techPointBlocks,
                     HashMap<MultiBlock, Integer> multiBlockCounts)
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

    public Chunk getChunk()
    {
        return chunk;
    }

    public HashMap<Block, BasicTechPointItem> getTechPointBlocks()
    {
        return techPointBlocks;
    }

    public HashMap<MultiBlock, Integer> getMultiBlockCounts()
    {
        return multiBlockCounts;
    }
}
