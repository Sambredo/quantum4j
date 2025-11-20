# Changelog

All notable changes to this project will be documented in this file.

The format is inspired by [Keep a Changelog](https://keepachangelog.com/)
and this project adheres to [Semantic Versioning](https://semver.org/).

---

## [0.1.0] - 2025-11-19

### Added
- Initial public release of Quantum4J.
- State-vector simulator backend (`StateVectorBackend`).
- Core circuit model (`QuantumCircuit`, `Instruction`).
- Complex number math and `StateVector` representation.
- Full set of standard 1-qubit gates: X, Y, Z, H, S, T, RX, RY, RZ.
- 2-qubit gates: CX, CZ, SWAP, iSWAP, CH.
- 3-qubit gate: CCX (Toffoli).
- Measurement API: `measure`, `measureAll`.
- OpenQASM 2.0 exporter (`QasmExporter`).
- Example circuits (Bell, GHZ, Toffoli, rotations, SWAP, iSWAP, QASM export).
- JUnit 5 test suite for core gates and measurement behavior.

### Notes
- Designed as a Java-first quantum SDK, Qiskit-inspired, for JVM developers.
