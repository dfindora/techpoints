package com.goldensands.sponge.config;

import javax.annotation.Nonnull;
import java.util.List;

public class MultiBlock extends TechPointItem implements Comparable<TechPointItem>
{
    private final int count;


    public MultiBlock(String material, List<Trait> traits, int minTechPoints, int maxTechPoints, int count)
    {
        super(material, traits, minTechPoints, maxTechPoints);
        this.count = count;
    }

    public int getCount()
    {
        return count;
    }

    @Override
    public String toString()
    {
        return super.toString() + ", count = " + count;
    }

    //same return as SuperClass, except returns 2 if counts aren't equal
    @Override
    public int compareTo(@Nonnull TechPointItem compareTo)
    {
        int compare = super.compareTo(compareTo);
        if(compare == 0 && compareTo instanceof MultiBlock)
        {
            if(this.count != ((MultiBlock) compareTo).count)
            {
                compare = 2;
            }
        }
        return compare;
    }
}
