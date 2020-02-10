package com.goldensands.modules;

public class ChunkCoordinate implements Comparable<ChunkCoordinate>
{
    private final int x;
    private final int z;

    public ChunkCoordinate(int x, int z)
    {
        this.x = x;
        this.z = z;
    }

    public int getX()
    {
        return x;
    }

    public int getZ()
    {
        return z;
    }

    @Override
    public int compareTo(ChunkCoordinate o)
    {
        return (this.x == o.x && this.z == o.z) ? 0 : 1;
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + z + ")";
    }
}
