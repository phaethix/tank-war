#!/usr/bin/env bash
set -euo pipefail

PROJECT_ROOT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$PROJECT_ROOT"

JAR_PATH="target/tank-war-1.0-SNAPSHOT.jar"

echo "Building tank-war..."
mvn clean package

echo "Starting tank-war..."
java --enable-preview -jar "$JAR_PATH"
