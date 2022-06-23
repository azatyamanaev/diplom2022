package ru.itis.glabplugin.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * 23.06.2022
 *
 * @author Azat Yamanaev
 */
public class ErrorDialog extends DialogWrapper {

    private final String text;

    public ErrorDialog(Integer id, String text) {
        super(true); // use current window as parent
        setTitle("Pipeline " + id + " error");
        this.text = text;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {

        return new ModalWindow(text).getMainPanel();
    }
}
