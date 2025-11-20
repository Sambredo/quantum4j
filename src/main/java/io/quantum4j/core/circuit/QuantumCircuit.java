package io.quantum4j.core.circuit;

import io.quantum4j.core.gates.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a quantum circuit with qubits and gate instructions.
 * <p>
 * A QuantumCircuit is constructed with a fixed number of qubits and allows adding gate operations via a fluent builder
 * API. Instructions are stored in execution order and can be retrieved for simulation or export.
 * </p>
 */
public final class QuantumCircuit {
    private final int numQubits;
    private final List<Instruction> instructions = new ArrayList<>();

    private QuantumCircuit(int numQubits) {
        this.numQubits = numQubits;
    }

    /**
     * Create a new quantum circuit with the specified number of qubits.
     *
     * @param numQubits
     *            the number of qubits (≥ 1)
     *
     * @return a new empty QuantumCircuit
     */
    public static QuantumCircuit create(int numQubits) {
        return new QuantumCircuit(numQubits);
    }

    /**
     * Get the number of qubits in this circuit.
     *
     * @return the qubit count
     */
    public int getNumQubits() {
        return numQubits;
    }

    /**
     * Get all instructions in this circuit in execution order.
     *
     * @return immutable list of instructions
     */
    public List<Instruction> getInstructions() {
        return Collections.unmodifiableList(instructions);
    }

    /**
     * Apply a generic gate to specified qubits.
     *
     * @param gate
     *            the gate to apply
     * @param qubits
     *            target qubit indices
     *
     * @return this circuit for method chaining
     */
    public QuantumCircuit apply(Gate gate, int... qubits) {
        instructions.add(Instruction.gate(gate, qubits));
        return this;
    }

    // ----------------------------------------------------------------------
    // 1-qubit shortcuts
    // ----------------------------------------------------------------------

    public QuantumCircuit h(int qubit) {
        instructions.add(Instruction.gate(new StandardGates.HGate(), qubit));
        return this;
    }

    public QuantumCircuit x(int qubit) {
        instructions.add(Instruction.gate(new StandardGates.XGate(), qubit));
        return this;
    }

    public QuantumCircuit y(int qubit) {
        instructions.add(Instruction.gate(new StandardGates.YGate(), qubit));
        return this;
    }

    public QuantumCircuit z(int qubit) {
        instructions.add(Instruction.gate(new StandardGates.ZGate(), qubit));
        return this;
    }

    public QuantumCircuit s(int qubit) {
        instructions.add(Instruction.gate(new StandardGates.SGate(), qubit));
        return this;
    }

    public QuantumCircuit t(int qubit) {
        instructions.add(Instruction.gate(new StandardGates.TGate(), qubit));
        return this;
    }

    public QuantumCircuit rx(int qubit, double theta) {
        instructions.add(Instruction.gate(new StandardGates.RXGate(theta), qubit));
        return this;
    }

    public QuantumCircuit ry(int qubit, double theta) {
        instructions.add(Instruction.gate(new StandardGates.RYGate(theta), qubit));
        return this;
    }

    public QuantumCircuit rz(int qubit, double theta) {
        instructions.add(Instruction.gate(new StandardGates.RZGate(theta), qubit));
        return this;
    }

    // ----------------------------------------------------------------------
    // 2-qubit shortcuts
    // ----------------------------------------------------------------------

    public QuantumCircuit cx(int control, int target) {
        instructions.add(Instruction.gate(new CNOTGate(), control, target));
        return this;
    }

    public QuantumCircuit cz(int control, int target) {
        instructions.add(Instruction.gate(new CZGate(), control, target));
        return this;
    }

    public QuantumCircuit swap(int q0, int q1) {
        instructions.add(Instruction.gate(new SWAPGate(), q0, q1));
        return this;
    }

    public QuantumCircuit iswap(int q0, int q1) {
        instructions.add(Instruction.gate(new ISWAPGate(), q0, q1));
        return this;
    }

    public QuantumCircuit ch(int control, int target) {
        instructions.add(Instruction.gate(new CHGate(), control, target));
        return this;
    }

    // ----------------------------------------------------------------------
    // Measurement API
    // ----------------------------------------------------------------------

    public QuantumCircuit measure(int qubit, int classicalBit) {
        instructions.add(Instruction.measure(qubit, classicalBit));
        return this;
    }

    public QuantumCircuit measureAll() {
        for (int q = 0; q < numQubits; q++) {
            instructions.add(Instruction.measure(q, q));
        }
        return this;
    }

    public QuantumCircuit ccx(int control1, int control2, int target) {
        instructions.add(Instruction.gate(new CCXGate(), control1, control2, target));
        return this;
    }

    // TODO: measure API later
}
