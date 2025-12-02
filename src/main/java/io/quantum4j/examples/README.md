# Quantum4J Examples

This package contains canonical, runnable examples that demonstrate Quantum4J fundamentals:

- Circuit construction and measurement
- Simulation with the state-vector backend
- Gate library usage (single, rotation, controlled, three-qubit, U-gates)
- Transpiler passes and the default pipeline
- QASM import/export round-trip
- Classic demos (Bell, GHZ) and basic error-handling patterns

## How to run

From the project root:

```bash
mvn -q exec:java -Dexec.mainClass=io.quantum4j.examples.BellStateDemo
```

Replace the main class with any of the demo class names below.

## Included demos

- `CircuitBasicsDemo` — create circuits, add gates/measurements, print instructions.
- `SimulationBasicsDemo` — run a circuit, collect counts, inspect amplitudes conceptually.
- `GateLibraryDemo` — showcase H/X/Y/Z, RX/RY/RZ, CX/CZ/CH/SWAP/CCX, U1/U2/U3.
- `TranspilerPassesDemo` — run individual passes and show before/after.
- `DefaultTranspilerDemo` — run the default pipeline on a sample circuit.
- `BellStateDemo` — create a Bell pair, simulate, and print counts.
- `GHZStateDemo` — create a GHZ state and measure.
- `QasmRoundTripDemo` — export/import OpenQASM 2.0.
- `ErrorHandlingDemo` — examples of invalid usage caught by the API.

All demos avoid mutating input circuits, rely on builder methods, and print deterministic or expectation-aligned outputs suitable for learning.***

