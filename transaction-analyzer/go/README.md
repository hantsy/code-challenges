# Transaction Analyzer (Go)

Lightweight challenge implementation for parsing transactions from CSV, filtering by merchant and date range, and computing statistics.

Run
-----
From the `go` folder, build or run the program directly:

```bash
cd d:\hantsylabs\code-challenges\transaction-analyzer\go
go run .
```

The program reads `input.csv` from the working directory and prompts for three inputs on stdin:
- `fromDate` — date/time in `dd/MM/yyyy HH:mm:ss` format (example: `20/08/2020 12:00:00`)
- `toDate` — date/time in the same format
- `merchant` — merchant name (example: `Kwik-E-Mart`)

Example run (PowerShell):

```powershell
Set-Content -Path tmp_input.txt -Value "20/08/2020 12:00:00`n20/08/2020 13:00:00`nKwik-E-Mart"
go run . < tmp_input.txt
```

Non-interactive / Debugger runs
--------------------------------
You can run the program non-interactively by creating a `tmp_input.txt` file with the three lines of input and passing it to the program using the `--stdin-file` flag described below. The `.vscode` launch configuration uses a `preLaunchTask` to create `tmp_input.txt` automatically.

```bash
go run . --stdin-file tmp_input.txt
```

When using the VS Code Run/Debug configuration added in `.vscode/launch.json`, the `Run Transaction Analyzer (run - integratedTerminal)` configuration writes the tmp file and then runs the program using the `--stdin-file` flag so the debugger runs non-interactively.

CLI flags
----------
The program supports non-interactive CLI flags, which are preferred over stdin input. Provide all three together:

```bash
go run . --from "20/08/2020 12:00:00" --to "20/08/2020 13:00:00" --merchant "Kwik-E-Mart"
```
Note that all three flags must be provided together if you use them; otherwise the app will prompt interactively for inputs.

Tests
-----
Run the unit tests (parsing, loader and repository):

```bash
cd d:\hantsylabs\code-challenges\transaction-analyzer\go
go test ./... -v
```

Notes
-----
- The tool expects the CSV to use the format in `input.csv`.
- Reversal transactions mark a `Related Transaction` id and exclude the referenced payment from results.
- Date range comparisons are inclusive of both `from` and `to` bounds.

Contributing
-----
- To tidy modules: `go mod tidy`
- Add new unit tests in `main_test.go` and run `go test`.
