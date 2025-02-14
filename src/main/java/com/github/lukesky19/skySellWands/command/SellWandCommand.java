/*
    SkySellWands adds sell wands that uses SkyShop's API selling.
    Copyright (C) 2024  lukeskywlker19

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.github.lukesky19.skySellWands.command;

import com.github.lukesky19.skySellWands.SkySellWands;
import com.github.lukesky19.skySellWands.configuration.manager.LocaleManager;
import com.github.lukesky19.skySellWands.configuration.record.Locale;
import com.github.lukesky19.skySellWands.manager.WandManager;
import com.github.lukesky19.skylib.format.FormatUtil;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.MessageComponentSerializer;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SellWandCommand {
    private final SkySellWands skySellWands;
    private final LocaleManager localeManager;
    private final WandManager wandManager;

    public SellWandCommand(SkySellWands skySellWands, LocaleManager localeManager, WandManager wandManager) {
        this.skySellWands = skySellWands;
        this.localeManager = localeManager;
        this.wandManager = wandManager;
    }

    public LiteralCommandNode<CommandSourceStack> createCommand() {
        LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal("skysellwand")
                .requires(ctx -> ctx.getSender().hasPermission("skysellwands.commands.skysellwands"));

        builder.then(Commands.literal("help")
            .requires(ctx -> ctx.getSender().hasPermission("skysellwands.commands.skysellwands.help"))
            .executes(ctx -> {
                Locale locale = localeManager.getLocale();
                
                CommandSender sender = ctx.getSource().getSender();
                for(String message : locale.help()) {
                    sender.sendMessage(FormatUtil.format(message));
                }
                
                return 1;
            })
        );
        
        builder.then(Commands.literal("reload")
            .requires(ctx -> ctx.getSender().hasPermission("skysellwands.commands.skysellwands.reload"))
            .executes(ctx -> {
                Locale locale = localeManager.getLocale();
                CommandSender sender = ctx.getSource().getSender();

                skySellWands.reload();

                sender.sendMessage(FormatUtil.format(locale.prefix() + locale.configReload()));
                
                return 1;
            })
        );
        
        builder.then(Commands.literal("give")
            .requires(ctx -> ctx.getSender().hasPermission("skysellwands.commands.skysellwands.give"))
            .then(Commands.argument("player_name", ArgumentTypes.player())
                .then(Commands.argument("uses", IntegerArgumentType.integer())
                    .suggests((commandContext, suggestionsBuilder) -> {
                        Message message = MessageComponentSerializer.message().serialize(FormatUtil.format("<green>A value of -1 will set the sell wand to have infinite uses.</green>"));
                        suggestionsBuilder.suggest(-1, message);

                        return suggestionsBuilder.buildFuture();
                    })
                    .then(Commands.argument("amount", IntegerArgumentType.integer())
                        .executes(ctx -> {
                            Player player = ctx.getArgument("player_name", Player.class);
                            int uses = ctx.getArgument("uses", int.class);
                            int amount = ctx.getArgument("amount", int.class);

                            if(uses == -1) {
                                wandManager.giveUnlimitedWand(player, amount);
                            } else {
                                wandManager.giveWand(player, uses, amount);
                            }

                            return 1;
                        })
                    )
                )
            )
        );
        
        return builder.build();
    }
}
