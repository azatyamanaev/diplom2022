package ru.itis.glabplugin.components.pipelines;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.NotNull;
import ru.itis.glabplugin.api.dto.PipelineDto;
import ru.itis.glabplugin.api.dto.StatusDto;
import ru.itis.glabplugin.components.StatusCell;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;

/**
 * 21.05.2022
 *
 * @author Azat Yamanaev
 */
public class PipelineJBTable extends JBTable {

    public PipelineJBTable(TableModel model) {
        super(model);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        TableCellRenderer cellRenderer = super.getCellRenderer(row, column);
        switch (column) {
            case 0:
                return statusCellRenderer();
            case 1:
                return pipelineCellRenderer();
            case 2:
                return actionCellRenderer();
            case 3:
                return deleteCellRenderer();
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
                column.setPreferredWidth(300);
                break;
            case 1:
                column.setPreferredWidth(800);
                break;
            case 2:
            case 3:
                column.setPreferredWidth(50);
                break;
        }
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

    private TableCellRenderer statusCellRenderer() {
        return (table, value, isSelected, hasFocus, row, column) -> {
            StatusDto statusDto = (StatusDto) value;

            JPanel panel = new JPanel();
            JBLabel status = new JBLabel(statusDto.getStatus());
            JBLabel duration = new JBLabel(String.valueOf(statusDto.getDuration()), new ImageIcon("/icons/time.png"), SwingConstants.LEADING);
            JBLabel updatedAt = new JBLabel(statusDto.getUpdatedAt(), new ImageIcon("/icons/calendar.png"), SwingConstants.LEADING);
            panel.add(status);
            panel.add(duration);
            panel.add(updatedAt);

            return new StatusCell(statusDto).getMainPanel();
        };
    }

    private TableCellRenderer pipelineCellRenderer() {
        return (table, value, isSelected, hasFocus, row, column) -> {
            PipelineDto dto = (PipelineDto) value;

            return new StatusCell(dto).getMainPanel();
        };
    }

    private TableCellRenderer actionCellRenderer() {
        return new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String status = String.valueOf(value);
                JBLabel action;
                if (status.equals("success") || status.equals("failed") || status.equals("manual")
                        || status.equals("canceled") || status.equals("skipped")) {
                    action = new JBLabel(AllIcons.Actions.Refresh);
                    action.setToolTipText("Retry failed jobs");
                } else {
                    action = new JBLabel(AllIcons.Actions.StopRefresh);
                    action.setToolTipText("Cancel pipeline");
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
                String status = String.valueOf(value);
                JBLabel action = new JBLabel(AllIcons.Actions.GC);
                action.setPreferredSize(new Dimension(45, 30));
                action.setEnabled(true);
                action.setToolTipText("Delete pipeline");
                return action;
            }
        };
    }
}
