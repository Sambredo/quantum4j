package com.quantum4j.core.circuit;

import com.quantum4j.core.backend.Backend;
import com.quantum4j.core.backend.BackendFactory;
import com.quantum4j.core.backend.RunOptions;
import com.quantum4j.core.gates.Gate;
import com.quantum4j.core.gates.StandardGates;
import com.quantum4j.visualization.CircuitAsciiRenderer;
import com.quantum4j.visualization.CircuitSvgRenderer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class QuantumCircuit {
    private final int numQubits;
    private final List<Instruction> instructions = new ArrayList<>();

    private QuantumCircuit(int numQubits) {
        this.numQubits = numQubits;
    }

    public static QuantumCircuit create(int numQubits) {
        return new QuantumCircuit(numQubits);
    }

    public int getNumQubits() {
        return numQubits;
    }

    public List<Instruction> getInstructions() {
        return Collections.unmodifiableList(instructions);
    }

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
        instructions.add(Instruction.gate(new StandardGates.CNOTGate(), control, target));
        return this;
    }

    public QuantumCircuit cz(int control, int target) {
        instructions.add(Instruction.gate(new StandardGates.CZGate(), control, target));
        return this;
    }

    public QuantumCircuit swap(int q0, int q1) {
        instructions.add(Instruction.gate(new StandardGates.SWAPGate(), q0, q1));
        return this;
    }

    public QuantumCircuit iswap(int q0, int q1) {
        instructions.add(Instruction.gate(new StandardGates.ISWAPGate(), q0, q1));
        return this;
    }

    public QuantumCircuit ch(int control, int target) {
        instructions.add(Instruction.gate(new StandardGates.CHGate(), control, target));
        return this;
    }

    // ----------------------------------------------------------------------
    // 3-qubit shortcuts
    // ----------------------------------------------------------------------

    public QuantumCircuit ccx(int control1, int control2, int target) {
        instructions.add(Instruction.gate(new StandardGates.CCXGate(), control1, control2, target));
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

    public QuantumCircuit addInstruction(Instruction instruction) {
        instructions.add(instruction);
        return this;
    }

    public QuantumCircuit u3(int qubit, double theta, double phi, double lambda) {
        instructions.add(Instruction.gate(new StandardGates.U3Gate(theta, phi, lambda), qubit));
        return this;
    }

    public QuantumCircuit u2(int qubit, double phi, double lambda) {
        instructions.add(Instruction.gate(new StandardGates.U2Gate(phi, lambda), qubit));
        return this;
    }

    public QuantumCircuit u1(int qubit, double lambda) {
        instructions.add(Instruction.gate(new StandardGates.U1Gate(lambda), qubit));
        return this;
    }

    /**
     * Execute this circuit with the given options using the configured backend.
     *
     * @param options execution options
     * @return result of execution
     */
    public com.quantum4j.core.backend.Result run(RunOptions options) {
        Backend backend = BackendFactory.get(options.getBackendType());
        if (backend == null) {
            throw new IllegalStateException("No backend registered for type " + options.getBackendType());
        }
        return backend.run(this, options);
    }

    /**
     * Render this circuit as ASCII art.
     *
     * @return ASCII diagram
     */
    public String drawAscii() {
        return CircuitAsciiRenderer.render(this);
    }

    /**
     * Render this circuit as SVG.
     *
     * @return SVG string
     */
    public String toSvg() {
        return CircuitSvgRenderer.render(this);
    }
}


