# Java YouTube Downloader & Audio Extractor

This project is a Java-based version of a YouTube video downloader and audio extractor tool, converted from an original Go implementation. The application starts an HTTP server and utilizes `yt-dlp` and `ffmpeg` for video downloading and audio extraction.

## Prerequisites

- Java 8 or higher
- Maven (for building the project)
- `yt-dlp` and `ffmpeg` installed and accessible in the system path

## Building and Running

1. Clone the repository:
```
git clone https://github.com/yourusername/java-youtube-downloader.git
cd java-youtube-downloader
```

2. Build the project using Maven:
```
mvn clean package
```
3. Deploy the generated `.war` file in a servlet container (e.g., Apache Tomcat).

4. Access the application via a web browser with the following format: `http://localhost:8080/watch?v=VIDEO_ID`

## Contributing

Feel free to submit pull requests for improvements, bug fixes, or new features. We appreciate any contribution that helps to enhance the project.

Deploy to Google Cloud Run:

[![Run on Google Cloud](https://deploy.cloud.run/button.svg)](https://deploy.cloud.run)

## License

This project is released under the MIT License. For more information, see the [LICENSE](LICENSE) file.
