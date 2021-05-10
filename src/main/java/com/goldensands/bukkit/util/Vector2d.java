package com.goldensands.bukkit.util;

public class Vector2d implements Comparable<Vector2d>
{
    private final int x;
    private final int z;

    public Vector2d(int x, int z)
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
    public int compareTo(Vector2d o)
    {
        return (this.x == o.x && this.z == o.z) ? 0 : 1;
    }

    @Override
    public String toString()
    {
        return "(" + x + ", " + z + ")";
    }
}
