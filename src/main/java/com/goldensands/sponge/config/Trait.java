package com.goldensands.sponge.config;

public class Trait implements Comparable<Trait>
{
    private final String name;
    private final String value;

    public Trait(String name, String value)
    {
        this.name = name;
        this.value = value;
    }

    public String getName()
    {
        return name;
    }

    public String getValue()
    {
        return value;
    }

    public boolean toBool()
    {
        return name.equalsIgnoreCase("true");
    }

    @Override
    public String toString()
    {
        return name + " = " + value;
    }

    //if the trait names are not equal, return -1
    //if the trait values are not equal, return 1
    //else return 0
    @Override
    public int compareTo(Trait compareTo)
    {
        int compare = 0;
        if(!this.name.equals(compareTo.name))
        {
            compare = -1;
        }
        else if(!this.value.equals(compareTo.value))
        {
            compare = 1;
        }
        return compare;
    }
}
