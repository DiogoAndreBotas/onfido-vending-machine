FROM amazoncorretto:17 as build
LABEL maintainer='DiogoAndreBotas'

WORKDIR /workspace/app
COPY . /workspace/app
RUN ./gradlew assemble --no-daemon --stacktrace

FROM amazoncorretto:17 as production
WORKDIR /app
VOLUME /tmp
COPY --from=build /workspace/app/build/libs/vending-machine.jar /app/
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/vending-machine.jar"]