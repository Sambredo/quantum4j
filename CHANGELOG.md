# Changelog
All notable changes to this project will be documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

---

## [0.1.0] - 2025-01-01
### Added
- **Statevector Simulation Engine**
  - Supports arbitrary `n` qubits (2ⁿ state vector).
  - High-precision complex number arithmetic.
  - Deterministic and sampled measurements (measureOne, measureAll).

### Circuit & Instruction Model
- `QuantumCircuit` with fluent API (`h()`, `x()`, `cx()`, `ccx()`, `rz(theta)`, etc.)
- `Instruction` class supporting:
  - GATE operations
  - MEASURE operations
  - Multi-qubit instructions

### Single-Qubit Gates
- X, Y, Z
- H
- S, T
- RX, RY, RZ (parameterized rotations)

### Two-Qubit Gates
- CNOT (CX)
- Controlled-H (CH)
- CZ
- SWAP
- ISWAP (full unitary implementation)

### Three-Qubit Gates
- CCX (Toffoli)

### Measurement API
- `measure(q, classicalBit)`
- `measureAll()`
- Classical register decoding in backend

### Backend
- `StateVectorBackend`
  - Executes circuit on statevector
  - Returns outcome counts with shot sampling

### QASM
- **QASM Exporter**
  - `QasmExporter.toQasm(circuit)`
  - Supports major gates + measurement

### Math
- Complex numbers (add, mul, conjugate, abs²)
- Statevector class with safe copy + collapse logic

### Testing
- JUnit 5 test suite:
  - H gate
  - X gate
  - CNOT entanglement
  - SWAP
  - ISWAP
  - CCX truth table behavior
  - Measurement behaviour

### Tooling
- **GitHub Actions CI**  
  - Build & test on Java 17 + 21  
  - Artifact + test report upload  
  - Caching for fast CI

### Metadata
- MIT License
- `.gitignore` for Maven/IntelliJ/VS Code
- Initial README with usage examples

---

## [Unreleased]
### Planned
- OpenQASM importer
- Density-matrix backend
- Noise models
- Quantum gate decomposition tools
- Benchmark suite
- Circuit visualization
- Bloch sphere visualization tools
- Integration with IBMQ / Braket (future)
