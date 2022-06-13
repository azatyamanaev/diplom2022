package ru.itis.glabplugin.components.jobs;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import ru.itis.glabplugin.api.dto.JobDto;
import ru.itis.glabplugin.api.dto.StatusDto;
import ru.itis.glabplugin.components.StatusCell;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * 22.05.2022
 *
 * @author Azat Yamanaev
 */
public class JobJBTable extends JBTable {

    public JobJBTable(TableModel model) {
        super(model);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        TableCellRenderer cellRenderer = super.getCellRenderer(row, column);
        switch (column) {
            case 0:
                return stringCellRenderer();
            case 1:
                return jobCellRenderer();
            case 2:
                return stringCellRenderer();
            case 3:
                return duraCellRenderer();
            case 4:
                return actionCellRenderer();
        }
        return cellRenderer;
    }

    @NotNull
    @Override
    public Component prepareRenderer(@NotNull TableCellRenderer renderer, int rowIndex, int columnIndex) {
        Component component = super.prepareRenderer(renderer, rowIndex, columnIndex);
        TableColumn column = getColumnModel().getColumn(columnIndex);
        switch (columnIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
                column.setPreferredWidth(300);
                break;
            case 4:
                column.setPreferredWidth(50);
                break;
        }
        return component;
    }

    private TableCellRenderer jobCellRenderer() {
        return (table, value, isSelected, hasFocus, row, column) -> {
            JobDto statusDto = (JobDto) value;

            return new StatusCell(statusDto).getMainPanel();
        };
    }

    private TableCellRenderer duraCellRenderer() {
        return (table, value, isSelected, hasFocus, row, column) -> {
            StatusDto dto = (StatusDto) value;

            return new StatusCell(dto, false).getMainPanel();
        };
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

    private TableCellRenderer actionCellRenderer() {
        return new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String status = (String) value;
                JBLabel action;
                if (status.equals("success") || status.equals("failed") || status.equals("manual")
                        || status.equals("canceled") || status.equals("skipped")) {
                    action = new JBLabel(AllIcons.Actions.Refresh);
                    action.setToolTipText("Retry job");
                } else {
                    action = new JBLabel(AllIcons.Actions.StopRefresh);
                    action.setToolTipText("Cancel job");
                }
                action.setPreferredSize(new Dimension(45, 30));
                return action;
            }
        };
    }

    private TableCellRenderer deleteCellRenderer() {
        return new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {

                JBLabel action = new JBLabel(AllIcons.Actions.GC);
                action.setPreferredSize(new Dimension(45, 30));
                action.setEnabled(true);
                action.setToolTipText("Delete job");
                return action;
            }
        };
    }
}
