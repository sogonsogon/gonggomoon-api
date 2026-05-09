# 1. Base Image: 가장 가벼운 Java 17 실행 환경 사용
FROM eclipse-temurin:17-jre-alpine

# 2. 컨테이너 내부의 작업 폴더 지정
WORKDIR /app

# 3. GitHub Actions에서 빌드한 jar 파일을 컨테이너로 복사
# (build/libs 폴더 안의 jar 파일을 app.jar로 이름 바꿔서 복사)
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 4. 애플리케이션 포트 노출 (명세 목적)
EXPOSE 8080

# 5. 컨테이너 실행 시 작동할 명령어 (메모리 제한 필수!)
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "app.jar"]