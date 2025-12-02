package com.quantum4j.core.backend;

import com.quantum4j.core.backend.BackendType;

import com.quantum4j.core.circuit.Instruction;
import com.quantum4j.core.circuit.QuantumCircuit;
import com.quantum4j.core.gates.Gate;
import com.quantum4j.core.gates.SingleQubitGate;
import com.quantum4j.core.gates.TwoQubitGate;
import com.quantum4j.core.gates.ThreeQubitGate;
import com.quantum4j.core.math.StateVector;

import java.util.HashMap;
import java.util.Map;

/**
 * State-vector simulator backend for quantum circuits.
 * <p>
 * Simulates quantum circuits using explicit state vectors and matrix multiplication. Each gate is applied as a unitary
 * transformation, and measurements collapse the state. Suitable for small to medium-sized circuits (up to ~20 qubits).
 * </p>
 */
public final class StateVectorBackend implements Backend {

    /**
     * Execute a quantum circuit on this state-vector backend.
     *
     * @param circuit
     *            the circuit to simulate
     * @param options
     *            execution options (number of shots)
     *
     * @return measurement results aggregated from all shots
     */
    @Override
    public Result run(QuantumCircuit circuit, RunOptions options) {
        int nQubits = circuit.getNumQubits();
        StateVector baseState = new StateVector(nQubits);

        Map<String, Integer> counts = new HashMap<>();
        boolean hasMeasurements = containsMeasurements(circuit);
        StateVector finalState = null;

        for (int shot = 0; shot < options.getShots(); shot++) {

            StateVector state = baseState.copy();
            int[] classicalRegister = new int[nQubits]; // for measure instructions

            for (Instruction inst : circuit.getInstructions()) {

                switch (inst.getType()) {

                case GATE: {
                    Gate gate = inst.getGate();
                    int[] qs = inst.getQubits();

                    if (gate instanceof SingleQubitGate) {
                        ((SingleQubitGate) gate).apply(state, qs[0]);

                    } else if (gate instanceof TwoQubitGate) {
                        ((TwoQubitGate) gate).apply(state, qs[0], qs[1]);

                    } else if (gate instanceof ThreeQubitGate) {
                        ((ThreeQubitGate) gate).apply(state, qs[0], qs[1], qs[2]);

                    } else {
                        throw new UnsupportedOperationException(
                                "Unsupported gate type: " + gate.getClass().getSimpleName());
                    }
                    break;
                }

                case MEASURE: {
                    int q = inst.getQubits()[0];
                    int c = inst.getClassicalBits()[0];

                    int measuredBit = state.measureOne(q); // you already added this
                    classicalRegister[c] = measuredBit;
                    break;
                }

                default:
                    throw new IllegalStateException("Unknown instruction type: " + inst.getType());
                }
            }

            // If circuit has explicit MEASURE instructions: use classical bits.
            // Otherwise measure all qubits at the end (backward compatibility).
            String outcome;
            if (hasMeasurements) {
                outcome = buildClassicalString(classicalRegister);
            } else {
                outcome = state.measureAll();
            }

            counts.merge(outcome, 1, Integer::sum);
            finalState = state.copy();
        }

        return new Result(counts, BackendType.STATEVECTOR, finalState);
    }

    // --------------------------------------------------------------
    // Helpers
    // --------------------------------------------------------------

    private boolean containsMeasurements(QuantumCircuit circuit) {
        return circuit.getInstructions().stream().anyMatch(i -> i.getType() == Instruction.Type.MEASURE);
    }

    private String buildClassicalString(int[] classicalRegister) {
        StringBuilder sb = new StringBuilder();
        for (int bit : classicalRegister) {
            sb.append(bit);
        }
        return sb.toString();
    }
}

