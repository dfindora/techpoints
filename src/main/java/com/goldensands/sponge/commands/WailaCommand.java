package com.goldensands.sponge.commands;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.util.blockray.BlockRay;
import org.spongepowered.api.util.blockray.BlockRayHit;
import org.spongepowered.api.world.World;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@NonnullByDefault
public class WailaCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource  src, CommandContext args)
    {
        if (src instanceof Player)
        {
            BlockRay<World> blockRay = BlockRay.from((Player)src)
                    .build();
            if(!blockRay.hasNext())
            {
                src.sendMessage(Text.of(TextColors.RED, "this block ray is empty."));
            }
            boolean found = false;
            int limit = 50;
            int i = 0;
            while(blockRay.hasNext() && !found && i < limit)
            {
                BlockRayHit<World> hit = blockRay.next();
                if(!hit.getLocation().getBlockType().equals(BlockTypes.AIR))
                {
                    src.sendMessage(Text.of(TextColors.AQUA, "Block Type: " + hit.getLocation().getBlockType().getId()));
                    found = true;
                    Collection<BlockTrait<?>> blockTraitList = hit.getLocation().getBlockType().getTraits();
                    if (blockTraitList.isEmpty())
                    {
                        src.sendMessage(Text.of(TextColors.RED, "this block (" + hit.getLocation().getBlockType().getId()
                                + ") contains no block traits."));
                    }
                    BlockState block = hit.getLocation().getBlock();
                    Optional<BlockTrait<?>> blockTrait = block.getTrait("variant");
//                    if(blockTrait.isPresent())
//                    {
//                        Optional<?> variant = block.getTraitValue(blockTrait.get());
//                        variant.ifPresent(o -> src.sendMessage(Text.of(TextColors.AQUA, "variant = " + o.toString())));
//                    }
//                    else
//                    {
//                        src.sendMessage(Text.of(TextColors.RED, "This block contains no variants."));
//                    }
                    Collection<BlockTrait<?>> blockTraits = block.getTraits();
                    for (BlockTrait<?> trait : blockTraits)
                    {
                        src.sendMessage(Text.of(TextColors.AQUA, "trait: " + trait.getName()));
                        src.sendMessage(Text.of(TextColors.AQUA, "value: " + block.getTraitValue(trait)));
                        src.sendMessage(Text.of(TextColors.AQUA, "possible values: " + trait.getPossibleValues()));
                    }
                }
                i++;
            }
            if(i >= limit)
            {
                src.sendMessage(Text.of(TextColors.RED, "The block is too far away."));
            }
        }
        return CommandResult.success();
    }
}
