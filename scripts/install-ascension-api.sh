#!/usr/bin/env bash
# Builds DiscordSRV Ascension's API at a pinned commit and installs it into the local Maven repo
# (~/.m2) as com.discordsrv:discordsrv-api:3.0.0-SNAPSHOT. Ascension doesn't publish its API anywhere,
# so this has to run before the first Maven build, and again whenever ASCENSION_COMMIT changes.
set -euo pipefail

root="$(cd "$(dirname "$0")/.." && pwd)"
commit="$(tr -d '[:space:]' < "$root/ASCENSION_COMMIT")"
src="$root/.ascension"
marker="$src/.installed-$commit"

if [[ -f "$marker" && "${1:-}" != "--force" ]]; then
    echo "Ascension API $commit already installed (pass --force to rebuild)"
    exit 0
fi

if [[ ! -d "$src/.git" ]]; then
    git clone --filter=blob:none https://github.com/DiscordSRV/Ascension.git "$src"
fi
git -C "$src" fetch --quiet origin "$commit" || git -C "$src" fetch --quiet origin
git -C "$src" checkout --quiet --detach "$commit"

log="$src/build-api.log"
if ! (cd "$src" && ./gradlew --quiet :api:publishToMavenLocal) > "$log" 2>&1; then
    grep -v 'warning:' "$log" | tail -40
    echo "Ascension API build failed; full log in $log" >&2
    exit 1
fi
rm -f "$src"/.installed-*
touch "$marker"
echo "Installed Ascension API from $commit"
