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

package com.loohp.interactivechatdiscordsrvaddon.api.events;

import com.discordsrv.api.discord.entity.channel.DiscordGuildMessageChannel;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordMessageContent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * This event is called after the plugin generates the required images, but before
 * DiscordSRV sends the message to the channels of one Discord server.
 * <p>
 * Cancelling this event sends the message without the images.
 *
 * @author LOOHP
 */
public class DiscordImageEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    private List<DiscordGuildMessageChannel> channels;
    private String originalMessage;
    private String newMessage;
    private List<DiscordMessageContent> discordMessageContents;
    private boolean cancel;

    public DiscordImageEvent(List<DiscordGuildMessageChannel> channels, String originalMessage, String newMessage,
                             List<DiscordMessageContent> discordMessageContents, boolean cancel, boolean async) {
        super(async);
        this.channels = channels;
        this.originalMessage = originalMessage;
        this.newMessage = newMessage;
        this.discordMessageContents = discordMessageContents;
        this.cancel = cancel;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public List<DiscordGuildMessageChannel> getChannels() {
        return channels;
    }

    public String getOriginalMessage() {
        return originalMessage;
    }

    public void setOriginalMessage(String originalMessage) {
        this.originalMessage = originalMessage;
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    public List<DiscordMessageContent> getDiscordMessageContents() {
        return discordMessageContents;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

}
