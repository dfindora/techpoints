package com.goldensands.config;

public class UniqueTechPointItem extends BasicTechPointItem
{
    private String reason;

    UniqueTechPointItem(int id, int data, int techPoints, String name, String reason)
    {
        super(id, data, techPoints, name);
        this.reason = reason;
    }

    public String getReason()
    {
        return reason;
    }

    @Override
    public String toString()
    {
        return super.toString() + ":" + reason;
    }
}
