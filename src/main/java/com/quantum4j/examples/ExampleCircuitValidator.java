package com.quantum4j.examples;

import com.quantum4j.algorithms.QFT;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.visualization.CircuitAsciiRenderer;
import com.quantum4j.visualization.CircuitSvgRenderer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Builds canonical example circuits, renders ASCII/SVG, writes golden ASCII snapshots, and verifies
 * deterministic ASCII output.
 */
public final class ExampleCircuitValidator {

    private ExampleCircuitValidator() {
    }

    public static void main(String[] args) throws Exception {
        runExample("bell", buildBell());
        runExample("ghz3", buildGHZ());
        runExample("teleport", buildTeleport());
        runExample("grover2", buildGrover());
        runExample("qft3", QFT.qft(3));
        runExample("iqft3", QFT.inverseQft(3));
        runExample("ccx", buildCCX());
        runExample("swap", buildSwap());
        runExample("cz", buildCZ());
    }

    private static QuantumCircuit buildBell() {
        return QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .measureAll();
    }

    private static QuantumCircuit buildGHZ() {
        return QuantumCircuit.create(3)
                .h(0)
                .cx(0, 1)
                .cx(1, 2)
                .measureAll();
    }

    private static QuantumCircuit buildTeleport() {
        // |psi> prepared on q0, entangle q1-q2, Bell measurement on q0/q1, corrections applied unconditionally.
        return QuantumCircuit.create(3)
                .h(0)
                .rz(0, Math.PI / 3)
                .h(1)
                .cx(1, 2)
                .cx(0, 1)
                .h(0)
                .measure(0, 0)
                .measure(1, 1)
                // classical condition omitted in ASCII; apply both corrections directly
                .cx(1, 2)
                .cz(0, 2)
                .measure(2, 2);
    }

    private static QuantumCircuit buildGrover() {
        // Oracle for |11> using CZ, then diffusion.
        return QuantumCircuit.create(2)
                .h(0).h(1)
                .cz(0, 1) // oracle marks |11>
                // diffusion
                .h(0).h(1)
                .x(0).x(1)
                .cz(0, 1)
                .x(0).x(1)
                .h(0).h(1)
                .measureAll();
    }

    private static QuantumCircuit buildCCX() {
        return QuantumCircuit.create(3)
                .ccx(0, 1, 2)
                .measureAll();
    }

    private static QuantumCircuit buildSwap() {
        return QuantumCircuit.create(2)
                .swap(0, 1)
                .measureAll();
    }

    private static QuantumCircuit buildCZ() {
        return QuantumCircuit.create(2)
                .cz(0, 1)
                .measureAll();
    }

    private static void runExample(String name, QuantumCircuit qc) throws Exception {
        // 1) ASCII render twice
        String ascii1 = CircuitAsciiRenderer.render(qc);
        String ascii2 = CircuitAsciiRenderer.render(qc);

        // 2) verify deterministic output
        if (!ascii1.equals(ascii2)) {
            throw new IllegalStateException("Non-deterministic ASCII for: " + name);
        }

        // 3) Write ASCII golden file
        Path out = Paths.get("src/test/resources/ascii-golden/examples/" + name + ".txt");
        Files.createDirectories(out.getParent());
        Files.writeString(out, ascii1);

        // 4) Write SVG
        Path svg = Paths.get("examples-out/" + name + ".svg");
        Files.createDirectories(svg.getParent());
        Files.writeString(svg, CircuitSvgRenderer.render(qc));

        // 5) Print output
        System.out.println("== " + name.toUpperCase() + " ==");
        System.out.println(ascii1);
        System.out.println("OK\n");
    }
}
