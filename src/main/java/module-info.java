module client.gui {
    
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires spring.web;
    requires java.desktop;
    requires spring.core;
    requires spring.webflux;
    requires lombok;
    requires spring.security.crypto;
    requires spring.security.core;
    requires reactor.core;
    requires java.datatransfer;
    requires AnimateFX;

    

    exports client.response to com.fasterxml.jackson.databind;
    exports client.entity.process to com.fasterxml.jackson.databind;
    exports client.entity.user to com.fasterxml.jackson.databind;
    exports client.entity.process.document to com.fasterxml.jackson.databind;
    exports client.entity.deserializer to com.fasterxml.jackson.databind;
    exports client.entity to com.fasterxml.jackson.databind;
    exports client.gui;
    exports client.response.deserializer to com.fasterxml.jackson.databind;
    exports client.gui.controller;
    
    opens client.gui to javafx.fxml;
    opens client.gui.controller to javafx.fxml;
    
}