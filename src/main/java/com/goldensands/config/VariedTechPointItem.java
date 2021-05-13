package com.goldensands.config;

public class VariedTechPointItem extends BasicTechPointItem
{
    private final String reason;
    private final int maxTechPoints;

    VariedTechPointItem(String id, int data, int minTechPoints, int maxTechPoints, String name, String reason)
    {
        super(id, data, minTechPoints, name);
        this.reason = reason;
        this.maxTechPoints = maxTechPoints;
    }

    public String getReason()
    {
        return reason;
    }

    public int getMaxTechPoints()
    {
        return maxTechPoints;
    }

    @Override
    public String toString()
    {
        return super.toString() + ":" + reason;
    }
}
