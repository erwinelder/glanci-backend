FROM amazoncorretto:17-alpine-jdk
WORKDIR /glanci_backend
COPY . .
RUN ./gradlew clean build -x test
EXPOSE 8080
CMD ["./gradlew", "run"]