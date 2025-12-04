# Quantum4J Examples

Unified, runnable examples demonstrating end-to-end usage of Quantum4J (Apache 2.0).

## Prerequisites
- Java 17+
- Maven

## How to run
From project root:
```
mvn -q clean install   # one-time build/verify
mvn -q -Dexec.mainClass=com.quantum4j.examples.BellStateExample exec:java
```
Replace the main class with any example below.

## Directory structure
- `src/main/java/com/quantum4j/examples/` — canonical examples
- `src/main/java/com/quantum4j/examples/qasm/` — QASM-focused demos

---
## Quick reference (grouped)
- Basics: `BellStateExample`, `GHZExample`, `TeleportationExample`
- Algorithms: `DeutschAlgorithmExample`, `GroverExample`, `QFTExample`
- QASM: `QasmRoundTripExample`, qasm/* demos
- Transpiler: `TranspilerPipelineExample`
- Hardware: `GroverHardwareExample`
- Visualization: `CircuitDrawingExample`, `VisualizationGoldenSuite`

---
## Examples with snippets & expected output

### BellStateExample
Run:
```
mvn -q -Dexec.mainClass=com.quantum4j.examples.BellStateExample exec:java
```
Snippet:
```java
QuantumCircuit qc = QuantumCircuit.create(2)
    .h(0).cx(0,1).measureAll();
Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(1000));
System.out.println(r.getCounts());
```
Expected counts (approx): `{00≈500, 11≈500}`

### GHZExample
Run:
```
mvn -q -Dexec.mainClass=com.quantum4j.examples.GHZExample exec:java
```
Key idea: H on q0, CX to q1 and q2, measure.  
Snippet:
```java
QuantumCircuit qc = QuantumCircuit.create(3)
    .h(0).cx(0,1).cx(1,2).measureAll();
Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(1000));
System.out.println(r.getCounts());
```
Expected counts: `{000≈500, 111≈500}`

### TeleportationExample
Run:
```
mvn -q -Dexec.mainClass=com.quantum4j.examples.TeleportationExample exec:java
```
Key idea: prepare |ψ> on q0, entangle q1–q2, Bell measurement on q0/q1, apply corrections.  
Snippet:
```java
QuantumCircuit qc = QuantumCircuit.create(3)
    .h(0).rz(0, Math.PI/3).h(1)
    .cx(1,2).cx(0,1).h(0)
    .measure(0,0).measure(1,1)
    .cx(1,2).cz(0,2).measure(2,2);
Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(1000));
System.out.println(r.getCounts());
```
Expected: target qubit ends in the input state (counts roughly balanced for |+| input in demo).

### DeutschAlgorithmExample
Run:
```
mvn -q -Dexec.mainClass=com.quantum4j.examples.DeutschAlgorithmExample exec:java
```
Snippet:
```java
DeutschAlgorithmExample constant = DeutschAlgorithmExample.constantOracle();
constant.run();
DeutschAlgorithmExample balanced = DeutschAlgorithmExample.balancedOracle();
balanced.run();
```
Expected: constant oracle → `{0=1000}`; balanced oracle → `{1=1000}`.

### GroverExample (2-qubit)
Run:
```
mvn -q -Dexec.mainClass=com.quantum4j.examples.GroverExample exec:java
```
Oracle marks |11>; one iteration + diffusion.  
Snippet:
```java
QuantumCircuit qc = QuantumCircuit.create(2)
    .h(0).h(1)
    .cz(0,1)        // oracle
    .h(0).h(1).x(0).x(1)
    .cz(0,1)        // diffusion
    .x(0).x(1).h(0).h(1)
    .measureAll();
Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(1000));
System.out.println(r.getCounts());
```
Expected counts: |11> dominates (~85–95%).

### GroverHardwareExample (IonQ)
Run (requires `IONQ_API_KEY` env var):
```
mvn -q -Dexec.mainClass=com.quantum4j.examples.GroverHardwareExample exec:java
```
If key is missing, fails fast. Results depend on hardware noise/queue.

### QFTExample
Run:
```
mvn -q -Dexec.mainClass=com.quantum4j.examples.QFTExample exec:java
```
Applies QFT(3) to a few basis states; prints amplitudes/counts.  
Snippet:
```java
QuantumCircuit qc = QFT.qft(3);
Result r = qc.run(RunOptions.withBackend(BackendType.STATEVECTOR).withShots(0));
System.out.println(qc.drawAscii());
```
Expected: uniform magnitudes 1/√8 with phase structure per input.

### QasmRoundTripExample
Run:
```
mvn -q -Dexec.mainClass=com.quantum4j.examples.QasmRoundTripExample exec:java
```
Exports → imports → compares instructions.  
Snippet:
```java
QuantumCircuit qc = QuantumCircuit.create(2).h(0).cx(0,1).measureAll();
String qasm = QasmExporter.toQasm(qc);
QuantumCircuit rt = QasmImporter.fromQasm(qasm);
System.out.println("Round-trip successful: " + qc.getInstructions().equals(rt.getInstructions()));
```
Expected: prints `Round-trip successful: true`.

### TranspilerPipelineExample
Run:
```
mvn -q -Dexec.mainClass=com.quantum4j.examples.TranspilerPipelineExample exec:java
```
Applies SwapDecomposition → CXCancellation → U3Decomposition → RotationFusion.  
Snippet:
```java
QuantumCircuit qc = QuantumCircuit.create(2).swap(0,1).cx(0,1).u3(0,0.1,0.2,0.3);
QuantumCircuit out = DefaultTranspiler.transpile(qc);
out.getInstructions().forEach(System.out::println);
```
Expected: before/after instruction lists showing decomposed/cancelled gates.

### Visualization (ASCII/SVG)
- `CircuitDrawingExample`: prints ASCII and writes SVG for a Bell circuit.  
- `VisualizationGoldenSuite`: regenerates ASCII goldens and SVGs for reference circuits (writes to examples-out/).

---
## Key APIs used
- `QuantumCircuit` fluent builder for gates and measurements.
- `StandardGates` gate implementations (H, X, CX, CZ, RZ, etc.).
- `RunOptions.withBackend(...)` to choose execution backend (STATEVECTOR or HARDWARE).
- `BackendFactory.register(...)` to supply hardware backends (e.g., IonQ).
- `Result.getCounts()` for measurement histograms.
- `QasmExporter` / `QasmImporter` for OpenQASM 2.0 round-trip.
- `PassManager` and passes (`SwapDecompositionPass`, `RotationFusionPass`, `CXCancellationPass`, `U3DecompositionPass`) for transpilation.
- `QFT` utility for Fourier transforms.


