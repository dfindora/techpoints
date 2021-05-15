package com.goldensands.sponge.commands;

import com.flowpowered.math.vector.Vector3i;
import com.goldensands.sponge.Techpoints;
import com.goldensands.sponge.modules.TechChunk;
import com.goldensands.sponge.modules.TechpointsModule;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

@NonnullByDefault
public class TechpointsCommand implements CommandExecutor
{
    private final Techpoints plugin;

    public TechpointsCommand(Techpoints plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
    {
        if (src instanceof Player)
        {
            if (src.hasPermission("techpoints.techpoints"))
            {
                Vector3i location = ((Player) src).getLocation().getChunkPosition();
                TechpointsModule techpointsModule = plugin.getModuleHandler().getTechpointsModule();
                TechChunk techChunk = techpointsModule.techpoints(location, ((Player) src).getWorld());
                techpointsModule.techPointsMessages(techChunk, (Player) src, false);
            }
            else
            {
                src.sendMessage(Text.of(TextColors.RED, "You do not have permission to execute /techpoints."));
            }
        }
        else
        {
            src.sendMessage(Text.of(TextColors.RED, "This command can only be executed by players."));
        }
        return CommandResult.success();
    }
}
