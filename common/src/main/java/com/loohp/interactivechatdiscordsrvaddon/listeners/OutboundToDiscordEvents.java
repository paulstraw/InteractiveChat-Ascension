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

import com.discordsrv.api.discord.entity.message.DiscordMessageEmbed;
import com.discordsrv.api.discord.entity.message.ReceivedDiscordMessage;
import com.discordsrv.api.discord.entity.message.SendableDiscordMessage;
import com.discordsrv.api.eventbus.EventPriorities;
import com.discordsrv.api.eventbus.Subscribe;
import com.discordsrv.api.events.message.post.game.GameChatMessagePostEvent;
import com.discordsrv.api.events.message.postprocess.game.GameChatMessagePostProcessEvent;
import com.discordsrv.api.events.message.preprocess.game.GameChatMessagePreProcessEvent;
import com.discordsrv.dependencies.net.dv8tion.jda.api.components.actionrow.ActionRow;
import com.discordsrv.dependencies.net.dv8tion.jda.api.entities.MessageEmbed;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.api.InteractiveChatAPI;
import com.loohp.interactivechat.libs.com.loohp.platformscheduler.Scheduler;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.TextReplacementConfig;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import com.loohp.interactivechat.objectholders.CustomPlaceholder;
import com.loohp.interactivechat.objectholders.ICInventoryHolder;
import com.loohp.interactivechat.objectholders.ICPlaceholder;
import com.loohp.interactivechat.objectholders.ICPlayer;
import com.loohp.interactivechat.objectholders.ICPlayerFactory;
import com.loohp.interactivechat.objectholders.OfflineICPlayer;
import com.loohp.interactivechat.objectholders.PlaceholderCooldownManager;
import com.loohp.interactivechat.objectholders.ValuePairs;
import com.loohp.interactivechat.objectholders.WebData;
import com.loohp.interactivechat.registry.Registry;
import com.loohp.interactivechat.utils.ChatColorUtils;
import com.loohp.interactivechat.utils.ColorUtils;
import com.loohp.interactivechat.utils.ComponentFlattening;
import com.loohp.interactivechat.utils.ComponentFont;
import com.loohp.interactivechat.utils.ComponentModernizing;
import com.loohp.interactivechat.utils.ComponentReplacing;
import com.loohp.interactivechat.utils.ComponentUtils;
import com.loohp.interactivechat.utils.CustomStringUtils;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.InventoryUtils;
import com.loohp.interactivechat.utils.ItemStackUtils;
import com.loohp.interactivechat.utils.PlaceholderParser;
import com.loohp.interactivechat.utils.PlayerUtils;
import com.loohp.interactivechatdiscordsrvaddon.InteractiveChatDiscordSrvAddon;
import com.loohp.interactivechatdiscordsrvaddon.api.events.DiscordImageEvent;
import com.loohp.interactivechatdiscordsrvaddon.api.events.GameMessagePostProcessEvent;
import com.loohp.interactivechatdiscordsrvaddon.api.events.GameMessagePreProcessEvent;
import com.loohp.interactivechatdiscordsrvaddon.api.events.GameMessageProcessInventoryEvent;
import com.loohp.interactivechatdiscordsrvaddon.api.events.GameMessageProcessItemEvent;
import com.loohp.interactivechatdiscordsrvaddon.api.events.GameMessageProcessPlayerInventoryEvent;
import com.loohp.interactivechatdiscordsrvaddon.debug.Debug;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.DiscordMessageContent;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.HoverClickDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.IDProvider;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ImageDisplayData;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.ImageDisplayType;
import com.loohp.interactivechatdiscordsrvaddon.objectholders.InteractionHandler;
import com.loohp.interactivechatdiscordsrvaddon.registry.DiscordDataRegistry;
import com.loohp.interactivechatdiscordsrvaddon.utils.ComponentStringUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.DiscordContentUtils;
import com.loohp.interactivechatdiscordsrvaddon.utils.ReplayableInputStream;
import com.loohp.interactivechatdiscordsrvaddon.utils.TranslationKeyUtils;
import com.loohp.interactivechatdiscordsrvaddon.wrappers.TitledInventoryWrapper;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.awt.Color;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.IntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutboundToDiscordEvents implements Listener {

    public static final Comparator<DiscordDisplayData> DISPLAY_DATA_COMPARATOR = Comparator.comparing(each -> each.getPosition());
    public static final Int2ObjectMap<DiscordDisplayData> DATA = Int2ObjectMaps.synchronize(new Int2ObjectLinkedOpenHashMap<>());
    public static final IntFunction<Pattern> DATA_PATTERN = i -> Pattern.compile("<ICD=" + i + "\\\\?>");
    private static final Pattern ANY_DATA_PATTERN = Pattern.compile("<ICD=([0-9]+)\\\\?>");
    private static final IDProvider DATA_ID_PROVIDER = new IDProvider();

    /**
     * What to render for each chat message, from DiscordSRV's pre-process event until the message is sent.
     * Upstream marked the message text with {@code <ICD=id>} and edited the message after Discord received it;
     * DiscordSRV Ascension lets us attach everything before sending instead, so the markers never leave this class.
     * Weak keys: DiscordSRV drops the event once the message is sent (or skipped).
     */
    private static final Map<GameChatMessagePreProcessEvent, PendingMessage> PENDING = Collections.synchronizedMap(new WeakHashMap<>());

    @Subscribe(priority = EventPriorities.DEFAULT)
    public void onGameToDiscord(GameChatMessagePreProcessEvent event) {
        Debug.debug("Triggering onGameToDiscord");
        InteractiveChatDiscordSrvAddon.plugin.messagesCounter.incrementAndGet();

        ICPlayer icSender = ICPlayerFactory.getICPlayer(event.getPlayer().uniqueId());
        if (icSender == null) {
            return;
        }
        Component message = stripInteractiveChatTags(ComponentStringUtils.toRegularComponent(event.getMessage()));

        message = processGameMessage(icSender, message);

        if (message == null) {
            event.setCancelled(true);
            return;
        }

        List<DiscordDisplayData> dataList = new ArrayList<>();
        Matcher matcher = ANY_DATA_PATTERN.matcher(PlainTextComponentSerializer.plainText().serialize(message));
        while (matcher.find()) {
            DiscordDisplayData data = DATA.remove(Integer.parseInt(matcher.group(1)));
            if (data != null) {
                dataList.add(data);
            }
        }
        message = message.replaceText(TextReplacementConfig.builder().match(ANY_DATA_PATTERN).replacement("").build());
        if (!dataList.isEmpty()) {
            dataList.sort(DISPLAY_DATA_COMPARATOR);
            PENDING.put(event, new PendingMessage(icSender, dataList));
        }

        event.setMessage(ComponentStringUtils.toDiscordSRVComponent(message));
    }

    /**
     * Called once per Discord server the message goes to. Adds the rendered images, embeds and components
     * to DiscordSRV's message, keeping its content, webhook username and avatar.
     */
    @Subscribe(priority = EventPriorities.DEFAULT)
    public void onGameToDiscordPostProcess(GameChatMessagePostProcessEvent event) {
        PendingMessage pending = PENDING.get(event.getPreEvent());
        if (pending == null) {
            return;
        }
        Debug.debug("onGameToDiscordPostProcess creating contents");
        ValuePairs<List<DiscordMessageContent>, InteractionHandler> pair = pending.getContents();
        List<DiscordMessageContent> contents = pair.getFirst();
        InteractionHandler interactionHandler = pair.getSecond();

        SendableDiscordMessage original = event.getMessage();
        String text = original.getContent() == null ? "" : original.getContent();
        DiscordImageEvent discordImageEvent = new DiscordImageEvent(event.getChannels(), text, text, contents, false, !Bukkit.isPrimaryThread());
        Bukkit.getPluginManager().callEvent(discordImageEvent);
        Debug.debug("onGameToDiscordPostProcess sending to discord, Cancelled: " + discordImageEvent.isCancelled());
        if (discordImageEvent.isCancelled()) {
            return;
        }

        SendableDiscordMessage.Builder builder = copyOf(original).setContent(discordImageEvent.getNewMessage());
        int i = 0;
        for (DiscordMessageContent content : contents) {
            i += content.getAttachments().size();
            if (i <= 10) {
                ValuePairs<List<MessageEmbed>, Set<String>> valuePair = content.toJDAMessageEmbeds();
                for (MessageEmbed embed : valuePair.getFirst()) {
                    builder.addEmbed(new DiscordMessageEmbed(embed));
                }
                for (Entry<String, byte[]> attachment : content.getAttachments().entrySet()) {
                    if (valuePair.getSecond().contains(attachment.getKey())) {
                        // DiscordSRV sends this one message to every channel in the server, reading each stream once per channel
                        builder.addAttachment(new ReplayableInputStream(attachment.getValue()), attachment.getKey());
                    }
                }
            }
        }
        for (ActionRow row : interactionHandler.getInteractionToRegister()) {
            // DiscordSRV's own builder wraps raw JDA action rows the same way
            builder.addComponent(() -> row);
        }
        event.setMessage(builder.build());
    }

    @Subscribe(priority = EventPriorities.DEFAULT)
    public void onGameToDiscordPost(GameChatMessagePostEvent event) {
        PendingMessage pending = PENDING.get(event.getPreEvent().getPreEvent());
        if (pending == null) {
            return;
        }
        ValuePairs<List<DiscordMessageContent>, InteractionHandler> pair = pending.getContents();
        InteractionHandler interactionHandler = pair.getSecond();
        for (ReceivedDiscordMessage message : event.getDiscordMessage().getMessages()) {
            if (!interactionHandler.getInteractions().isEmpty()) {
                DiscordInteractionEvents.register(message.getChannel().getId() + "/" + message.getId(), interactionHandler, pair.getFirst());
            }
            if (InteractiveChatDiscordSrvAddon.plugin.embedDeleteAfter > 0) {
                String content = message.getContent();
                Scheduler.runTaskLaterAsynchronously(InteractiveChatDiscordSrvAddon.plugin, () -> {
                    message.edit(SendableDiscordMessage.builder().setContent(content).build());
                }, InteractiveChatDiscordSrvAddon.plugin.embedDeleteAfter * 20L);
            }
        }
    }

    private static SendableDiscordMessage.Builder copyOf(SendableDiscordMessage message) {
        SendableDiscordMessage.Builder builder = SendableDiscordMessage.builder()
                .setContent(message.getContent())
                .setAllowedMentions(message.getAllowedMentions())
                .setWebhookUsername(message.getWebhookUsername())
                .setWebhookAvatarUrl(message.getWebhookAvatarUrl())
                .setSuppressedNotifications(message.isSuppressedNotifications())
                .setSuppressedEmbeds(message.isSuppressedEmbeds())
                .setMessageIdToReplyTo(message.getMessageIdToReplyTo());
        message.getEmbeds().forEach(builder::addEmbed);
        message.getComponents().forEach(builder::addComponent);
        for (Entry<InputStream, String> attachment : message.getAttachments().entrySet()) {
            builder.addAttachment(attachment.getKey(), attachment.getValue());
        }
        return builder;
    }

    /**
     * Removes InteractiveChat's sender markers (e.g. {@code <chat=UUID:[item]:>}) and other internal tags,
     * leaving the text players typed. Ported from InteractiveChat's hook for legacy DiscordSRV
     * ({@code com.loohp.interactivechat.hooks.discordsrv.DiscordSRVEvents}), which doesn't run for Ascension.
     */
    public static Component stripInteractiveChatTags(Component component) {
        component = ComponentReplacing.replace(component, Registry.ID_PATTERN.pattern(), false, (result, matchedComponents) -> {
            String placeholder = result.group(4);
            String replacement = placeholder == null ? "" : Registry.ID_UNESCAPE_PATTERN.matcher(placeholder).replaceAll(">");
            return LegacyComponentSerializer.legacySection().deserialize(replacement);
        });
        if (InteractiveChat.chatControlRedHook) {
            component = ComponentReplacing.replace(component, Registry.CHR_ID_PATTERN.pattern(), false, (result, matchedComponents) -> {
                String placeholder = result.group(4);
                String replacement = placeholder == null ? "" : Registry.ID_UNESCAPE_PATTERN.matcher(placeholder).replaceAll(">");
                return LegacyComponentSerializer.legacySection().deserialize(replacement);
            });
        }
        component = ComponentReplacing.replace(component, Registry.MENTION_TAG_CONVERTER.getReversePattern().pattern(), true, (result, matchedComponents) -> {
            return LegacyComponentSerializer.legacySection().deserialize(result.group(2));
        });
        component = component.replaceText(TextReplacementConfig.builder().match(ChatColorUtils.COLOR_TAG_PATTERN).replacement((result, builder) -> {
            String escape = result.group(1);
            return builder.content(escape == null ? "" : escape);
        }).build());
        if (InteractiveChat.fontTags) {
            component = component.replaceText(TextReplacementConfig.builder().match(ComponentFont.FONT_TAG_PATTERN).replacement((result, builder) -> {
                String escape = result.group(2);
                return builder.content(escape == null ? "" : escape);
            }).build());
        }
        return component;
    }

    public Component processGameMessage(ICPlayer icSender, Component component) {
        PlaceholderCooldownManager cooldownManager = InteractiveChatDiscordSrvAddon.plugin.placeholderCooldownManager;
        long now = cooldownManager.checkMessage(icSender.getUniqueId(), PlainTextComponentSerializer.plainText().serialize(component)).getTimeNow();

        GameMessagePreProcessEvent gameMessagePreProcessEvent = new GameMessagePreProcessEvent(icSender, component, false);
        Bukkit.getPluginManager().callEvent(gameMessagePreProcessEvent);
        if (gameMessagePreProcessEvent.isCancelled()) {
            return null;
        }
        component = ComponentFlattening.flatten(gameMessagePreProcessEvent.getComponent());

        String plain = InteractiveChatComponentSerializer.plainText().serialize(component);

        Debug.debug("onGameToDiscord processing custom placeholders");
        for (ICPlaceholder placeholder : InteractiveChatAPI.getICPlaceholderList()) {
            if (!placeholder.isBuildIn()) {
                CustomPlaceholder customP = (CustomPlaceholder) placeholder;
                if (!InteractiveChat.useCustomPlaceholderPermissions || (InteractiveChat.useCustomPlaceholderPermissions && PlayerUtils.hasPermission(icSender.getUniqueId(), customP.getPermission(), true, 200))) {
                    Matcher matcher = customP.getKeyword().matcher(plain);
                    if (matcher.find()) {
                        if (!cooldownManager.isPlaceholderOnCooldownAt(icSender.getUniqueId(), customP, now)) {
                            Component replaceText;
                            if (customP.getReplace().isEnabled()) {
                                replaceText = PlaceholderParser.parse(icSender, customP.getReplace().getReplaceText());
                            } else {
                                replaceText = null;
                            }
                            List<Component> toAppend = new LinkedList<>();
                            Set<Component> shown = new HashSet<>();
                            component = ComponentReplacing.replace(component, customP.getKeyword().pattern(), true, (result, matchedComponents) -> {
                                Component replaceString = replaceText == null ? result.componentGroup() : ComponentUtils.applyReplacementRegex(replaceText, result, 1);
                                if (!shown.contains(replaceString)) {
                                    shown.add(replaceString);
                                    int position = result.start();
                                    if (InteractiveChatDiscordSrvAddon.plugin.hoverEnabled && !InteractiveChatDiscordSrvAddon.plugin.hoverIgnore.contains(customP.getKey())) {
                                        HoverClickDisplayData.Builder hoverClick = new HoverClickDisplayData.Builder().player(icSender).postion(position).color(DiscordDataRegistry.DISCORD_HOVER_COLOR).displayText(PlainTextComponentSerializer.plainText().serialize(replaceString));
                                        boolean usingHoverClick = false;

                                        if (customP.getHover().isEnabled()) {
                                            usingHoverClick = true;
                                            Component hoverText = PlaceholderParser.parse(icSender, ComponentUtils.applyReplacementRegex(customP.getHover().getText(), result, 1));
                                            Color color = ColorUtils.getFirstColor(LegacyComponentSerializer.legacySection().serialize(hoverText));
                                            hoverClick.hoverText(hoverText);
                                            if (color != null) {
                                                hoverClick.color(color);
                                            }
                                        }

                                        if (customP.getClick().isEnabled()) {
                                            usingHoverClick = true;
                                            String clickValue = ChatColorUtils.translateAlternateColorCodes('&', PlaceholderParser.parse(icSender, CustomStringUtils.applyReplacementRegex(customP.getClick().getValue(), result, 1)));
                                            hoverClick.clickAction(customP.getClick().getAction()).clickValue(CustomStringUtils.applyReplacementRegex(clickValue, result, 1));
                                        }

                                        if (usingHoverClick) {
                                            int hoverId = DATA_ID_PROVIDER.getNext();
                                            DATA.put(hoverId, hoverClick.build());
                                            toAppend.add(Component.text("<ICD=" + hoverId + ">"));
                                        }
                                    }
                                }
                                return replaceText == null ? Component.empty().children(matchedComponents) : replaceString;
                            });
                            for (Component componentToAppend : toAppend) {
                                component = component.append(componentToAppend);
                            }
                        } else {
                            return null;
                        }
                    }
                }
            }
        }

        if (InteractiveChat.t && WebData.getInstance() != null) {
            for (CustomPlaceholder customP : WebData.getInstance().getSpecialPlaceholders()) {
                Matcher matcher = customP.getKeyword().matcher(plain);
                if (matcher.find()) {
                    if (!cooldownManager.isPlaceholderOnCooldownAt(icSender.getUniqueId(), customP, now)) {
                        Component replaceText;
                        if (customP.getReplace().isEnabled()) {
                            replaceText = PlaceholderParser.parse(icSender, customP.getReplace().getReplaceText());
                        } else {
                            replaceText = null;
                        }
                        List<Component> toAppend = new LinkedList<>();
                        Set<Component> shown = new HashSet<>();
                        component = ComponentReplacing.replace(component, customP.getKeyword().pattern(), true, (result, matchedComponents) -> {
                            Component replaceString = replaceText == null ? result.componentGroup() : ComponentUtils.applyReplacementRegex(replaceText, result, 1);
                            if (!shown.contains(replaceString)) {
                                shown.add(replaceString);
                                int position = result.start();
                                if (InteractiveChatDiscordSrvAddon.plugin.hoverEnabled && !InteractiveChatDiscordSrvAddon.plugin.hoverIgnore.contains(customP.getKey())) {
                                    HoverClickDisplayData.Builder hoverClick = new HoverClickDisplayData.Builder().player(icSender).postion(position).color(DiscordDataRegistry.DISCORD_HOVER_COLOR).displayText(PlainTextComponentSerializer.plainText().serialize(replaceString));
                                    boolean usingHoverClick = false;

                                    if (customP.getHover().isEnabled()) {
                                        usingHoverClick = true;
                                        Component hoverText = PlaceholderParser.parse(icSender, ComponentUtils.applyReplacementRegex(customP.getHover().getText(), result, 1));
                                        Color color = ColorUtils.getFirstColor(LegacyComponentSerializer.legacySection().serialize(hoverText));
                                        hoverClick.hoverText(hoverText);
                                        if (color != null) {
                                            hoverClick.color(color);
                                        }
                                    }

                                    if (customP.getClick().isEnabled()) {
                                        usingHoverClick = true;
                                        String clickValue = ChatColorUtils.translateAlternateColorCodes('&', PlaceholderParser.parse(icSender, CustomStringUtils.applyReplacementRegex(customP.getClick().getValue(), result, 1)));
                                        hoverClick.clickAction(customP.getClick().getAction()).clickValue(CustomStringUtils.applyReplacementRegex(clickValue, result, 1));
                                    }

                                    if (usingHoverClick) {
                                        int hoverId = DATA_ID_PROVIDER.getNext();
                                        DATA.put(hoverId, hoverClick.build());
                                        toAppend.add(Component.text("<ICD=" + hoverId + ">"));
                                    }
                                }
                            }
                            return replaceText == null ? Component.empty().children(matchedComponents) : replaceString;
                        });
                        for (Component componentToAppend : toAppend) {
                            component = component.append(componentToAppend);
                        }
                    } else {
                        return null;
                    }
                }
            }
        }

        if (InteractiveChat.useItem && PlayerUtils.hasPermission(icSender.getUniqueId(), "interactivechat.module.item", true, 200)) {
            Debug.debug("onGameToDiscord processing item display");
            Matcher matcher = InteractiveChat.itemPlaceholder.getKeyword().matcher(plain);
            if (matcher.find()) {
                if (!cooldownManager.isPlaceholderOnCooldownAt(icSender.getUniqueId(), InteractiveChat.placeholderList.values().stream().filter(each -> each.equals(InteractiveChat.itemPlaceholder)).findFirst().get(), now)) {
                    ItemStack item = PlayerUtils.getHeldItem(icSender);
                    boolean isAir = item.getType().equals(Material.AIR);
                    if (!InteractiveChat.itemAirAllow && isAir) {
                        return null;
                    }
                    String itemStr = PlainTextComponentSerializer.plainText().serialize(ComponentStringUtils.resolve(ComponentModernizing.modernize(ItemStackUtils.getDisplayName(item)), InteractiveChatDiscordSrvAddon.plugin.getResourceManager().getLanguageManager().getTranslateFunction().ofLanguage(InteractiveChatDiscordSrvAddon.plugin.language)));
                    itemStr = ComponentStringUtils.stripColorAndConvertMagic(itemStr);

                    int amount = item.getAmount();
                    if (isAir) {
                        amount = 1;
                    }

                    String replaceText = ComponentStringUtils.stripColorAndConvertMagic(LegacyComponentSerializer.legacySection().serialize(PlaceholderParser.parse(icSender, (amount == 1 ? InteractiveChat.itemSingularReplaceText : InteractiveChat.itemReplaceText.replaceText(TextReplacementConfig.builder().matchLiteral("{Amount}").replacement(Component.text(amount)).build())).replaceText(TextReplacementConfig.builder().matchLiteral("{Item}").replacement(Component.text(itemStr)).build()))));

                    AtomicBoolean replaced = new AtomicBoolean(false);
                    Component replaceComponent = LegacyComponentSerializer.legacySection().deserialize(replaceText);
                    component = ComponentReplacing.replace(component, InteractiveChat.itemPlaceholder.getKeyword().pattern(), true, (groups) -> {
                        replaced.set(true);
                        return replaceComponent;
                    });
                    if (replaced.get() && InteractiveChatDiscordSrvAddon.plugin.itemImage) {
                        int inventoryId = DATA_ID_PROVIDER.getNext();
                        int position = matcher.start();

                        String title = ComponentStringUtils.stripColorAndConvertMagic(PlaceholderParser.parse(icSender, InteractiveChat.itemTitle));

                        Inventory inv = DiscordContentUtils.getBlockInventory(item);

                        GameMessageProcessItemEvent gameMessageProcessItemEvent = new GameMessageProcessItemEvent(icSender, title, component, false, inventoryId, item.clone(), inv);
                        Bukkit.getPluginManager().callEvent(gameMessageProcessItemEvent);
                        if (!gameMessageProcessItemEvent.isCancelled()) {
                            component = gameMessageProcessItemEvent.getComponent();
                            title = gameMessageProcessItemEvent.getTitle();
                            if (gameMessageProcessItemEvent.hasInventory()) {
                                DATA.put(inventoryId, new ImageDisplayData(icSender, position, title, ImageDisplayType.ITEM_CONTAINER, gameMessageProcessItemEvent.getItemStack().clone(), new TitledInventoryWrapper(ItemStackUtils.getDisplayName(item, false), gameMessageProcessItemEvent.getInventory())));
                            } else {
                                DATA.put(inventoryId, new ImageDisplayData(icSender, position, title, ImageDisplayType.ITEM, gameMessageProcessItemEvent.getItemStack().clone()));
                            }
                        }
                        component = component.append(Component.text("<ICD=" + inventoryId + ">"));
                    }
                } else {
                    return null;
                }
            }
        }

        if (InteractiveChat.useInventory && PlayerUtils.hasPermission(icSender.getUniqueId(), "interactivechat.module.inventory", true, 200)) {
            Debug.debug("onGameToDiscord processing inventory display");
            Matcher matcher = InteractiveChat.invPlaceholder.getKeyword().matcher(plain);
            if (matcher.find()) {
                if (!cooldownManager.isPlaceholderOnCooldownAt(icSender.getUniqueId(), InteractiveChat.placeholderList.values().stream().filter(each -> each.equals(InteractiveChat.invPlaceholder)).findFirst().get(), now)) {
                    String replaceText = ComponentStringUtils.stripColorAndConvertMagic(LegacyComponentSerializer.legacySection().serialize(PlaceholderParser.parse(icSender, InteractiveChat.invReplaceText)));

                    AtomicBoolean replaced = new AtomicBoolean(false);
                    Component replaceComponent = LegacyComponentSerializer.legacySection().deserialize(replaceText);
                    component = ComponentReplacing.replace(component, InteractiveChat.invPlaceholder.getKeyword().pattern(), true, (groups) -> {
                        replaced.set(true);
                        return replaceComponent;
                    });

                    if (replaced.get() && InteractiveChatDiscordSrvAddon.plugin.invImage) {
                        int inventoryId = DATA_ID_PROVIDER.getNext();
                        int position = matcher.start();

                        Inventory inv = Bukkit.createInventory(ICInventoryHolder.INSTANCE, 45);
                        for (int j = 0; j < icSender.getInventory().getSize(); j++) {
                            if (icSender.getInventory().getItem(j) != null) {
                                if (!icSender.getInventory().getItem(j).getType().equals(Material.AIR)) {
                                    inv.setItem(j, icSender.getInventory().getItem(j).clone());
                                }
                            }
                        }
                        String title = ComponentStringUtils.stripColorAndConvertMagic(PlaceholderParser.parse(icSender, InteractiveChat.invTitle));

                        GameMessageProcessPlayerInventoryEvent gameMessageProcessPlayerInventoryEvent = new GameMessageProcessPlayerInventoryEvent(icSender, title, component, false, inventoryId, inv);
                        Bukkit.getPluginManager().callEvent(gameMessageProcessPlayerInventoryEvent);
                        if (!gameMessageProcessPlayerInventoryEvent.isCancelled()) {
                            component = gameMessageProcessPlayerInventoryEvent.getComponent();
                            title = gameMessageProcessPlayerInventoryEvent.getTitle();
                            DATA.put(inventoryId, new ImageDisplayData(icSender, position, title, ImageDisplayType.INVENTORY, true, new TitledInventoryWrapper(Component.translatable(TranslationKeyUtils.getDefaultContainerTitle()), gameMessageProcessPlayerInventoryEvent.getInventory())));
                        }

                        component = component.append(Component.text("<ICD=" + inventoryId + ">"));
                    }
                } else {
                    return null;
                }
            }
        }

        if (InteractiveChat.useEnder && PlayerUtils.hasPermission(icSender.getUniqueId(), "interactivechat.module.enderchest", true, 200)) {
            Debug.debug("onGameToDiscord processing enderchest display");
            Matcher matcher = InteractiveChat.enderPlaceholder.getKeyword().matcher(plain);
            if (matcher.find()) {
                if (!cooldownManager.isPlaceholderOnCooldownAt(icSender.getUniqueId(), InteractiveChat.placeholderList.values().stream().filter(each -> each.equals(InteractiveChat.enderPlaceholder)).findFirst().get(), now)) {
                    String replaceText = ComponentStringUtils.stripColorAndConvertMagic(LegacyComponentSerializer.legacySection().serialize(PlaceholderParser.parse(icSender, InteractiveChat.enderReplaceText)));

                    AtomicBoolean replaced = new AtomicBoolean(false);
                    Component replaceComponent = LegacyComponentSerializer.legacySection().deserialize(replaceText);
                    component = ComponentReplacing.replace(component, InteractiveChat.enderPlaceholder.getKeyword().pattern(), true, (groups) -> {
                        replaced.set(true);
                        return replaceComponent;
                    });

                    if (replaced.get() && InteractiveChatDiscordSrvAddon.plugin.enderImage) {
                        int inventoryId = DATA_ID_PROVIDER.getNext();
                        int position = matcher.start();

                        Inventory inv = Bukkit.createInventory(ICInventoryHolder.INSTANCE, InventoryUtils.toMultipleOf9(icSender.getEnderChest().getSize()));
                        for (int j = 0; j < icSender.getEnderChest().getSize(); j++) {
                            if (icSender.getEnderChest().getItem(j) != null) {
                                if (!icSender.getEnderChest().getItem(j).getType().equals(Material.AIR)) {
                                    inv.setItem(j, icSender.getEnderChest().getItem(j).clone());
                                }
                            }
                        }
                        String title = ComponentStringUtils.stripColorAndConvertMagic(PlaceholderParser.parse(icSender, InteractiveChat.enderTitle));

                        GameMessageProcessInventoryEvent gameMessageProcessInventoryEvent = new GameMessageProcessInventoryEvent(icSender, title, component, false, inventoryId, inv);
                        Bukkit.getPluginManager().callEvent(gameMessageProcessInventoryEvent);
                        if (!gameMessageProcessInventoryEvent.isCancelled()) {
                            component = gameMessageProcessInventoryEvent.getComponent();
                            title = gameMessageProcessInventoryEvent.getTitle();
                            DATA.put(inventoryId, new ImageDisplayData(icSender, position, title, ImageDisplayType.ENDERCHEST, new TitledInventoryWrapper(Component.translatable(TranslationKeyUtils.getEnderChestContainerTitle()), gameMessageProcessInventoryEvent.getInventory())));
                        }

                        component = component.append(Component.text("<ICD=" + inventoryId + ">"));
                    }
                } else {
                    return null;
                }
            }
        }

        // Mentions: upstream translated "@name" into Discord mentions here. DiscordSRV Ascension resolves
        // mentions itself (see its "mentions" config), so that is left to it.

        GameMessagePostProcessEvent gameMessagePostProcessEvent = new GameMessagePostProcessEvent(icSender, component, false);
        Bukkit.getPluginManager().callEvent(gameMessagePostProcessEvent);
        if (gameMessagePostProcessEvent.isCancelled()) {
            return null;
        }
        component = gameMessagePostProcessEvent.getComponent();
        return component;
    }

    private static class PendingMessage {

        private final OfflineICPlayer player;
        private final List<DiscordDisplayData> dataList;
        private ValuePairs<List<DiscordMessageContent>, InteractionHandler> contents;

        private PendingMessage(OfflineICPlayer player, List<DiscordDisplayData> dataList) {
            this.player = player;
            this.dataList = dataList;
        }

        /**
         * Renders on first use, then reuses the images for every other Discord server the message goes to.
         */
        public synchronized ValuePairs<List<DiscordMessageContent>, InteractionHandler> getContents() {
            if (contents == null) {
                contents = DiscordContentUtils.createContents(dataList, player);
            }
            return contents;
        }

    }

}
