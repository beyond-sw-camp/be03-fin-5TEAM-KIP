FROM openjdk:17-jdk-slim as builder

WORKDIR /app

# gradle 빌드시에 필요한 파일들을 workdir로 copy 
COPY gradlew . 
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# grdlew 파일 실행권한 부여
RUN chmod +x ./gradlew

# /app/build/livs/*.jar 파일 아래 명령어를 통해 실행
RUN ./gradlew bootJar

FROM openjdk:17-jdk-slim

# 설정 파일 복사
#COPY --from=builder /app/src/main/resources/kip-fcm-firebase-adminsdk-gm80d-6a9b5bbd58.json /app/src/main/resources/
#COPY --from=builder /app/src/main/resources/jwt.yml /app/src/main/resources/

COPY --from=builder /app/build/libs/*.jar app.jar

WORKDIR /app

# docker run -d -p 8080:8080 -v C:/Users/Playdata/Desktop/tmp:/tmp order_backend:v1
VOLUME /tmp

#CMD 또는 ENTRYPOINT 를 통해 실행
ENTRYPOINT [ "java","-jar","app.jar"]

# docker실행시 db정보를 환경변수로 주입
# docker run -d -p 8080:8080 -e SPRING_DATASOURCE_URL=jdbc:mariadb://host.docker.internal:3306/spring_order -v C:/Users/Playdata/Desktop/tmp:/tmp order-backend:v1