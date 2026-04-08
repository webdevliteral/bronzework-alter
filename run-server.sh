#!/usr/bin/env bash
# Convenience launcher for the Alter game server.
# - Uses system JDK 21 just to invoke the Gradle wrapper jar.
# - Gradle's foojay-resolver auto-downloads JDK 17 for the actual server JVM.
# - Uses --no-daemon so Ctrl-C cleanly tears everything down.
set -e
cd "$(dirname "$0")"
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
exec java -classpath gradle/wrapper/gradle-wrapper.jar \
    org.gradle.wrapper.GradleWrapperMain \
    --no-daemon --console=plain :game-server:run
