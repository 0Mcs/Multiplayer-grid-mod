#!/usr/bin/env bash
# Builds every supported Minecraft version and copies the jars into libs/<version>/.
# Requires a JDK that Fabric Loom accepts (25 for 26.x, 21+ for 1.21.x).
# Set JAVA_HOME if your default JDK is not the one you want to use.
set -euo pipefail
cd "$(dirname "$0")"

GRADLE=(--no-daemon -q)
if [ -n "${JAVA_HOME:-}" ]; then
	GRADLE+=(-Dorg.gradle.java.home="$JAVA_HOME")
fi

mkdir -p libs

# 26.x line: version -> dependency range
build_26() {
	local version="$1" dep="$2"
	echo "== 26.x: $version"
	./gradlew "${GRADLE[@]}" -Pmc="$version" -Pmc_dep="$dep" -Ploader_version=0.20.1 clean build
	mkdir -p "libs/$version"
	cp "build/libs/Multiplayer Grid $version-0.1.0.jar" "libs/$version/"
}

build_26 26.1 "~26.1"
build_26 26.1.2 "~26.1"
build_26 26.2 "~26.2"
build_26 26.3 "~26.3"

# 1.21.x line (currently verified against 1.21.11).
build_121() {
	local version="$1"
	echo "== 1.21.x: $version"
	( cd legacy && ./gradlew "${GRADLE[@]}" -Pminecraft_version="$version" clean build )
	mkdir -p "libs/$version"
	cp "legacy/build/libs/Multiplayer Grid $version-0.1.0.jar" "libs/$version/"
}

build_121 1.21.11

echo "Done. Jars in libs/"
find libs -name '*.jar' | sort
