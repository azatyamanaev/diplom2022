package ru.itis.glabplugin.components.projects;

import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * 20.05.2022
 *
 * @author Azat Yamanaev
 */
public class ProjectJBTable extends JBTable {

    public ProjectJBTable(TableModel model) {
        super(model);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        TableCellRenderer cellRenderer = super.getCellRenderer(row, column);
        if (column < 3) return stringCellRenderer();
//        if (column == 3) return checkboxCellRenderer();
        return cellRenderer;
    }

    @NotNull
    @Override
    public Component prepareRenderer(@NotNull TableCellRenderer renderer, int rowIndex, int columnIndex) {
        Component component = super.prepareRenderer(renderer, rowIndex, columnIndex);
        TableColumn column = getColumnModel().getColumn(columnIndex);
        if (columnIndex < 3) column.setPreferredWidth(220);
//        if (columnIndex == 3) column.setPreferredWidth(80);
        return component;
    }

    private TableCellRenderer stringCellRenderer() {
        return new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String val = String.valueOf(value);
                JBLabel label = new JBLabel(val);
                label.setCopyable(true);
                return new JBLabel(val);
            }
        };
    }

    private TableCellRenderer checkboxCellRenderer() {
        return new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                boolean val = (boolean) value;
                JBCheckBox checkBox = new JBCheckBox();
                checkBox.setEnabled(true);
                checkBox.setHorizontalAlignment(SwingConstants.CENTER);
                checkBox.setSelected(val);
                checkBox.addActionListener(e -> Messages.showMessageDialog(e.getActionCommand(), "Checkbox", Messages.getInformationIcon()));

                return checkBox;
            }
        };
    }
}
