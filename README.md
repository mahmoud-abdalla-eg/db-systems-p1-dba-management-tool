# Database Systems - Project 1: DBA Management Tool

An interactive Java console application that connects to MySQL with JDBC and provides basic database administration operations on data and metadata.

## Features

- Connect to a local or custom MySQL database through JDBC.
- Display database and JDBC driver metadata.
- List tables and views in the current catalog.
- Inspect table columns, types, nullability, primary keys, foreign keys, and indexes.
- Run `SELECT` queries and display results as formatted text tables.
- Run `INSERT`, `UPDATE`, `DELETE`, and DDL statements.
- Execute multiple statements in a transaction with `COMMIT` or `ROLLBACK`.
- Execute a SQL initialization script.

## Project Structure

```text
CST_DBS_P1_MahmoudAhmed/
├── lib/
│   └── mysql-connector-j-9.5.0.jar  # MySQL JDBC driver
├── sql/
│   └── init_sample_db.sql            # Sample schema and seed data
├── src/com/dbs/dba/
│   ├── DBAToolApp.java               # Console menu and user input
│   ├── DBManager.java                # JDBC connection and SQL execution
│   ├── MetadataExplorer.java         # DatabaseMetaData inspection
│   └── TableFormatter.java           # ResultSetMetaData table formatting
├── run.bat                           # Compile and run from Command Prompt
├── run.ps1                           # Compile and run from PowerShell
├── .gitignore                        # Excludes reports and generated files
└── README.md                         # Project documentation
```

The `bin/` directory is generated during compilation and is intentionally not tracked by Git. The academic PDF/DOCX report is kept separately from this source-code repository.

## Requirements

- Windows
- Java JDK 17 or newer
- MySQL Server running on `localhost:3306`
- MySQL root credentials, or another user with permission to create and update the database

The MySQL JDBC driver is included in `lib/`, so no Maven or Gradle setup is required.

## Run the Application

Open a terminal in the project folder and run one of these scripts.

PowerShell:

```powershell
.\run.ps1
```

Command Prompt:

```cmd
run.bat
```

The program first asks whether to use the default connection. If the MySQL root account has a password, choose `n`, select option `1`, press Enter to use the default JDBC URL, and enter the root username and password.

Default JDBC URL:

```text
jdbc:mysql://localhost:3306/dbs_proj1?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```

Manual compilation and execution:

```powershell
javac -encoding UTF-8 -cp "lib/*" -d bin src/com/dbs/dba/*.java
java -cp "bin;lib/*" com.dbs.dba.DBAToolApp
```

## Main Menu

```text
1. Connect to database
2. Show database metadata
3. List tables
4. Describe a table
5. Run a query
6. Run an update
7. Run a transaction
8. Run an SQL script
9. Run verification queries
0. Exit
```

To create the sample database objects and seed data, choose option `8` and press Enter when asked for the file path. The script creates the `departments`, `employees`, and `audit_logs` tables plus the `v_department_summary` view.

## Sample Data

The sample employees include:

| Employee | Name |
|---:|---|
| 101 | Amina Li |
| 102 | Omar Chen |
| 103 | Mei Hassan |
| 104 | Yusuf Zhang |
| 105 | Sara Wang |

## Troubleshooting

- **Communications link failure:** Start MySQL Server and confirm that it is listening on port `3306`.
- **No suitable driver found for `1234`:** The password was entered as the JDBC URL. Press Enter at the JDBC URL prompt and enter the password only at the password prompt.
- **Access denied:** Check the MySQL username and password.
- **Script file not found:** Run the program from the project folder so that `sql/init_sample_db.sql` can be found.

## Repository Notes

The `.gitignore` excludes compiled classes, PDF/DOCX reports, ZIP/RAR archives, IDE files, and temporary files. The source code, SQL script, run scripts, README, and JDBC driver remain available in the repository.
