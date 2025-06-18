#  Java Calendar App (with SQLite Integration)

A desktop calendar application built with Java Swing that allows users to view monthly calendars and add events to specific dates. Event data is saved using a local SQLite database for persistence.

---

## ✨ Features

- View current and future months
- Add and view events on specific dates
- SQLite-based data storage (no external server needed)
- Data persists across sessions
- Clean, interactive Java Swing GUI

---

## 🛠️ Technologies Used

- Java (JDK 8+)
- Java Swing for GUI
- SQLite (via `sqlite-jdbc` driver)
- JDBC for database connectivity

---

## 🚀 How to Run

### ✅ Prerequisites

- Java 8 or higher installed
- `sqlite-jdbc-3.41.2.1.jar` in the same directory as your app

---

### 🏃 Option 1: Run the JAR

```bash
java -cp ".;sqlite-jdbc-3.41.2.1.jar" -jar CalendarApp.jar
````

> 📝 On Linux/macOS, replace `;` with `:` in the classpath:

```bash
java -cp ".:sqlite-jdbc-3.41.2.1.jar" -jar CalendarApp.jar
```

---

### 🏗️ Option 2: Compile and Run from Source

1. Compile:

   ```bash
   javac -cp ".;sqlite-jdbc-3.41.2.1.jar" CalendarApp.java
   ```

2. Run:

   ```bash
   java -cp ".;sqlite-jdbc-3.41.2.1.jar" CalendarApp
   ```

---

## 🗂️ Project Structure

```
.
├── CalendarApp.java           # Main application file
├── CalendarApp.jar            # Compiled executable (optional)
├── calendar.db                # SQLite database (auto-created)
├── sqlite-jdbc-3.41.2.1.jar   # SQLite JDBC driver (must be present)
└── README.md
```

---

## 📌 Notes

* All event data is stored locally in `calendar.db`
* The app auto-creates the DB and required tables if not present
* Works offline; no internet required


