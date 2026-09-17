# SpendWise — Personal Finance & Subscription Manager

A console-based Java application that helps a user record income and
expenses, set category budgets, track recurring subscriptions, and
generate simple financial reports — all from a terminal menu, backed
by plain CSV files (no database, no external services).

---

## 1. Overview

People often track expenses in one place, subscriptions in another
(or nowhere at all), and rarely stop to compare either against a
budget. **SpendWise** brings all three into one lightweight tool:

- Record every income/expense transaction with a category and date.
- Set a monthly spending limit per category and get warned at 80%
  usage, alerted if it's exceeded.
- Track recurring subscriptions (Netflix, gym, SaaS tools, etc.) in
  any billing cycle and see their true monthly/yearly cost.
- Generate reports: spending by category, a combined activity feed,
  and an overall monthly financial outlook.

---

## 2. Features

### Module 1 — Transaction Management
- Add income / add expense (amount, category, date, description)
- View all transactions (sorted by date)
- Search by keyword (ID, category, or description)
- Filter by category
- Sort by amount (highest first)
- Delete a transaction by ID
- View total income, total expense, and net balance

### Module 2 — Budget Management
- Set a monthly budget limit per category
- View all budgets
- Check utilization for one category or all categories at once
- Automatic **warning at ≥ 80%** usage and **alert when exceeded**
- Delete a budget

### Module 3 — Subscription Management
- Add a subscription with a billing cycle (Weekly / Monthly /
  Quarterly / Yearly)
- View all subscriptions or only active ones
- Search subscriptions by name/category
- Cancel (soft-stop) or permanently delete a subscription
- View total monthly and yearly subscription cost (all cycles are
  normalised to a common monthly figure for comparison)

### Module 4 — Financial Reports
- Spending-by-category breakdown, highest first
- Combined activity feed — transactions and subscriptions merged
  into one list and printed polymorphically via a common
  `Reportable` interface
- Monthly financial outlook: income − expenses − recurring
  subscription cost
- Budget status summary across every category at once

---

## 3. Technologies / Tools Used

| Area | Choice |
|---|---|
| Language | Java 17+ (tested on JDK 21) |
| Paradigm | Core Java, Object-Oriented Programming |
| Data storage | Plain CSV files (`java.io` / `BufferedReader`/`BufferedWriter`) |
| Collections | `ArrayList`, `HashMap` / `LinkedHashMap` |
| Interface | Terminal / CLI (`java.util.Scanner`) |
| Build | Plain `javac` — no Maven/Gradle required |
| Diagrams | Graphviz + Matplotlib (see `docs/diagrams/`) |

---

## 4. Project Structure

```
SpendWise/
├── src/main/java/com/spendwise/
│   ├── Main.java                     # CLI entry point (menus only, no business logic)
│   ├── model/
│   │   ├── FinancialRecord.java      # abstract base class
│   │   ├── Reportable.java           # interface
│   │   ├── Transaction.java
│   │   ├── Subscription.java
│   │   ├── Budget.java
│   │   ├── TransactionType.java      # enum
│   │   └── BillingCycle.java         # enum
│   ├── service/
│   │   ├── TransactionService.java   # Module 1
│   │   ├── BudgetService.java        # Module 2
│   │   ├── SubscriptionService.java  # Module 3
│   │   └── ReportService.java        # Module 4
│   ├── storage/
│   │   ├── CSVFileHandler.java       # generic CSV read/write
│   │   ├── TransactionStorage.java
│   │   ├── BudgetStorage.java
│   │   └── SubscriptionStorage.java
│   ├── util/
│   │   ├── InputValidator.java
│   │   └── IdGenerator.java
│   └── exception/
│       ├── InvalidInputException.java
│       ├── RecordNotFoundException.java
│       └── DuplicateIdException.java
├── data/                             # CSV data files (created automatically)
│   ├── transactions.csv
│   ├── budgets.csv
│   └── subscriptions.csv
├── docs/
│   └── screenshots/
├── README.md
└── statement.md
```

---

## 5. Steps to Install & Run

**Requirements:** JDK 17 or newer (no other dependencies).

```bash
# 1. Clone the repository
git clone <https://github.com/bhoomikakushwaha07-cloud/SpendWise.git>
cd SpendWise

# 2. Create a bin folder
mkdir bin

# 3. Compile
javac -d bin src\main\java\com\spendwise\Main.java src\main\java\com\spendwise\model\*.java src\main\java\com\spendwise\service\*.java src\main\java\com\spendwise\storage\*.java src\main\java\com\spendwise\util\*.java src\main\java\com\spendwise\exception\*.java

# 4. Run
java -cp bin com.spendwise.Main
```
---

## 6. Instructions for Testing

There is no separate automated test framework, but the application was verified with
a full manual test pass covering every menu option for the exact inputs, expected results, and actual results.

To run the scenario yourself:

```bash
javac -d bin $(find src -name "*.java")
java -cp bin com.spendwise.Main < test-results/sample-session-input.txt
```

This replays: adding income & 8 expenses across different categories,
setting 5 category budgets, checking utilization (including a
triggered warning), adding 4 subscriptions with different billing
cycles, and viewing every report.

---

## 7. Screenshots

**Budget Management — utilization warnings at ≥ 80%:**

![Budget module](SpendWise/docs/screenshots/Budget_module.png)

**Financial Reports — spending by category & monthly outlook:**

![Reports module](SpendWise/docs/screenshots/Reports_module.png)

---

## 8. Future Enhancements

- Switch `double` to `BigDecimal` for exact currency arithmetic.
- Add JUnit test suite for the service layer.
- Support multiple named budgets per month (currently one active
  limit per category).
- Export reports to CSV/PDF directly from the app.
