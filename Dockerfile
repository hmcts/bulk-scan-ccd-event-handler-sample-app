ARG APP_INSIGHTS_AGENT_VERSION=2.5.0

FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.4

COPY lib/applicationinsights-agent-2.5.0.jar lib/AI-Agent.xml /opt/app/
COPY build/libs/bulk-scan-ccd-event-handler-sample-app.jar /opt/app/

HEALTHCHECK --interval=10s --timeout=10s --retries=10 CMD http_proxy="" wget -q --spider http://localhost:8484/health || exit 1

EXPOSE 8484
CMD [ "bulk-scan-ccd-event-handler-sample-app.jar" ]
