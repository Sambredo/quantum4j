package com.quantum4j.tests.ascii;

import com.quantum4j.core.circuit.QuantumCircuit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsciiRendererGateTests {

    @Test
    void cnotLayout() {
        QuantumCircuit qc = QuantumCircuit.create(2).cx(0, 1);
        String ascii = qc.drawAscii();
        assertTrue(ascii.contains("o"));
        assertTrue(ascii.contains("X"));
        assertTrue(ascii.contains("|"));
    }

    @Test
    void czLayout() {
        QuantumCircuit qc = QuantumCircuit.create(2).cz(0, 1);
        String ascii = qc.drawAscii();
        assertTrue(ascii.contains("o"));
        assertTrue(ascii.contains("Z"));
        assertTrue(ascii.contains("|"));
    }

    @Test
    void swapLayout() {
        QuantumCircuit qc = QuantumCircuit.create(2).swap(0, 1);
        String ascii = qc.drawAscii();
        assertTrue(ascii.contains("x"));
        assertTrue(ascii.contains("|"));
    }

    @Test
    void ccxLayout() {
        QuantumCircuit qc = QuantumCircuit.create(3).ccx(0, 1, 2);
        String ascii = qc.drawAscii();
        assertTrue(ascii.contains("o"));
        assertTrue(ascii.contains("X"));
        assertTrue(ascii.contains("|"));
    }
}


