package io.quantum4j.core.backend.hardware.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Minimal HTTP helper for hardware backends.
 */
public final class HardwareBackendHttpClient {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static volatile BiFunction<String, Map<String, String>, String> mockResponder = null;

    private HardwareBackendHttpClient() {
    }

    /**
     * Install a mock responder (for tests). Pass {@code null} to clear.
     */
    public static void setMockResponder(BiFunction<String, Map<String, String>, String> responder) {
        mockResponder = responder;
    }

    /**
     * POST JSON with headers, retrying up to 3 attempts with backoff.
     */
    public static String postJson(String url, String json, Map<String, String> headers) {
        BiFunction<String, Map<String, String>, String> mock = mockResponder;
        if (mock != null) {
            return mock.apply(url, headers);
        }

        Map<String, String> safeHeaders = headers == null ? new ConcurrentHashMap<>() : headers;
        int attempts = 0;
        long backoff = 100L;
        while (true) {
            attempts++;
            try {
                HttpRequest.Builder builder = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(10))
                        .header("Content-Type", "application/json");
                for (Map.Entry<String, String> e : safeHeaders.entrySet()) {
                    builder.header(e.getKey(), e.getValue());
                }
                HttpRequest request = builder.POST(HttpRequest.BodyPublishers.ofString(json)).build();
                HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                int code = response.statusCode();
                if (code >= 200 && code < 300) {
                    return response.body();
                }
                throw new IOException("HTTP " + code + ": " + response.body());
            } catch (IOException | InterruptedException ex) {
                if (attempts >= 3) {
                    throw new RuntimeException("HTTP POST failed after retries: " + ex.getMessage(), ex);
                }
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during backoff", ie);
                }
                backoff *= 2;
            }
        }
    }
}
