FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="Marcel Hendrich <inpercima@gmail.com>"

# see https://github.com/adoptium/containers/blob/main/17/jdk/alpine/Dockerfile.releases.full#L26
ENV LANG='de_DE.UTF-8' LANGUAGE='de_DE:de' LC_ALL='de_DE.UTF-8'

RUN echo "Europe/Berlin" > /etc/timezone

ENV USER=inpercima
RUN addgroup -g 1000 ${USER} \
 && adduser -D -u 1000 -G ${USER} ${USER}

# see https://github.com/nodejs/docker-node/blob/master/docs/BestPractices.md#non-root-user
USER ${USER}
WORKDIR /home/${USER}

COPY --chown=${USER} application-prod.yml .
COPY --chown=${USER} *.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java","-XshowSettings:vm","-XX:MaxRAMPercentage=95","-jar","./app.jar","--spring.profiles.active=prod"]
