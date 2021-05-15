package com.goldensands.sponge.commands;

import com.goldensands.sponge.Techpoints;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

@NonnullByDefault
public class ConfigCommand implements CommandExecutor
{
    private final Techpoints plugin;

    public ConfigCommand(Techpoints plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException
    {
        if (src.hasPermission("techpoints.techconfig"))
        {
            String arg = args.<String>getOne("type").orElse(null);
            if (arg != null)
            {
                if (arg.equals("reload"))
                {
                    plugin.getConfigManager().reloadTechPoints();
                    if(src instanceof Player)
                    {
                        src.sendMessage(Text.of(TextColors.GREEN, "Configs reloaded."));
                    }
                }
            }
        }
        return CommandResult.success();
    }
}
