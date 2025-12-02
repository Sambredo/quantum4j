package com.quantum4j.tests.ascii;

import com.quantum4j.core.circuit.QuantumCircuit;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AsciiGoldenMatchTest {

    @Test
    void bellStateExampleMatchesGolden() throws IOException {
        QuantumCircuit qc = QuantumCircuit.create(2)
                .h(0)
                .cx(0, 1)
                .measureAll();
        String ascii = qc.drawAscii().trim();
        List<String> expected = Files.readAllLines(Path.of("src/test/resources/ascii-golden/BellStateExample.txt"));
        assertEquals(String.join(System.lineSeparator(), expected).trim(), ascii);
    }
}


