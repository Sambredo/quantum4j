# Quantum4J Examples

Unified, runnable examples demonstrating end-to-end usage of Quantum4J (Apache 2.0).

## Prerequisites
- Java 17+
- Maven

## How to run
From project root:
```
mvn -q -Dexec.mainClass=com.quantum4j.examples.BellStateExample exec:java
```
Replace the main class with any example below.

## Directory structure
- `src/main/java/com/quantum4j/examples/` — canonical examples
- `src/main/java/com/quantum4j/examples/qasm/` — QASM-focused demos (already present)

## Examples (this module)
- `BellStateExample` — Bell pair, statevector, counts, QASM export.
- `GHZExample` — 3-qubit GHZ state amplitudes and counts.
- `TeleportationExample` — coherent teleportation circuit with measurements.
- `DeutschAlgorithmExample` — constant vs balanced oracle, deterministic output.
- `GroverExample` — 2-qubit Grover, probability amplification for |11>.
- `GroverHardwareExample` — submits Grover to a hardware backend (IonQ) if `IONQ_API_KEY` is set; fails fast otherwise.
- `QFTExample` — QFT(3) applied to |001>, |010>, |111>, amplitudes shown.
- `QasmRoundTripExample` — export/import and instruction equality check.
- `TranspilerPipelineExample` — run SwapDecomposition, RotationFusion, CXCancellation, U3Decomposition.

## Expected outputs (approximate)
- Bell: statevector |00> and |11> at ~0.707; counts ~{00:500, 11:500}.
- GHZ: |000> and |111> at ~0.707; counts ~{000:500, 111:500}.
- Teleportation: target qubit in |+>; counts roughly balanced on target bit.
- Deutsch: constant → {0=1000}; balanced → {1=1000}.
- Grover: |11> dominates (~85–95% of shots).
- GroverHardware: hardware counts depend on device noise; requires env var `IONQ_API_KEY`.
- QFT: uniform magnitudes 1/sqrt(8) with phase differences per input.
- Qasm round-trip: prints “Round-trip successful: true”.
- Transpiler pipeline: shows reduced/rewritten instruction list after passes.

## Key APIs used
- `QuantumCircuit` fluent builder for gates and measurements.
- `StandardGates` gate implementations (X, H, CX, CZ, RZ, etc.).
- `RunOptions.withBackend(...)` to choose execution backend (STATEVECTOR or HARDWARE).
- `BackendFactory.register(...)` to supply hardware backends (e.g., IonQ).
- `StateVectorBackend` with `RunOptions.withShots(...)` for sampling.
- `Result.getCounts()` for measurement histograms.
- `QasmExporter` / `QasmImporter` for OpenQASM 2.0 round-trip.
- `PassManager` and passes (`SwapDecompositionPass`, `RotationFusionPass`, `CXCancellationPass`, `U3DecompositionPass`) for transpilation.
- `QFT` utility for Fourier transforms.

