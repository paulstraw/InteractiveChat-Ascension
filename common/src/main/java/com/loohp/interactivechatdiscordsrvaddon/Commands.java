/*
 * This file is part of InteractiveChatDiscordSrvAddon2.
 *
 * Copyright (C) 2020 - 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2020 - 2025. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package com.loohp.interactivechatdiscordsrvaddon;

import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.api.InteractiveChatAPI;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.event.HoverEvent;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.format.NamedTextColor;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ComponentStyling;
import com.loohp.interactivechat.utils.LanguageUtils;
import com.loohp.interactivechatdiscordsrvaddon.api.events.InteractiveChatDiscordSRVConfigReloadEvent;
import com.loohp.interactivechatdiscordsrvaddon.registry.ResourceRegistry;
import com.loohp.interactivechatdiscordsrvaddon.resources.ResourcePackInfo;
import com.loohp.interactivechatdiscordsrvaddon.utils.ResourcePackInfoUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.TranslationKeyUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Commands implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("interactivechatascension")) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.AQUA + "InteractiveChat-Ascension: an unofficial fork of LOOHP's InteractiveChat DiscordSRV Addon, for DiscordSRV Ascension");
            sender.sendMessage(ChatColor.GOLD + "You are running InteractiveChat-Ascension version: " + InteractiveChatDiscordSrvAddon.plugin.getDescription().getVersion());
            return true;
        }

        if (args[0].equalsIgnoreCase("status")) {
            if (sender.hasPermission("interactivechatascension.status")) {
                sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.defaultResourceHashLang.replaceFirst("%s", InteractiveChatDiscordSrvAddon.plugin.defaultResourceHash + " (" + InteractiveChat.exactMinecraftVersion + ")"));
                sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.loadedResourcesLang);
                for (ResourcePackInfo info : InteractiveChatDiscordSrvAddon.plugin.getResourceManager().getResourcePackInfo()) {
                    Component name = ResourcePackInfoUtils.resolveName(info);
                    if (info.getStatus()) {
                        Component component = Component.text(" - ").append(name).color(info.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) == 0 ? NamedTextColor.GREEN : NamedTextColor.YELLOW);
                        Component hoverComponent = ResourcePackInfoUtils.resolveDescription(info);
                        if (info.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) > 0) {
                            hoverComponent = hoverComponent.append(Component.text("\n")).append(Component.translatable(TranslationKeyUtils.getNewIncompatiblePack()).color(NamedTextColor.YELLOW));
                        } else if (info.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) < 0) {
                            hoverComponent = hoverComponent.append(Component.text("\n")).append(Component.translatable(TranslationKeyUtils.getOldIncompatiblePack()).color(NamedTextColor.YELLOW));
                        }
                        component = component.hoverEvent(HoverEvent.showText(hoverComponent));
                        InteractiveChatAPI.sendMessage(sender, component);
                        if (!(sender instanceof Player)) {
                            for (Component each : ComponentStyling.splitAtLineBreaks(ResourcePackInfoUtils.resolveDescription(info))) {
                                InteractiveChatAPI.sendMessage(sender, Component.text("   - ").color(NamedTextColor.GRAY).append(each));
                                if (info.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) > 0) {
                                    sender.sendMessage(ChatColor.YELLOW + "     " + LanguageUtils.getTranslation(TranslationKeyUtils.getNewIncompatiblePack(), InteractiveChatDiscordSrvAddon.plugin.language).getResult());
                                } else if (info.compareServerPackFormat(ResourceRegistry.RESOURCE_PACK_VERSION) < 0) {
                                    sender.sendMessage(ChatColor.YELLOW + "     " + LanguageUtils.getTranslation(TranslationKeyUtils.getOldIncompatiblePack(), InteractiveChatDiscordSrvAddon.plugin.language).getResult());
                                }
                            }
                        }
                    } else {
                        Component component = Component.text(" - ").append(name).color(NamedTextColor.RED);
                        if (info.getRejectedReason() != null) {
                            component = component.hoverEvent(HoverEvent.showText(Component.text(info.getRejectedReason()).color(NamedTextColor.RED)));
                        }
                        InteractiveChatAPI.sendMessage(sender, component);
                        if (!(sender instanceof Player)) {
                            InteractiveChatAPI.sendMessage(sender, Component.text("   - ").append(Component.text(info.getRejectedReason()).color(NamedTextColor.RED)).color(NamedTextColor.RED));
                        }
                    }
                }
            } else {
                InteractiveChat.sendMessage(sender, InteractiveChat.noPermissionMessage);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reloadconfig")) {
            if (sender.hasPermission("interactivechatascension.reloadconfig")) {
                try {
                    if (InteractiveChatDiscordSrvAddon.plugin.resourceReloadLock.tryLock(0, TimeUnit.MILLISECONDS)) {
                        try {
                            InteractiveChatDiscordSrvAddon.plugin.reloadConfig();
                            Bukkit.getPluginManager().callEvent(new InteractiveChatDiscordSRVConfigReloadEvent());
                            sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.reloadConfigMessage);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        } finally {
                            InteractiveChatDiscordSrvAddon.plugin.resourceReloadLock.unlock();
                        }
                    } else {
                        sender.sendMessage(ChatColor.YELLOW + "Resource reloading in progress, please wait!");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                InteractiveChat.sendMessage(sender, InteractiveChat.noPermissionMessage);
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("reloadtexture")) {
            List<String> argList = Arrays.asList(args);
            boolean clean = argList.contains("--reset");
            boolean redownload = argList.contains("--redownload") || clean;
            if (sender.hasPermission("interactivechatascension.reloadtexture")) {
                sender.sendMessage(InteractiveChatDiscordSrvAddon.plugin.reloadTextureMessage);
                InteractiveChatDiscordSrvAddon.plugin.reloadTextures(redownload, clean, sender);
            } else {
                InteractiveChat.sendMessage(sender, InteractiveChat.noPermissionMessage);
            }
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Unknown subcommand. Try /" + label + " status");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> tab = new LinkedList<>();
        if (!cmd.getName().equalsIgnoreCase("interactivechatascension")) {
            return tab;
        }

        switch (args.length) {
            case 0:
                if (sender.hasPermission("interactivechatascension.reloadconfig")) {
                    tab.add("reloadconfig");
                }
                if (sender.hasPermission("interactivechatascension.reloadtexture")) {
                    tab.add("reloadtexture");
                }
                if (sender.hasPermission("interactivechatascension.status")) {
                    tab.add("status");
                }
                return tab;
            case 1:
                if (sender.hasPermission("interactivechatascension.reloadconfig")) {
                    if ("reloadconfig".startsWith(args[0].toLowerCase())) {
                        tab.add("reloadconfig");
                    }
                }
                if (sender.hasPermission("interactivechatascension.reloadtexture")) {
                    if ("reloadtexture".startsWith(args[0].toLowerCase())) {
                        tab.add("reloadtexture");
                    }
                }
                if (sender.hasPermission("interactivechatascension.status")) {
                    if ("status".startsWith(args[0].toLowerCase())) {
                        tab.add("status");
                    }
                }
                return tab;
            case 2:
                if (sender.hasPermission("interactivechatascension.reloadtexture")) {
                    if ("reloadtexture".equals(args[0].toLowerCase())) {
                        if ("--redownload".startsWith(args[1].toLowerCase())) {
                            tab.add("--redownload");
                        }
                        if ("--reset".startsWith(args[1].toLowerCase())) {
                            tab.add("--reset");
                        }
                    }
                }
                return tab;
            default:
                return tab;
        }
    }

}
