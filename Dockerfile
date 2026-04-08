# syntax=docker/dockerfile:1.4

  # ---- Build stage ----------------------------------------------------------
  FROM eclipse-temurin:17-jdk AS builder

  WORKDIR /build
  COPY . .

  # Build the shadow (fat) jar. The gradlew script isn't checked in, so we
  # invoke the wrapper jar directly. -x test skips tests for speed.
  RUN java -classpath gradle/wrapper/gradle-wrapper.jar \
        org.gradle.wrapper.GradleWrapperMain \
        --no-daemon --console=plain \
        :game-server:shadowJar -x test

  # ---- Runtime stage --------------------------------------------------------
  FROM eclipse-temurin:17-jre

  # Layout inside the container:
  #   /app/
  #   ├── data/         (bind-mounted from host: cache, xteas.json, rsa/, saves/)
  #   ├── game.yml      (bind-mounted from host)
  #   ├── dev-settings.yml (bind-mounted from host)
  #   └── server/
  #       └── alter.jar (the shadow fat jar)
  #
  # Working directory must be `server/` because Alter's code uses relative paths
  # like `../data/cache`, `../data/rsa/key.pem`, `../game.yml`.

  WORKDIR /app/server
  COPY --from=builder /build/game-server/build/libs/game-server-*-all.jar ./alter.jar

  EXPOSE 43594

  # `echo y` accepts the RSA-key generation prompt on first run.
  # After the keypair is persisted to /app/data/rsa/key.pem, subsequent runs
  # don't prompt — the y just gets ignored.
  ENTRYPOINT ["sh", "-c", "echo y | exec java -Xmx1500m -jar alter.jar"]
