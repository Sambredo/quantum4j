package com.quantum4j.core.circuit;

import com.quantum4j.core.gates.Gate;

/**
 * Represents a single instruction in a quantum circuit.
 * <p>
 * An Instruction can be either a gate operation or a measurement. Gate instructions include the gate and target qubit
 * indices. Measurement instructions include qubit and classical bit indices.
 * </p>
 */
public final class Instruction {
    /**
     * Type of instruction: GATE or MEASURE.
     */
    public enum Type {
        /** A quantum gate operation */
        GATE,
        /** A measurement operation */
        MEASURE
    }

    private final Type type;
    private final Gate gate; // for GATE
    private final int[] qubits; // target qubits (1 element for measure)
    private final int[] classicalBits; // classical output indices (1 element)

    private Instruction(Type type, Gate gate, int[] qubits, int[] classicalBits) {
        this.type = type;
        this.gate = gate;
        this.qubits = qubits;
        this.classicalBits = classicalBits;
    }

    /**
     * Create a gate instruction.
     *
     * @param gate
     *            the gate to apply
     * @param qubits
     *            target qubit indices
     *
     * @return a new gate Instruction
     */
    public static Instruction gate(Gate gate, int... qubits) {
        return new Instruction(Type.GATE, gate, qubits, null);
    }

    /**
     * Create a measurement instruction.
     *
     * @param qubit
     *            the qubit to measure
     * @param classicalBit
     *            the classical bit index to store result
     *
     * @return a new measurement Instruction
     */
    public static Instruction measure(int qubit, int classicalBit) {
        return new Instruction(Type.MEASURE, null, new int[] { qubit }, new int[] { classicalBit });
    }

    /**
     * Get the type of this instruction.
     *
     * @return GATE or MEASURE
     */
    public Type getType() {
        return type;
    }

    /**
     * Get the gate (only for GATE type instructions).
     *
     * @return the gate, or null if this is a MEASURE
     */
    public Gate getGate() {
        return gate;
    }

    /**
     * Get the target qubits.
     *
     * @return array of qubit indices
     */
    public int[] getQubits() {
        return qubits;
    }

    /**
     * Get the classical bit indices (only for MEASURE type instructions).
     *
     * @return array of classical bit indices
     */
    public int[] getClassicalBits() {
        return classicalBits;
    }

    /**
     * Create a deep copy of this instruction. Gate instances are treated as immutable and are shared; qubit/classical
     * index arrays are cloned to avoid aliasing.
     *
     * @return a new Instruction with the same contents
     */
    public Instruction copy() {
        int[] qCopy = (qubits != null) ? qubits.clone() : null;
        int[] cCopy = (classicalBits != null) ? classicalBits.clone() : null;
        return new Instruction(type, gate, qCopy, cCopy);
    }
}


