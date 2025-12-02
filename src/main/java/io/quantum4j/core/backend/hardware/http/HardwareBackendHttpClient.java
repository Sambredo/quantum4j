package com.quantum4j.core.backend.hardware.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Minimal HTTP helper for hardware backends.
 */
public final class HardwareBackendHttpClient {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    @FunctionalInterface
    public interface MockResponder {
        String respond(String method, String url, String body, Map<String, String> headers);
    }

    private static volatile MockResponder mockResponder = null;

    private HardwareBackendHttpClient() {
    }

    /**
     * Install a mock responder (for tests). Pass {@code null} to clear.
     */
    public static void setMockResponder(MockResponder responder) {
        mockResponder = responder;
    }

    /**
     * POST JSON with headers, retrying up to 3 attempts with backoff.
     */
    public static String postJson(String url, String json, Map<String, String> headers) {
        return send("POST", url, json, headers);
    }

    /**
     * GET JSON with headers, retrying up to 3 attempts with backoff.
     */
    public static String getJson(String url, Map<String, String> headers) {
        return send("GET", url, null, headers);
    }

    private static String send(String method, String url, String body, Map<String, String> headers) {
        MockResponder mock = mockResponder;
        if (mock != null) {
            return mock.respond(method, url, body, headers);
        }

        Map<String, String> safeHeaders = headers == null ? new ConcurrentHashMap<>() : headers;
        int attempts = 0;
        long backoff = 100L;
        while (true) {
            attempts++;
            try {
                HttpRequest.Builder builder = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(10));
                for (Map.Entry<String, String> e : safeHeaders.entrySet()) {
                    builder.header(e.getKey(), e.getValue());
                }
                if ("POST".equalsIgnoreCase(method)) {
                    builder.header("Content-Type", "application/json");
                    builder.POST(HttpRequest.BodyPublishers.ofString(body == null ? "" : body));
                } else {
                    builder.GET();
                }
                HttpRequest request = builder.build();
                HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                int code = response.statusCode();
                if (code >= 200 && code < 300) {
                    return response.body();
                }
                throw new IOException("HTTP " + code + ": " + response.body());
            } catch (IOException | InterruptedException ex) {
                if (attempts >= 3) {
                    throw new RuntimeException("HTTP " + method + " failed after retries: " + ex.getMessage(), ex);
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

