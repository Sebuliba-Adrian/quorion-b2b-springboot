FROM eclipse-temurin:17-jre-alpine

LABEL maintainer="Sebuliba Adrian <adrian@agentimarketplace.com>"
LABEL org.opencontainers.image.source="https://github.com/Sebuliba-Adrian/quorion-b2b-springboot"
LABEL org.opencontainers.image.description="Quorion B2B API - Distributor Buyer-Seller Negotiation System"

WORKDIR /app

RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

COPY target/*.jar app.jar

RUN chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENV JAVA_OPTS="-Xms256m -Xmx512m"

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget -q --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
