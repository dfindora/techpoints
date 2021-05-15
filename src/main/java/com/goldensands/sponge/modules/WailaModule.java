package com.goldensands.sponge.modules;

import com.goldensands.sponge.Techpoints;
import com.goldensands.sponge.config.TechPointItem;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;

import java.util.Collection;
import java.util.Optional;

public class WailaModule
{
    private final Techpoints plugin;

    public WailaModule(Techpoints plugin)
    {
        this.plugin = plugin;
    }

    public BlockState waila(Player src)
    {
        BlockRay<World> blockRay = BlockRay.from(src)
                .build();
        int limit = 50;
        int i = 0;
        while(blockRay.hasNext() && i < limit)
        {
            BlockRayHit<World> hit = blockRay.next();
            if(!hit.getLocation().getBlockType().equals(BlockTypes.AIR))
            {
                return hit.getLocation().getBlock();
            }
            i++;
        }
        if(i >= limit)
        {
            src.sendMessage(Text.of(TextColors.RED, "The block is too far away."));
        }
        return null;
    }
}
