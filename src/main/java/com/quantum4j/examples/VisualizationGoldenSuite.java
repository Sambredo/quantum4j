package com.quantum4j.examples;

import com.quantum4j.algorithms.QFT;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.visualization.CircuitAsciiRenderer;
import com.quantum4j.visualization.CircuitSvgRenderer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * Generates ASCII (raw + pretty) and SVG diagrams for canonical circuits, compares raw against golden fixtures,
 * and writes outputs to examples-out/.
 */
public final class VisualizationGoldenSuite {

    private VisualizationGoldenSuite() {
    }

    public static void main(String[] args) throws Exception {
        Map<String, QuantumCircuit> circuits = Map.of(
                "bell", buildBell(),
                "ghz3", buildGHZ(),
                "teleport", buildTeleport(),
                "grover2", buildGrover(),
                "qft3", QFT.qft(3),
                "iqft3", QFT.inverseQft(3),
                "ccx", buildCCX(),
                "swap", buildSwap(),
                "cz", buildCZ()
        );

        for (Map.Entry<String, QuantumCircuit> entry : circuits.entrySet()) {
            run(entry.getKey(), entry.getValue());
        }
    }

    private static void run(String name, QuantumCircuit qc) throws Exception {
        String raw = CircuitAsciiRenderer.render(qc, false);
        String pretty = CircuitAsciiRenderer.render(qc, true);

        // deterministic check
        if (!raw.equals(CircuitAsciiRenderer.render(qc, false))) {
            throw new IllegalStateException("Non-deterministic raw ASCII for " + name);
        }

        // compare to golden
        Path golden = Path.of("src/test/resources/ascii-golden/examples/" + name + ".txt");
        if (Files.exists(golden)) {
            String expected = Files.readString(golden);
            if (!expected.equals(raw)) {
                throw new IllegalStateException("Raw ASCII mismatch for " + name);
            }
        }

        // write outputs
        Path outDir = Path.of("examples-out");
        Files.createDirectories(outDir);
        Path rawOut = outDir.resolve(name + "-raw.txt");
        Path prettyOut = outDir.resolve(name + "-pretty.txt");
        Path svgOut = outDir.resolve(name + ".svg");
        Files.writeString(rawOut, raw);
        Files.writeString(prettyOut, pretty);
        CircuitSvgRenderer.writeToFile(qc, svgOut);

        System.out.println("== " + name.toUpperCase() + " ==");
        System.out.println(raw);
        System.out.println(pretty);
        System.out.println("OK\n");
    }

    private static QuantumCircuit buildBell() {
        return QuantumCircuit.create(2).h(0).cx(0, 1).measureAll();
    }

    private static QuantumCircuit buildGHZ() {
        return QuantumCircuit.create(3).h(0).cx(0, 1).cx(1, 2).measureAll();
    }

    private static QuantumCircuit buildTeleport() {
        return QuantumCircuit.create(3)
                .h(0).rz(0, Math.PI / 3)
                .h(1)
                .cx(1, 2)
                .cx(0, 1)
                .h(0)
                .measure(0, 0)
                .measure(1, 1)
                .cx(1, 2)
                .cz(0, 2)
                .measure(2, 2);
    }

    private static QuantumCircuit buildGrover() {
        return QuantumCircuit.create(2)
                .h(0).h(1)
                .cz(0, 1)
                .h(0).h(1)
                .x(0).x(1)
                .cz(0, 1)
                .x(0).x(1)
                .h(0).h(1)
                .measureAll();
    }

    private static QuantumCircuit buildCCX() {
        return QuantumCircuit.create(3).ccx(0, 1, 2).measureAll();
    }

    private static QuantumCircuit buildSwap() {
        return QuantumCircuit.create(2).swap(0, 1).measureAll();
    }

    private static QuantumCircuit buildCZ() {
        return QuantumCircuit.create(2).cz(0, 1).measureAll();
    }
}
