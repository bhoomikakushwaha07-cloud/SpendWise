# Problem Statement

## Problem

People commonly record their expenses in one place (or nowhere),
track subscriptions in another (or forget about them entirely), and
rarely have an easy way to see whether they are overspending in a
given category until the money is already gone. There is no single,
lightweight place to see income, expenses, budgets, and recurring
subscriptions together.

## Scope of the Project

SpendWise is a **console-based personal finance and subscription
manager** built in core Java. It covers:

- Recording and managing income/expense transactions
- Setting and monitoring category-wise monthly budgets
- Tracking recurring subscriptions across different billing cycles
  and calculating their true monthly/yearly cost
- Generating financial reports that combine all of the above

It deliberately stays out of scope for:
- Multi-user support or authentication
- Bank/API integrations, payment gateways, or real transactions
- A GUI or web interface (this is a CLI tool)
- A relational database (plain CSV files are used instead)

## Target Users

Students, young professionals, or anyone managing personal finances
on a single computer who wants a simple, transparent, no-signup way
to track where their money goes each month — without installing a
spreadsheet template or a mobile app that asks for bank access.

## High-Level Features

1. **Transaction Management** — add, view, search, filter, sort, and
   delete income/expense records; see total income, total expense,
   and net balance.
2. **Budget Management** — set a monthly limit per category; get a
   warning at 80% usage and an alert if the budget is exceeded.
3. **Subscription Management** — track recurring charges (weekly,
   monthly, quarterly, yearly) with add/cancel/delete and a combined
   monthly/yearly cost view.
4. **Financial Reports** — spending-by-category breakdown, a
   combined activity feed of transactions and subscriptions, and an
   overall monthly financial outlook.

