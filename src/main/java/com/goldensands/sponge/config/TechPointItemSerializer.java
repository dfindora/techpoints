package com.goldensands.sponge.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class TechPointItemSerializer implements TypeSerializer<TechPointItem>
{
    @Override
    public @Nullable TechPointItem deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value)
            throws ObjectMappingException
    {
        String material = value.getNode("material").getString();
        List<Trait> traits = value.getNode("traits").getList(TypeToken.of(Trait.class));
        int minTechPoints = value.getNode("minTechPoints").getInt();
        int maxTechPoints = value.getNode("maxTechPoints").getInt();
        return new TechPointItem(material, traits, minTechPoints, maxTechPoints);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable TechPointItem obj, @NonNull ConfigurationNode value)
            throws ObjectMappingException
    {
        if (obj != null)
        {
            value.getNode("material").setValue(obj.getMaterial());
            value.getNode("traits").setValue(obj.getTraits());
            value.getNode("minTechPoints").setValue(obj.getMinTechPoints());
            value.getNode("maxTechPoints").setValue(obj.getMaxTechPoints());
        }
    }
}
