FROM adoptopenjdk:11-jre-hotspot

WORKDIR D:/Nesan/Container/Middleware_Odin

COPY /target/orders-module-1.0.0-runner.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]