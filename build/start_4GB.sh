#!/usr/bin/env sh
set -eu

BASE="$(CDPATH= cd -- "$(dirname "$0")" && pwd)"
CP="$BASE/SimYukkuri.jar:$BASE/../lib/*"
exec java -Xms1024m -Xmx4096m -cp "$CP" src.SimYukkuri
