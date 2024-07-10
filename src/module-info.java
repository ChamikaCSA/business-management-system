module BusinessManagementSystem {
    requires java.base;
    requires java.sql;
    requires java.desktop;
    requires com.formdev.flatlaf;
    requires java.mail;

    exports app;
    exports entities;
    exports services;
    exports utils;
    exports gui;
}
