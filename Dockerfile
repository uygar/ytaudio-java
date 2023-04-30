FROM openjdk:8-jdk-alpine
WORKDIR /src
COPY . .
RUN apk add --no-cache maven && \
    mvn clean package

# vimagick/youtube-dl contains ffmpeg, youtube-dl.
FROM debian
RUN apt-get -qqy update && \
    apt-get -qqy install curl ffmpeg python3
RUN curl -L https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -o /usr/local/bin/yt-dlp && \
    chmod a+rx /usr/local/bin/yt-dlp

COPY --from=0 /src/target/*.war /app.war
ENTRYPOINT ["java", "-jar", "/app.war"]
