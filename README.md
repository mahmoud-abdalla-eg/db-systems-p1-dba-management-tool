# Database Systems - Project One: DBA Management Tool

An interactive Java application built with JDBC to connect, access, update, and manage relational database systems (MySQL), designed specifically for Database Administrators (DBAs).

---

## 📌 Features

1. **Relational Database Connectivity (JDBC)**:
   * Dynamic connection configuration with custom URL, host, port, credentials, and safe defaults.
2. **Metadata Inspection (`DatabaseMetaData`)**:
   * Inspect database product name, version, and JDBC driver specifications.
   * Enumerate all tables and views in the database catalog.
   * Deep table schema inspection: column names, SQL types, column sizes, nullability, primary key constraints, foreign key references (`importedKeys`), and indexes.
3. **Dynamic Result Set Formatting (`ResultSetMetaData`)**:
   * Accepts arbitrary `SELECT` queries from the DBA.
   * Inspects result structure dynamically and outputs data in clean, bordered ASCII text tables.
4. **Data & Schema Updates (DML / DDL)**:
   * Execute `INSERT`, `UPDATE`, `DELETE`, `CREATE TABLE`, `DROP`, and `ALTER` statements with execution time and affected row counts.
5. **Atomic Multi-Statement Transactions**:
   * Interactive transaction buffering (`COMMIT` / `ROLLBACK`).
   * Guarantees `ACID` compliance by rolling back changes on any failure.
6. **Script Execution**:
   * Initialize and execute complete `.sql` script files (such as `sql/init_sample_db.sql`).

---

## 📂 Project Structure

```text
Proj 1/
├── bin/                          # Compiled Java bytecode (.class files)
├── lib/
│   └── mysql-connector-j-9.5.0.jar # MySQL JDBC Driver
├── sql/
│   └── init_sample_db.sql        # Sample schema (departments, employees, audit_logs)
├── src/
│   └── com/dbs/dba/
│       ├── DBManager.java        # JDBC connection, query execution, transactions
│       ├── MetadataExplorer.java # DatabaseMetaData schema inspection
│       ├── TableFormatter.java   # ResultSetMetaData dynamic ASCII table printing
│       └── DBAToolApp.java       # Interactive DBA console application
├── .vscode/
│   └── settings.json             # VS Code Java classpath configuration
├── run.bat                       # One-click Windows CMD compile & run script
├── run.ps1                       # One-click PowerShell compile & run script
├── README.md                     # Project overview and quick start
├── CST_DBS_P1_MahmoudAhmed.docx  # Word document project report (with screenshot placeholders)
└── CST_DBS_P1_MahmoudAhmed.pdf   # Academic project report (PDF)
```

---

## 🚀 Quick Start Guide

### Prerequisites
* Java JDK 17 or higher (tested up to JDK 26)
* Running MySQL instance (e.g. localhost:3306) or custom database host

### Run via Scripts
* **PowerShell**:
  ```powershell
  .\run.ps1
  ```
* **Command Prompt (CMD)**:
  ```cmd
  run.bat
  ```

### Manual Compilation and Execution
```bash
# 1. Compile
javac -encoding UTF-8 -cp "lib/*" -d bin src/com/dbs/dba/*.java

# 2. Run
java -cp "bin;lib/*" com.dbs.dba.DBAToolApp
```

---

## 📝 Submission Checklist
- [x] Java program connects, queries, and updates DB with JDBC.
- [x] Operates on both Data and Meta-data.
- [x] Works as a management tool for DBAs with interactive SQL input.
- [x] Formats SQL results in clean text tables.
- [x] Full Academic Project Report in `CST_DBS_P1_MahmoudAhmed.docx` and `CST_DBS_P1_MahmoudAhmed.pdf`.
