package com.goldensands.config;

public class MultiBlock extends BasicTechPointItem
{
    private int numOfBlocks;

    MultiBlock(int id, int data, int techPoints, int numOfBlocks, String name)
    {
        super(id, data, techPoints, name);
        this.numOfBlocks = numOfBlocks;
    }

    public int getNumOfBlocks() {
        return numOfBlocks;
    }

    @Override
    public String toString()
    {
        return super.toString() + ":" + numOfBlocks;
    }
}
