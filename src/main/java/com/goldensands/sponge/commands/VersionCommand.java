package com.goldensands.sponge.commands;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.plugin.meta.util.NonnullByDefault;

import java.util.Objects;

@NonnullByDefault
public class VersionCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args)
    {
        src.sendMessage(Text.of(TextColors.YELLOW, "Techpoints version: "
                + Objects.requireNonNull(Sponge.getPluginManager()
                .getPlugin("techpoints").orElse(null)).getVersion().orElse(null)));
        return CommandResult.success();
    }
}
