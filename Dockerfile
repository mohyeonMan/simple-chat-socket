FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 로컬의 모든 파일을 컨테이너로 복사
COPY . .

# 실행 권한 추가 (gradlew 사용을 위해)
RUN chmod +x ./gradlew

# Gradle 빌드 실행
RUN ./gradlew bootJar

# 애플리케이션 실행
CMD ["java", "-jar", "build/libs/simple-chat-socket.jar"]
