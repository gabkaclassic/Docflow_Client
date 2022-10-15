module client.gui {
    requires javafx.controls;
    requires javafx.fxml;
    
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires spring.web;
    requires spring.core;
    requires spring.webflux;
    requires lombok;
    requires spring.security.core;
    requires reactor.core;
    
    opens client.gui to javafx.fxml;
    exports client.gui;
}