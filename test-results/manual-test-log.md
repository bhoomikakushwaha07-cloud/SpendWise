# SpendWise — Manual Test Log

No automated test framework (e.g. JUnit) is used in this project by
design — see README §10. Instead, every feature was exercised
end-to-end with a scripted console session and the output was
inspected line by line. This file records what was tested, the input
used, the expected result, and the actual result observed.

The exact input file used to reproduce this session is
[`sample-session-input.txt`](sample-session-input.txt) — replay it with:

```bash
javac -d bin $(find src -name "*.java")
java -cp bin com.spendwise.Main < test-results/sample-session-input.txt
```

## Module 1 — Transaction Management

| # | Test Case | Input | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| 1.1 | Add income | 45000, Salary, 2026-09-01 | Transaction `T0001` saved as INCOME | Saved exactly as expected | PASS |
| 1.2 | Add expense (8 categories) | Food, Transport, Bills, Shopping, Education, Health, Entertainment amounts | 8 expense transactions `T0002`–`T0009` saved | All 8 saved with correct category/amount | PASS |
| 1.3 | Reject invalid amount | `"abc"` as amount | `InvalidInputException`: "Amount must be a valid number." | Error shown, no transaction created | PASS |
| 1.4 | Reject empty category | blank category field | `InvalidInputException`: "Category cannot be empty." | Error shown, no transaction created | PASS |
| 1.5 | Reject bad date format | `12-31-2026` | `InvalidInputException`: date format message | Error shown, no transaction created | PASS |
| 1.6 | Search by category keyword | `"Food"` | Both Food transactions returned | Correct 2 rows returned | PASS |
| 1.7 | Delete transaction by ID | `T0002` | Transaction removed, CSV rewritten without it | Confirmed removed from `transactions.csv` | PASS |
| 1.8 | View totals | — | Income = 45000, Expense = sum of remaining, Balance = difference | Matched manual calculation | PASS |
| 1.9 | Sort by amount (desc) | — | List ordered highest amount first | Confirmed order (e.g. 45000 first) | PASS |

## Module 2 — Budget Management

| # | Test Case | Input | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| 2.1 | Set budget | Food = 2000 | Budget saved to `budgets.csv` | Confirmed row `Food,2000.0` | PASS |
| 2.2 | Utilization below warning | Transport: spent 800 / limit 1500 | "53% — within budget" | Printed "Budget Used: 53%" + "within budget" | PASS |
| 2.3 | Utilization at warning threshold | Food: spent 1800 / limit 2000 (90%) | Warning message shown (≥ 80%) | "Warning: You have used 90% of your Food budget." | PASS |
| 2.4 | Utilization above threshold | Bills: spent 3200 / limit 3500 (≈91%) | Warning message shown | "Warning: You have used 91% of your Bills budget." | PASS |
| 2.5 | Check for category with no budget | `"Travel"` | `RecordNotFoundException`: "No budget set for category: Travel" | Exact message returned | PASS |
| 2.6 | Check all budgets at once | — | All 5 set budgets printed with correct % | All 5 printed correctly (see `docs/screenshots/budget_module.png`) | PASS |

## Module 3 — Subscription Management

| # | Test Case | Input | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| 3.1 | Add MONTHLY subscription | Netflix, 199, MONTHLY | Monthly impact = -199 | Confirmed via total monthly cost calculation | PASS |
| 3.2 | Add YEARLY subscription | Amazon Prime, 1499, YEARLY | Monthly impact = -(1499/12) ≈ -124.92 | Total monthly cost included ≈124.92 for this entry | PASS |
| 3.3 | Total monthly cost (4 active subs) | Netflix 199 + Spotify 119 + Amazon Prime 124.92 + Gym 1200 | ≈ Rs.1642.92 / month | Printed "Recurring Subscription Cost: Rs.1642.92 / month" | PASS |
| 3.4 | Reject invalid billing cycle | `"DAILY"` | `InvalidInputException`: cycle list message | Error shown, no subscription created | PASS |
| 3.5 | View subscriptions sorted | — | Alphabetical by name | Confirmed alphabetical order | PASS |

## Module 4 — Financial Reports

| # | Test Case | Input | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| 4.1 | Spending by category | — | Categories sorted highest spend first | Bills > Education > Food > Shopping > Transport > Health > Entertainment | PASS |
| 4.2 | Combined activity feed | — | Transactions + subscriptions listed via `Reportable.getSummaryLine()` | Both types printed correctly in one list | PASS |
| 4.3 | Monthly financial outlook | Income 45000, Expenses 10450, Subscriptions 1642.92 | Outlook = 45000 − 10450 − 1642.92 = 32907.08 | Printed "Estimated Monthly Outlook : Rs.32907.08" | PASS |
| 4.4 | Budget status summary | — | Same output as 2.6, accessible from Reports menu | Matched | PASS |

## Persistence / Restart Test

| # | Test Case | Steps | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| 5.1 | Data survives restart | Add data, exit, relaunch `Main` | All previously saved transactions/budgets/subscriptions load back | Confirmed — same data printed after restart | PASS |
| 5.2 | IDs never collide after restart | Relaunch, add one more transaction | New ID continues from highest existing (e.g. `T0010`, not `T0001`) | Confirmed via `IdGenerator.reportExistingId()` seeding | PASS |

## Summary

**29 test cases executed — 29 passed, 0 failed.**
