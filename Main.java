import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.servlet.http.HttpServlet;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Main extends HttpServlet {
    public static void main(String[] args) {
        String listenAddr = System.getenv("LISTEN_ADDR");
        String addr = listenAddr + ":" + System.getenv("PORT");

        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(Integer.valueOf(addr)), 0);
            server.createContext("/watch", new StreamHandler());
            server.start();
            System.out.printf("starting server at %s%n", addr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class StreamHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String v = getQueryParam(exchange);
            if (v == null || v.isEmpty()) {
                exchange.sendResponseHeaders(400, 0);
                exchange.getResponseBody().write("use format /watch?v=...".getBytes());
                exchange.close();
                return;
            }

            try {
                downloadVideoAndExtractAudio(v, exchange.getResponseBody());
            } catch (Exception e) {
                e.printStackTrace();
                exchange.sendResponseHeaders(500, 0);
                exchange.getResponseBody().write(("stream error: " + e.getMessage()).getBytes());
            } finally {
                exchange.close();
            }
        }

        private String getQueryParam(HttpExchange exchange) {
            URI requestedUri = exchange.getRequestURI();
            String query = requestedUri.getRawQuery();
            Map<String, String> queryParams = parseQuery(query);
            return queryParams.get("v");
        }

        private Map<String, String> parseQuery(String query) {
            Map<String, String> queryParams = new HashMap<>();
            if (query != null && !query.isEmpty()) {
                String[] pairs = query.split("&");
                for (String pair : pairs) {
                    int idx = pair.indexOf("=");
                    queryParams.put(URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8),
                            URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8));
                }
            }
            return queryParams;
        }

        private void downloadVideoAndExtractAudio(String id, OutputStream out) throws IOException, InterruptedException {
            String url = "https://youtube.com/watch?v=" + id;

            ProcessBuilder ytdlBuilder = new ProcessBuilder("yt-dlp", url, "-o-");
            Process ytdl = ytdlBuilder.start();

            ProcessBuilder ffmpegBuilder = new ProcessBuilder("ffmpeg", "-i", "/dev/stdin", "-f", "mp3", "-ab",
                    "96000", "-vn", "-");
            ffmpegBuilder.redirectInput();
            Process ffmpeg = ffmpegBuilder.start();

            try (InputStream ffmpegInputStream = ffmpeg.getInputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = ffmpegInputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            System.out.printf("stream finished%n");
        }
    }
}
