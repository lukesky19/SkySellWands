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
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SellWandCommand implements CommandExecutor, TabCompleter {
    private final SkySellWands skySellWands;
    private final LocaleManager localeManager;
    private final WandManager wandManager;

    public SellWandCommand(SkySellWands skySellWands, LocaleManager localeManager, WandManager wandManager) {
        this.skySellWands = skySellWands;
        this.localeManager = localeManager;
        this.wandManager = wandManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        final Locale locale = localeManager.getLocale();

        if(sender instanceof Player player) {
            if(player.hasPermission("skysellwands.commands.skysellwands")) {
                switch(args.length) {
                    case 1 -> {
                        // Help, Reload
                        switch(args[0].toLowerCase()) {
                            case "help" -> {
                                if(player.hasPermission("skysellwands.commands.skysellwands.help")) {
                                    for(String message : locale.help()) {
                                        player.sendMessage(FormatUtil.format(message));
                                    }

                                    return true;
                                } else {
                                    player.sendMessage(FormatUtil.format(locale.prefix() + locale.noPermission()));

                                    return false;
                                }
                            }

                            case "reload" -> {
                                if(player.hasPermission("skysellwands.commands.skysellwands.reload")) {
                                    skySellWands.reload();

                                    player.sendMessage(FormatUtil.format(locale.prefix() + locale.configReload()));

                                    return true;
                                } else {
                                    player.sendMessage(FormatUtil.format(locale.prefix() + locale.noPermission()));

                                    return false;
                                }
                            }

                            default -> {
                                player.sendMessage(FormatUtil.format(locale.prefix() + locale.unknownArgument()));

                                return false;
                            }
                        }
                    }

                    case 4 -> {
                        // 0 -> give
                        // 1 -> player_name
                        // 2 -> uses
                        // 3 -> amount
                        if(args[0].equalsIgnoreCase("give")) {
                            if(player.hasPermission("skysellwands.commands.skysellwands.give")) {
                                Player target = skySellWands.getServer().getPlayer(args[1]);
                                int uses;
                                int amount;

                                try {
                                    uses = Integer.parseInt(args[2]);
                                } catch (NumberFormatException ignored) {
                                    player.sendMessage(FormatUtil.format(locale.prefix() + locale.invalidUses()));

                                    return false;
                                }

                                try {
                                    amount = Integer.parseInt(args[3]);
                                } catch (NumberFormatException ignored) {
                                    player.sendMessage(FormatUtil.format(locale.prefix() + locale.invalidAmount()));

                                    return false;
                                }

                                if(target != null && target.isOnline() && target.isConnected()) {
                                    wandManager.giveWand(target, uses, amount);

                                    return true;
                                } else {
                                    player.sendMessage(FormatUtil.format(locale.prefix() + locale.invalidPlayer()));

                                    return false;
                                }
                            } else {
                                player.sendMessage(FormatUtil.format(locale.prefix() + locale.noPermission()));

                                return false;
                            }
                        } else {
                            player.sendMessage(FormatUtil.format(locale.prefix() + locale.unknownArgument()));

                            return false;
                        }
                    }

                    default -> {
                        player.sendMessage(FormatUtil.format(locale.prefix() + locale.unknownArgument()));

                        return false;
                    }
                }
            } else {
                player.sendMessage(FormatUtil.format(locale.prefix() + locale.noPermission()));

                return false;
            }
        } else {
            final ComponentLogger logger = skySellWands.getComponentLogger();

            switch(args.length) {
                case 1 -> {
                    switch(args[0].toLowerCase()) {
                        case "help" -> {
                            for(String message : locale.help()) {
                                logger.info(FormatUtil.format(message));
                            }

                            return true;
                        }

                        case "reload" -> {
                            skySellWands.reload();

                            logger.info(FormatUtil.format(locale.configReload()));

                            return true;
                        }

                        default -> {
                            logger.info(FormatUtil.format(locale.unknownArgument()));

                            return false;
                        }
                    }
                }

                case 4 -> {
                    // 0 -> give
                    // 1 -> player_name
                    // 2 -> uses
                    // 3 -> amount
                    if(args[0].equalsIgnoreCase("give")) {
                        Player target = skySellWands.getServer().getPlayer(args[1]);
                        int uses;
                        int amount;

                        try {
                            uses = Integer.parseInt(args[2]);
                        } catch (NumberFormatException ignored) {
                            logger.info(FormatUtil.format(locale.invalidUses()));

                            return false;
                        }

                        try {
                            amount = Integer.parseInt(args[3]);
                        } catch (NumberFormatException ignored) {
                            logger.info(FormatUtil.format(locale.invalidAmount()));

                            return false;
                        }

                        if(target != null && target.isOnline() && target.isConnected()) {
                            wandManager.giveWand(target, uses, amount);

                            return true;
                        } else {
                            logger.info(FormatUtil.format(locale.invalidPlayer()));

                            return false;
                        }
                    } else {
                        logger.info(FormatUtil.format(locale.unknownArgument()));

                        return false;
                    }
                }

                default -> {
                    logger.info(FormatUtil.format(locale.unknownArgument()));

                    return false;
                }
            }
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> subCmds = new ArrayList<>();

        if(sender instanceof Player) {
            switch (args.length) {
                case 1 -> {
                    if (sender.hasPermission("skysellwands.commands.skysellwand.give")) subCmds.add("give");
                    if (sender.hasPermission("skysellwands.commands.skysellwands.help")) subCmds.add("help");
                    if (sender.hasPermission("skysellwands.commands.skysellwands.reload")) subCmds.add("reload");
                }

                case 2 -> {
                    if(args[0].equalsIgnoreCase("give")) {
                        if (sender.hasPermission("skysellwands.commands.skysellwands.give")) {
                            for (Player p : skySellWands.getServer().getOnlinePlayers()) {
                                subCmds.add(p.getName());
                            }
                        }
                    }
                }
            }
        } else {
            switch(args.length) {
                case 1 -> {
                    subCmds.add("give");
                    subCmds.add("help");
                    subCmds.add("reload");
                }

                case 2 -> {
                    if(args[0].equalsIgnoreCase("give")) {
                        for (Player p : skySellWands.getServer().getOnlinePlayers()) {
                            subCmds.add(p.getName());
                        }
                    }
                }
            }
        }

        return subCmds;
    }
}
