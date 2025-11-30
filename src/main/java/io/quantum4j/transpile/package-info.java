/**
 * ===============================================================
 *  Quantum4J Transpiler Architecture – IMPORTANT RULES
 * ===============================================================
 *
 *  ALL TranspilerPass implementations MUST follow these rules:
 *
 *  1. INSTRUCTION IMMUTABILITY
 *     Instruction objects must NEVER be reused or mutated.
 *     Always call `instruction.copy()` when forwarding an instruction.
 *
 *  2. PURE FUNCTIONAL PASSES
 *     A pass must NOT modify the original QuantumCircuit instance.
 *     Always create a fresh QuantumCircuit(circuit.getNumQubits()).
 *
 *  3. ORDER PRESERVATION
 *     Passes must preserve the order of instructions unless
 *     the pass's purpose is reordering (e.g., scheduling).
 *
 *  4. GATE ARITY IS FINAL
 *     Gate arity must not be inferred — always use gate.arity().
 *
 *  5. IMMUTABLE GATE INSTANCES
 *     Do NOT mutate gate objects (some passes reuse gates).
 *
 *  6. PASS-CHAIN SAFETY
 *     All passes must be safe to run with:
 *        - CX → CZ → SWAP → CCX decompositions
 *        - Optimization passes
 *        - QASM round-tripping
 *
 *  Violating these rules breaks:
 *      - multi-pass correctness
 *      - QASM round-trip fidelity
 *      - commutation + scheduling passes
 *      - backend simulator correctness
 *
 *  Include this comment block BEFORE generating any new passes.
 */
package io.quantum4j.transpile;
