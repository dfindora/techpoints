package com.goldensands.sponge.commands;

import com.goldensands.sponge.Techpoints;
import com.goldensands.sponge.config.TechPointItem;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.trait.BlockTrait;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import java.util.Collection;

@NonnullByDefault
public class WailaCommand implements CommandExecutor {

    private final Techpoints plugin;

    public WailaCommand(Techpoints plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource  src, CommandContext args)
    {
        if (src instanceof Player)
        {
            if (src.hasPermission("techpoints.techwaila"))
            {
                src.sendMessage(Text.of(TextColors.YELLOW, "------------TRAITS------------"));
                BlockState block = plugin.getModuleHandler().getWailaModule().waila((Player)src);
                src.sendMessage(Text.of(TextColors.AQUA, "Block Type: " + block.getType().getId()));
                        Collection<BlockTrait<?>> blockTraitList = block.getTraits();
                        if (blockTraitList.isEmpty())
                        {
                            src.sendMessage(Text.of(TextColors.RED, "this block (" + block.getType().getId()
                                    + ") contains no block traits."));
                        }
                        Collection<BlockTrait<?>> blockTraits = block.getTraits();
                        for (BlockTrait<?> trait : blockTraits)
                        {
                            src.sendMessage(Text.of(TextColors.AQUA, "trait: " + trait.getName()));
                            src.sendMessage(Text.of(TextColors.AQUA, "value: " + block.getTraitValue(trait)));
                            src.sendMessage(Text.of(TextColors.AQUA, "possible values: " + trait.getPossibleValues()));
                        }
                        TechPointItem techPointItem = plugin.getConfigManager().getTechPointItem(block);
                        if(techPointItem != null)
                        {
                            src.sendMessage(Text.of(TextColors.BLUE, "Techpoints: " + techPointItem.getMinTechPoints()
                                    + " - " + techPointItem.getMaxTechPoints()));
                        }
                        else
                        {
                            src.sendMessage(Text.of(TextColors.RED, "This block has no techpoints."));
                        }
            }
            else
            {
                src.sendMessage(Text.of(TextColors.RED, "You do not have permission to execute /techwaila."));
            }
        }
        else
        {
            src.sendMessage(Text.of(TextColors.RED, "This command can only be executed by players."));
        }
        return CommandResult.success();
    }
}
