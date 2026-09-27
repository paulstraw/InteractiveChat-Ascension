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

package com.loohp.interactivechatdiscordsrvaddon.listeners;

import com.loohp.interactivechat.libs.com.loohp.platformscheduler.Scheduler;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.HashUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordMessageContent;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.InteractionHandler;
import com.discordsrv.api.eventbus.Subscribe;
import com.discordsrv.dependencies.net.dv8tion.jda.api.entities.Message;
import com.discordsrv.dependencies.net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import com.discordsrv.dependencies.net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent;
import com.discordsrv.dependencies.net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DiscordInteractionEvents {

    public static final String INTERACTION_ID_PREFIX;

    static {
        try {
            String uuid = InteractiveChatDiscordSrvAddon.plugin.getServerId();
            if (uuid == null) {
                uuid = UUID.randomUUID().toString();
            }
            INTERACTION_ID_PREFIX = "ICD_" + HashUtils.createSha1String(new ByteArrayInputStream(uuid.getBytes(StandardCharsets.UTF_8))) + "_";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static final Map<String, InteractionData> REGISTER = new ConcurrentHashMap<>();

    public static void register(Message message, InteractionHandler interactionHandler, List<DiscordMessageContent> discordMessageContent) {
        register(message.getChannel().getId() + "/" + message.getId(), interactionHandler, discordMessageContent);
    }

    public static void register(String messageId, InteractionHandler interactionHandler, List<DiscordMessageContent> discordMessageContent) {
        List<String> interactionIds = interactionHandler.getInteractions();
        InteractionData interactionData = new InteractionData(interactionHandler, discordMessageContent, interactionIds, messageId);
        for (String id : interactionIds) {
            if (!id.startsWith(INTERACTION_ID_PREFIX)) {
                throw new IllegalArgumentException("InteractionIds must start with the INTERACTION_ID_PREFIX, however \"" + id + "\" does not");
            }
            REGISTER.put(id, interactionData);
        }
        Scheduler.runTaskLaterAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
            for (String id : interactionIds) {
                REGISTER.remove(id);
            }
        }, interactionHandler.getExpire() / 50);
    }

    public static InteractionData getInteractionData(String interactionId) {
        return REGISTER.get(interactionId);
    }

    public static void unregisterAll() {
        REGISTER.clear();
    }

    @Subscribe
    public void onButtonClick(ButtonInteractionEvent event) {
        handleInteraction(event);
    }

    @Subscribe
    public void onSelectionMenu(StringSelectInteractionEvent event) {
        handleInteraction(event);
    }

    private void handleInteraction(GenericComponentInteractionCreateEvent event) {
        String id = event.getComponentId();
        if (!id.startsWith(INTERACTION_ID_PREFIX)) {
            return;
        }
        InteractionData data = REGISTER.get(id);
        if (data != null) {
            data.getInteractionHandler().getReactionConsumer().accept(event, data.getContents());
            return;
        }
        event.reply(ChatColorUtils.stripColor(InteractiveChatDiscordSrvAddon.plugin.interactionExpire)).setEphemeral(true).queue();
    }

    public static class InteractionData {

        private InteractionHandler interactionHandler;
        private List<DiscordMessageContent> contents;
        private List<String> interactionIds;
        private List<String> messageIds;

        public InteractionData(InteractionHandler interactionHandler, List<DiscordMessageContent> contents, List<String> interactionIds, List<String> messageIds) {
            this.interactionHandler = interactionHandler;
            this.contents = contents;
            this.interactionIds = interactionIds;
            this.messageIds = messageIds;
        }

        public InteractionData(InteractionHandler interactionHandler, List<DiscordMessageContent> contents, List<String> interactionIds, String messageId) {
            this.interactionHandler = interactionHandler;
            this.contents = contents;
            this.interactionIds = interactionIds;
            List<String> messageIds = new ArrayList<>();
            messageIds.add(messageId);
            this.messageIds = messageIds;
        }

        public InteractionHandler getInteractionHandler() {
            return interactionHandler;
        }

        public List<DiscordMessageContent> getContents() {
            return contents;
        }

        public List<String> getInteractionIds() {
            return interactionIds;
        }

        public List<String> getMessageIds() {
            return messageIds;
        }

    }

}
