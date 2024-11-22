module Group2 {
    // Require Necessary JavaFX And SQL Modules
	requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;

    // Open Application Package To Required Modules
    opens application to javafx.fxml, javafx.base, javafx.graphics;

    // Export Application Package For External Accessibility
    exports application;
}