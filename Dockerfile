ARG APP_INSIGHTS_AGENT_VERSION=2.6.1

# Build image

FROM busybox as downloader

RUN wget -P /tmp http://github.com/microsoft/ApplicationInsights-Java/releases/download/2.6.1/applicationinsights-agent-2.6.1.jar

# Application image

FROM hmctspublic.azurecr.io/base/java:openjdk-11-distroless-1.4

COPY --from=downloader /tmp/applicationinsights-agent-${APP_INSIGHTS_AGENT_VERSION}.jar /opt/app/

COPY lib/AI-Agent.xml /opt/app/
COPY build/libs/bulk-scan-ccd-event-handler-sample-app.jar /opt/app/

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" wget -q --spider http://localhost:8484/health || exit 1

EXPOSE 8484
CMD [ "bulk-scan-ccd-event-handler-sample-app.jar" ]
