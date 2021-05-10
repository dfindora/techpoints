package com.goldensands.bukkit.util;

public class Vector3d implements Comparable<Vector3d>
{
    private final double x;
    private final double y;
    private final double z;

    public Vector3d(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    @Override
    public int compareTo(Vector3d vector3d)
    {
        return (this.x == vector3d.x && this.y == vector3d.y && this.z == vector3d.z) ? 0 : 1;
    }

    @Override
    public String toString()
    {
        return x + ", " + y + ", " + z;
    }
}
