package com.goldensands.modules;

public class Coordinate implements Comparable<Coordinate>
{
    private final double x;
    private final double y;
    private final double z;

    public Coordinate(double x, double y, double z)
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
    public int compareTo(Coordinate coordinate)
    {
        return (this.x == coordinate.x && this.y == coordinate.y && this.z == coordinate.z) ? 0 : 1;
    }
}
