# ─────────────────────────────────────────────────────────────
# Stage 1 — Build
# Use a full JDK image to compile and package the JAR
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jdk-alpine AS builder

WORKDIR /app

# Copy Maven wrapper and pom first — Docker layer caches dependencies
# so that source-only changes don't re-download the internet
COPY pom.xml .
COPY .mvn/ .mvn/
RUN test -f mvnw || (apk add --no-cache wget && \
    wget -q https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.11/apache-maven-3.9.11-bin.tar.gz -O /tmp/mvn.tar.gz && \
    tar -xzf /tmp/mvn.tar.gz -C /opt && \
    ln -s /opt/apache-maven-3.9.11/bin/mvn /usr/local/bin/mvn)

COPY mvnw* ./
RUN chmod +x mvnw 2>/dev/null || true

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -q 2>/dev/null || mvn dependency:go-offline -q

# Copy source and build, skipping tests (tests run in CI, not image build)
COPY src ./src
RUN mvn clean package -DskipTests -q 2>/dev/null || mvn clean package -DskipTests -q

# ─────────────────────────────────────────────────────────────
# Stage 2 — Runtime
# Minimal JRE image — no compiler, no build tools
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:25-jre-alpine AS runtime

WORKDIR /app

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy only the fat JAR from the builder stage
COPY --from=builder /app/target/booking-service-1.0.0.jar app.jar

EXPOSE 8080

# Virtual Threads are enabled via application.properties
# -XX:+UseZGC pairs well with Virtual Threads for low-latency GC
ENTRYPOINT ["java", "-XX:+UseZGC", "-jar", "app.jar"]