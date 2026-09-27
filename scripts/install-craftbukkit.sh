#!/usr/bin/env bash
# Builds CraftBukkit with Spigot's BuildTools and installs it into the local Maven repo (~/.m2).
# The NMS module (V26_2) compiles against it, and it isn't published anywhere.
set -euo pipefail

version="${1:-26.2}"
if [[ -f "$HOME/.m2/repository/org/bukkit/craftbukkit/$version-R0.1-SNAPSHOT/craftbukkit-$version-R0.1-SNAPSHOT.jar" && "${2:-}" != "--force" ]]; then
    echo "CraftBukkit $version already installed (pass --force as the second argument to rebuild)"
    exit 0
fi

work="$(cd "$(dirname "$0")/.." && pwd)/.buildtools"
mkdir -p "$work"
curl -fsSLo "$work/BuildTools.jar" https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
if ! (cd "$work" && java -jar BuildTools.jar --rev "$version" --compile craftbukkit) > "$work/buildtools.log" 2>&1; then
    tail -40 "$work/buildtools.log"
    echo "BuildTools failed; full log in $work/buildtools.log" >&2
    exit 1
fi
echo "Installed CraftBukkit $version"
