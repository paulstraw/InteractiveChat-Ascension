# Contributing

## Layout

This is a fork of [LOOHP/InteractiveChat-DiscordSRV-Addon](https://github.com/LOOHP/InteractiveChat-DiscordSRV-Addon), kept close to upstream so new Minecraft versions can be merged in.

- `common/`: the plugin. Everything that talks to Discord is in `listeners/OutboundToDiscordEvents`, `listeners/DiscordCommands`, `listeners/DiscordInteractionEvents`, `utils/DiscordContentUtils` and the main class; that's where this fork differs from upstream. Rendering (`graphics/`, `resources/`, most of `utils/`) is upstream's and should stay untouched where possible.
- `abstraction/` and `V26_2/`: the NMS layer. Only the module for the supported Minecraft version is built (see `<modules>` in `pom.xml`). The other `V*` directories are upstream's, kept unbuilt so merges don't conflict.
- Source files keep upstream's package, `com.loohp.interactivechatdiscordsrvaddon`, and upstream's class names. The shade plugin moves them to `com.paulstraw.icascension` in the jar, so the plugin can't clash with the upstream addon. `plugin.yml` names the relocated main class.

## Dependencies that aren't published

- **CraftBukkit** for the NMS module: `scripts/install-craftbukkit.sh <version>` runs Spigot's BuildTools.
- **DiscordSRV Ascension's API**: `scripts/install-ascension-api.sh` builds it at the commit in `ASCENSION_COMMIT`. It includes JDA, already relocated to `com.discordsrv.dependencies.net.dv8tion.jda`, so code uses JDA through those package names and no relocation is needed. Set `ASCENSION_COMMIT=main` in the environment to try the latest Ascension instead.

To move to a newer Ascension, update `ASCENSION_COMMIT`, run `scripts/install-ascension-api.sh`, build and test. CI also builds against Ascension's `main` once a week to catch API changes early.

## Merging upstream

```sh
git remote add upstream https://github.com/LOOHP/InteractiveChat-DiscordSRV-Addon.git   # once
git fetch upstream
git merge upstream/master
```

Expect conflicts in the Discord files listed above, and modify/delete conflicts for files this fork removed (Discord-to-game previews, ImageFrame, death and advancement handling, bStats, the updater). Keep them deleted unless the feature is being brought back.

For a new Minecraft version, upstream adds a `V<version>` module. Add it to `<modules>` in the root `pom.xml` and as a dependency in `common/pom.xml`, build CraftBukkit for it, and update the CI workflow's version.
