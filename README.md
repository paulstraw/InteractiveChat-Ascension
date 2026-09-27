# InteractiveChat-Ascension

**An unofficial fork of [LOOHP's InteractiveChat DiscordSRV Addon](https://github.com/LOOHP/InteractiveChat-DiscordSRV-Addon) for [DiscordSRV Ascension](https://github.com/DiscordSRV/Ascension).**
It isn't made or supported by LOOHP or the DiscordSRV team. Please report problems here, not to them.

When players use [InteractiveChat](https://github.com/LOOHP/InteractiveChat) placeholders such as `[item]`, `[inv]` and `[ender]` in chat, the message DiscordSRV Ascension relays to Discord shows:

- the placeholder as readable text (the item's name, "Steve's Inventory", and so on) instead of InteractiveChat's internal markers, and
- rendered images: the item's icon, its full in-game tooltip, and inventory and ender chest grids, with a menu to inspect any slot.

It also adds Discord slash commands: `/item`, `/inv`, `/ender` (and `…asuser` variants), `/playerinfo`, `/playerlist` and `/resourcepack`.

Nearly all of the rendering is LOOHP's work. This fork replaces the part that talked to legacy DiscordSRV with DiscordSRV Ascension's API.

## Requirements

- Paper 26.2 (the only version this fork is built and tested for)
- [InteractiveChat](https://modrinth.com/plugin/interactivechat) 2026.1.1 or newer, with its dependencies
- [DiscordSRV Ascension](https://github.com/DiscordSRV/Ascension) (tested against commit in [`ASCENSION_COMMIT`](ASCENSION_COMMIT)). Ascension has no stable API yet, so a newer Ascension build can break this plugin.

On first start the plugin downloads Minecraft's assets and a few libraries from LOOHP's asset API (`api.loohpjames.com`) and `resources.download.minecraft.net`, and caches them in `plugins/InteractiveChat-Ascension`. If they can't be downloaded, chat still reaches Discord as text, without images, and the console says so once.

## Differences from the upstream addon

- Works with DiscordSRV Ascension only, not legacy DiscordSRV.
- Images are attached to the message DiscordSRV sends, rather than sent and then edited in, and the webhook username and avatar DiscordSRV chose are kept.
- Slash commands are registered through DiscordSRV's own command registry, so its commands (such as `/link`) keep working. They work in any channel; use Discord's *Server Settings → Integrations* to limit where.
- Not included: Discord-to-game attachment previews (ImageFrame), death message and advancement images, translating `@player` into Discord mentions (Ascension handles mentions itself), bStats, and the update checker.
- The command is `/ica` (`/interactivechatascension`), permissions start with `interactivechatascension.`, and the config lives in `plugins/InteractiveChat-Ascension`.

## Building

You need JDK 25, Maven and git. Two dependencies aren't published anywhere, so scripts build them into your local Maven repository first:

```sh
scripts/install-craftbukkit.sh 26.2     # Spigot BuildTools, a couple of minutes
scripts/install-ascension-api.sh        # DiscordSRV Ascension's API, at the commit in ASCENSION_COMMIT
mvn package
```

The plugin is `common/target/InteractiveChat-Ascension-<version>.jar`.

See [CONTRIBUTING.md](CONTRIBUTING.md) for how the fork is laid out and how to merge upstream changes.

## License and credits

GPLv3, like the upstream addon. Copyright LoohpJames and contributors for the upstream code; LOOHP's copyright headers are kept on every file that came from it. If you find this useful, consider [supporting LOOHP](https://github.com/sponsors/LOOHP), who wrote the part that matters.

DiscordSRV Ascension's API (MIT) is used as a compile-time dependency and isn't bundled.
