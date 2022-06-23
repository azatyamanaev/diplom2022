package ru.itis.glabplugin.ui;

import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import java.awt.*;

/**
 * 23.06.2022
 *
 * @author Azat Yamanaev
 */
public class ModalWindow {

    private JPanel mainPanel;
    private JScrollPane scrollPane;
    private JTextArea area;


    public ModalWindow(String text) {
        area.setText(text);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

}
