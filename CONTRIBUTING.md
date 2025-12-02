# Contributing to Quantum4J

Thanks for helping improve Quantum4J. This project aims to be the JVM-native, Qiskit-inspired quantum SDK with strong engineering discipline. Please follow the guidelines below to keep the codebase consistent and reliable.

## Ground Rules
- Keep qubit order invariant: local matrix order must match the parameter order; never reorder qubits internally.
- Prefer immutability for matrices; avoid hidden state.
- Maintain test coverage for new features and bug fixes.
- Follow the Google/IntelliJ Java style guide; use 4-space indentation, no tabs.
- All contributions must be original work by the contributor; do not submit code you do not have rights to contribute.
- By contributing, you agree that your contributions are licensed under the Apache License, Version 2.0.

## Getting Started
1) Fork and clone the repo.  
2) Ensure Java 17+ and Maven are installed.  
3) Build and test: `mvn clean test`.  
4) Run the full suite before opening a PR.

## Development Workflow
- Create a feature branch from `main` or the active feature branch.
- Keep commits scoped and descriptive.
- Add or update Javadoc for public APIs; doclint is enforced.
- Update `README.md` and `CHANGELOG.md` when user-facing changes occur.

## Testing
-- Unit tests live under `src/test/java/com/quantum4j/tests`.
- Add focused tests for new gates, passes, import/export paths, and regressions.
- For new transpiler passes, include both positive and negative/edge test cases.

## Submitting Changes
- Open a Pull Request with:
  - A clear summary of the change and rationale.
  - Test evidence (`mvn test` output or equivalent).
  - Notes on any API or behavior changes.
- Link related issues if they exist.

## Reporting Issues
- Use the issue templates (bug report or feature request).
- Provide minimal repro steps, expected vs actual behavior, and environment details.

## Code of Conduct
By participating, you agree to abide by the Code of Conduct (see `CODE_OF_CONDUCT.md`).

## Security
If you believe you have found a security issue, please follow `SECURITY.md` and do **not** open a public issue with sensitive details.


