package com.goldensands.sponge.config;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("UnstableApiUsage")
public class TraitSerializer implements TypeSerializer<Trait>
{
    @Override
    public @Nullable Trait deserialize(@NonNull TypeToken<?> type, @NonNull ConfigurationNode value)
            throws ObjectMappingException
    {
        String name = value.getNode("name").getString();
        String val = value.getNode("value").getString();
        return new Trait(name, val);
    }

    @Override
    public void serialize(@NonNull TypeToken<?> type, @Nullable Trait obj, @NonNull ConfigurationNode value)
            throws ObjectMappingException
    {
        if(obj != null)
        {
            value.getNode("name").setValue(obj.getName());
            value.getNode("value").setValue(obj.getValue());
        }
    }
}
