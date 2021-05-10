package com.goldensands.config;

public class BasicTechPointItem implements Comparable<BasicTechPointItem>
{
    private int id;
    private int data;
    private int techPoints;
    private String name;

    public BasicTechPointItem(int id, int data, int techPoints, String name)
    {
        this.id = id;
        this.data = data;
        this.techPoints = techPoints;
        this.name = name;
    }

    public int getId()
    {
        return id;
    }

    public int getData()
    {
        return data;
    }

    public void setData(int data)
    {
        this.data = data;
    }

    public int getTechPoints()
    {
        return techPoints;
    }

    public String getName()
    {
        return name;
    }

    @Override
    public String toString()
    {
        return "- " + id + ":" + data + ":" + techPoints + ":" + name;
    }

    @Override
    public int compareTo(BasicTechPointItem compareTo)
    {
        int result = 0;

        if (id < compareTo.id)
        {
            result = -1;
        }
        else if (id > compareTo.id)
        {
            result = 1;
        }
        else
        {
            if (data < compareTo.data && data >= 0)
            {
                result = -1;
            }
            else if (data > compareTo.data && data >= 0 && compareTo.data >= 0)
            {
                result = 1;
            }
        }

        return result;
    }
}
