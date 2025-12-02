package com.quantum4j.examples;

import com.quantum4j.core.backend.BackendType;
import com.quantum4j.core.backend.Result;
import com.quantum4j.core.backend.RunOptions;
import com.quantum4j.core.circuit.QuantumCircuit;

/**
 * Showcases common gates: single-qubit, rotations, controlled, and U-gates.
 */
public final class GateLibraryDemo {
    public static void main(String[] args) {
        QuantumCircuit qc = QuantumCircuit.create(3)
                .h(0)
                .x(1)
                .rz(2, Math.PI / 4)
                .cx(0, 1)
                .cz(1, 2)
                .ch(0, 2)
                .swap(0, 2)
                .ccx(0, 1, 2)
                .u1(0, Math.PI / 8)
                .u2(1, Math.PI / 4, Math.PI / 3)
                .u3(2, Math.PI / 3, Math.PI / 5, Math.PI / 7)
                .measureAll();

        Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(200));
        System.out.println("Gate library demo counts: " + r.getCounts());
    }
}


