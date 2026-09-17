package com.spendwise.model;

/**
 * Anything that can describe itself in one printable line for the
 * Reports module. Transaction and Subscription are very different
 * classes, but ReportService can treat a mixed list of them
 * uniformly as Reportable - a simple, explainable example of
 * interfaces + polymorphism working together.
 */
public interface Reportable {
    String getSummaryLine();
}
