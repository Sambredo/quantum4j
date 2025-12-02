package com.quantum4j.core.backend.hardware;

import com.quantum4j.core.backend.BackendType;
import com.quantum4j.core.backend.Result;
import com.quantum4j.core.backend.RunOptions;
import com.quantum4j.core.backend.hardware.http.HardwareBackendHttpClient;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.qasm.QasmExporter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * IonQ hardware backend using OpenQASM 2.0 submission.
 */
public final class IonQBackend implements HardwareBackend {
    private static final String JOB_URL = "https://api.ionq.co/v0.3/jobs";
    private final String apiKey;
    private final int maxPollAttempts;
    private final long pollSleepMillis;

    public IonQBackend(String apiKey) {
        this(apiKey, 10, 500);
    }

    public IonQBackend(String apiKey, int maxPollAttempts, long pollSleepMillis) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey must not be null/blank");
        }
        this.apiKey = apiKey;
        this.maxPollAttempts = maxPollAttempts;
        this.pollSleepMillis = pollSleepMillis;
    }

    @Override
    public String vendor() {
        return "IonQ";
    }

    @Override
    public String toVendorPayload(String qasm, RunOptions options) {
        int shots = options.getShots();
        return "{\"type\":\"qasm\",\"qasm\":" + quote(qasm) + ",\"shots\":" + shots + "}";
    }

    @Override
    public Result parseVendorResult(String json) {
        Map<String, Integer> counts = extractCounts(json);
        return new Result(counts, BackendType.HARDWARE, null);
    }

    @Override
    public Result run(QuantumCircuit circuit, RunOptions options) {
        String qasm = QasmExporter.toQasm(circuit);
        String payload = toVendorPayload(qasm, options);
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "apiKey " + apiKey);
        String createResp = HardwareBackendHttpClient.postJson(JOB_URL, payload, headers);
        String jobId = parseStringField(createResp, "id");
        String status = parseStringField(createResp, "status");
        String pollResp = createResp;
        int attempts = 0;
        while (!"completed".equalsIgnoreCase(status) && attempts < maxPollAttempts) {
            attempts++;
            sleepQuietly(pollSleepMillis);
            pollResp = HardwareBackendHttpClient.getJson(JOB_URL + "/" + jobId, headers);
            status = parseStringField(pollResp, "status");
            if ("failed".equalsIgnoreCase(status) || "canceled".equalsIgnoreCase(status)) {
                throw new RuntimeException("IonQ job " + jobId + " ended with status=" + status);
            }
        }
        if (!"completed".equalsIgnoreCase(status)) {
            throw new RuntimeException("IonQ job did not complete within attempts; jobId=" + jobId + ", last status=" + status);
        }
        return parseVendorResult(pollResp);
    }

    private String quote(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }

    private Map<String, Integer> extractCounts(String json) {
        Map<String, Integer> counts = new HashMap<>();
        int resultsIdx = json.indexOf("\"results\"");
        if (resultsIdx < 0) {
            return counts;
        }
        int objStart = json.indexOf('{', resultsIdx);
        int objEnd = findMatchingBrace(json, objStart);
        if (objStart < 0 || objEnd < 0) {
            return counts;
        }
        String body = json.substring(objStart + 1, objEnd).trim();
        if (body.isEmpty()) {
            return counts;
        }
        String[] entries = body.split(",");
        for (String e : entries) {
            String[] kv = e.split(":");
            if (kv.length == 2) {
                String key = stripQuotes(kv[0].trim());
                double val;
                try {
                    val = Double.parseDouble(kv[1].trim());
                } catch (NumberFormatException ex) {
                    continue;
                }
                counts.put(key, (int) Math.round(val));
            }
        }
        return counts;
    }

    private String parseStringField(String json, String field) {
        String needle = "\"" + field + "\"";
        int idx = json.indexOf(needle);
        if (idx < 0) return "";
        int colon = json.indexOf(':', idx);
        if (colon < 0) return "";
        int firstQuote = json.indexOf('"', colon + 1);
        int secondQuote = json.indexOf('"', firstQuote + 1);
        if (firstQuote < 0 || secondQuote < 0) return "";
        return json.substring(firstQuote + 1, secondQuote);
    }

    private int findMatchingBrace(String s, int start) {
        int depth = 0;
        for (int i = start; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '{') depth++;
            if (c == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private String stripQuotes(String s) {
        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    private void sleepQuietly(long millis) {
        try {
            TimeUnit.MILLISECONDS.sleep(millis);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}


