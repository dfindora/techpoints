package com.goldensands.modules;

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
    private int techPoints;
    private Chunk chunk;
    private HashMap<Block, BasicTechPointItem> techPointBlocks;
    private HashMap<MultiBlock, Integer> multiBlockCounts;

    public TechChunk(int techPoints, Chunk chunk, HashMap<Block, BasicTechPointItem> techPointBlocks,
                     HashMap<MultiBlock, Integer> multiBlockCounts)
    {
        this.techPoints = techPoints;
        this.chunk = chunk;
        this.techPointBlocks = techPointBlocks;
        this.multiBlockCounts = multiBlockCounts;
    }

    public int getTechPoints()
    {
        return techPoints;
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
