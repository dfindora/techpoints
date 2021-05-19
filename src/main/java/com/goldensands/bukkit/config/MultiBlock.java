package com.goldensands.bukkit.config;

public class MultiBlock extends BasicTechPointItem
{
    private final int numOfBlocks;

    MultiBlock(String id, int data, int techPoints, int numOfBlocks, String name)
    {
        super(id, data, techPoints, name);
        this.numOfBlocks = numOfBlocks;
    }

    public int getNumOfBlocks()
    {
        return numOfBlocks;
    }

    @Override
    public String toString()
    {
        return super.toString() + ":" + numOfBlocks;
    }
}
