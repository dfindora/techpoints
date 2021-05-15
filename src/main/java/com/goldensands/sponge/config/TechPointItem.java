package com.goldensands.sponge.config;

import java.util.List;

public class TechPointItem implements Comparable<TechPointItem>
{
    private final String material;
    private final List<Trait> traits;
    private final int minTechPoints;
    private final int maxTechPoints;

    public TechPointItem(String material, List<Trait> traits, int minTechPoints, int maxTechPoints)
    {
        this.material = material;
        this.traits = traits;
        this.minTechPoints = minTechPoints;
        this.maxTechPoints = maxTechPoints;
    }

    public String getMaterial()
    {
        return material;
    }

    public List<Trait> getTraits()
    {
        return traits;
    }

    public int getMinTechPoints()
    {
        return minTechPoints;
    }

    public int getMaxTechPoints()
    {
        return maxTechPoints;
    }

    @Override
    public String toString()
    {
        return (traits.size() > 0)
                ? material + ":" + traits.get(0).getValue() + " techpoints: " + minTechPoints + " - " + maxTechPoints
                :material + ":" + " techpoints: " + minTechPoints + " - " + maxTechPoints ;
    }

    //if the materials don't match, return -1.
    //if a trait doesn't match, return 1.
    //else return 0.
    @Override
    public int compareTo(TechPointItem compareTo)
    {
        int compare = 0;
        if(!this.material.equals(compareTo.material))
        {
            compare = -1;
        }
        else if(traits.size() != compareTo.traits.size())
        {
            compare = 1;
        }
        else
        {
            for(int i = 0; i < traits.size(); i++)
            {
                if (traits.get(i).compareTo(compareTo.traits.get(i)) != 0)
                {
                    compare = 1;
                    break;
                }
            }
        }
        return compare;
    }
}
