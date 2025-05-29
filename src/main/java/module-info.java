module pt.ipp.isep.dei {
    requires javafx.controls;
    requires javafx.fxml;
    requires AuthLib;
    requires guru.nidi.graphviz;
    requires org.apache.commons.lang3;
    requires java.logging;

    opens pt.ipp.isep.dei.ui.gui to javafx.fxml;
    opens pt.ipp.isep.dei.ui.gui.controller to javafx.fxml;
    exports pt.ipp.isep.dei.ui.gui;
    exports pt.ipp.isep.dei.ui.gui.controller;
} 