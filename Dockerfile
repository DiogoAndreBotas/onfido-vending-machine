FROM amazoncorretto:17 as build
LABEL maintainer='Diogo André Botas'

WORKDIR /src
ADD . /src
RUN ./gradlew clean assemble --no-daemon

FROM amazoncorretto:17 as production
WORKDIR /app
COPY --from=build /src/build/libs/*.jar .
CMD exec java $JAVA_OPTS -jar *.jar