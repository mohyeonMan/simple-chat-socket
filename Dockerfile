FROM openjdk:17-jdk-slim
WORKDIR /app
COPY build/libs/simple-chat-socket.jar app.jar
ENTRYPOINT ["sh", "-c", "export INSTANCE_IP=$(hostname -i) && java -jar app.jar"]
