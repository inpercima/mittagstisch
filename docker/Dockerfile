FROM mcr.microsoft.com/playwright/java:v1.64.0-noble

LABEL maintainer="Marcel Hendrich <inpercima@gmail.com>"

ENV LANG='de_DE.UTF-8' LANGUAGE='de_DE:de' LC_ALL='de_DE.UTF-8'

RUN echo "Europe/Berlin" > /etc/timezone

USER pwuser
WORKDIR /home/pwuser

COPY --chown=pwuser application-prod.yml .
COPY --chown=pwuser *.jar ./app.jar

EXPOSE 8080

ENTRYPOINT ["java","-XshowSettings:vm","-XX:MaxRAMPercentage=95","-jar","./app.jar","--spring.profiles.active=prod"]
