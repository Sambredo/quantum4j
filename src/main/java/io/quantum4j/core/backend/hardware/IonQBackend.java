package io.quantum4j.core.backend.hardware;

import io.quantum4j.core.backend.BackendType;
import io.quantum4j.core.backend.Result;
import io.quantum4j.core.backend.RunOptions;
import io.quantum4j.core.backend.hardware.http.HardwareBackendHttpClient;
import io.quantum4j.core.circuit.QuantumCircuit;
import io.quantum4j.qasm.QasmExporter;

import java.util.HashMap;
import java.util.Map;

/**
 * IonQ hardware backend using OpenQASM 2.0 submission.
 */
public final class IonQBackend implements HardwareBackend {
    private static final String JOB_URL = "https://api.ionq.co/v0.3/jobs";
    private final String apiKey;

    public IonQBackend(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalArgumentException("apiKey must not be null/blank");
        }
        this.apiKey = apiKey;
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
        String jobId = parseField(createResp, "id");
        String status = parseField(createResp, "status");
        String pollResp = createResp;
        int attempts = 0;
        while (!"completed".equalsIgnoreCase(status) && attempts < 5) {
            attempts++;
            pollResp = HardwareBackendHttpClient.postJson(JOB_URL + "/" + jobId, payload, headers);
            status = parseField(pollResp, "status");
        }
        if (!"completed".equalsIgnoreCase(status)) {
            throw new RuntimeException("IonQ job did not complete; last status=" + status);
        }
        return parseVendorResult(pollResp);
    }

    private String quote(String s) {
        return "\"" + s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }

    private Map<String, Integer> extractCounts(String json) {
        Map<String, Integer> counts = new HashMap<>();
        int idx = json.indexOf("\"results\"");
        if (idx < 0) {
            return counts;
        }
        int brace = json.indexOf('{', idx);
        int end = json.indexOf('}', brace);
        if (brace < 0 || end < 0) {
            return counts;
        }
        String body = json.substring(brace + 1, end);
        String[] entries = body.split(",");
        for (String e : entries) {
            String[] kv = e.split(":");
            if (kv.length == 2) {
                String key = kv[0].trim().replace("\"", "");
                try {
                    int val = (int) Double.parseDouble(kv[1].trim());
                    counts.put(key, val);
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return counts;
    }

    private String parseField(String json, String field) {
        String needle = "\"" + field + "\"";
        int idx = json.indexOf(needle);
        if (idx < 0) return "";
        int colon = json.indexOf(':', idx);
        int startQuote = json.indexOf('"', colon + 1);
        int endQuote = json.indexOf('"', startQuote + 1);
        if (startQuote < 0 || endQuote < 0) {
            return "";
        }
        return json.substring(startQuote + 1, endQuote);
    }
}
