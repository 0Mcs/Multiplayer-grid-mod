#!/usr/bin/env bash
# Builds every supported Minecraft version and copies the jars into libs/<version>/.
set -euo pipefail
cd "$(dirname "$0")"
export JAVA_HOME="${JAVA_HOME:-/home/u01ar/.local/opt/jdk-25.0.5+8}"
GRADLE=(-Dorg.gradle.java.home="$JAVA_HOME" --no-daemon -q)

mkdir -p libs

# 26.x line (official mappings)
for v in 26.1 26.1.2 26.2 26.3; do
	dep="~$v"
	case "$v" in 26.1*|26.2*) dep="~26.$([ "$v" = "26.1" ] && echo 1 || ([ "$v" = "26.1.2" ] && echo 1 || echo 2))" ;; esac
	echo "== 26.x: $v"
	./gradlew "${GRADLE[@]}" -Pmc="$v" -Pmc_dep="$dep" -Ploader_version=0.20.1 clean build
	mkdir -p "libs/$v"
	cp build/libs/Multiplayer\ Grid\ "$v"-0.1.0.jar "libs/$v/"
done

# 1.21.x line verified against 1.21.11 (official mappings, new GUI API)
echo "== 1.21.x: 1.21.11"
( cd legacy && ./gradlew "${GRADLE[@]}" -Pminecraft_version=1.21.11 clean build )
mkdir -p "libs/1.21.11"
cp legacy/build/libs/Multiplayer\ Grid\ 1.21.11-0.1.0.jar "libs/1.21.11/"

echo "Done. Jars in libs/"
find libs -name '*.jar' | sort
