module BusinessManagementSystem {
    requires java.base;
    requires java.sql;
    requires java.desktop;
    requires com.formdev.flatlaf;
    requires java.mail;
    requires jfreechart;
    requires itextpdf;
    requires activation;

    exports app;
    exports models;
    exports controllers;
    exports utils;
    exports views;
}
