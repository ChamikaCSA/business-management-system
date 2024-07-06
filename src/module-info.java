module BusinessManagementSystem {
    requires java.base;
    requires java.sql;
    requires java.desktop;

    exports app;
    exports entities;
    exports services;
    exports utils;
    exports GUI;
}
