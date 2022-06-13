package ru.itis.glabplugin.components.projects;

import ru.itis.glabplugin.api.models.Project;
import ru.itis.glabplugin.components.TableRowDefinition;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * 20.05.2022
 *
 * @author Azat Yamanaev
 */
public class ProjectTableModel extends DefaultTableModel {

    public List<Project> rows;

    public List<TableRowDefinition<Project>> definitions;

    public ProjectTableModel() {
        super(20, 4);
        this.rows = new ArrayList<>();
        this.definitions = List.of(
                new TableRowDefinition<>("ID", Project::getId),
                new TableRowDefinition<>("Name", Project::getName),
                new TableRowDefinition<>("Url", Project::getUrl));
    }

    @Override
    public int getRowCount() {
        if (rows == null) rows = new ArrayList<>();
        return rows.size();
    }

    @Override
    public int getColumnCount() {
        return definitions.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Project row = rows.get(rowIndex);
        return definitions.get(columnIndex).tableModelRowFunction.apply(row);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return definitions.get(columnIndex).title;
    }

    public static void updateTable(ProjectTableModel tableModel, List<Project> projects) {
        tableModel.rows.clear();
        if (projects != null && projects.size() > 0) {
//            if (tableModel.getRowCount() < projects.size()) {
//                tableModel.setRowCount(80);
//            }
            tableModel.rows.addAll(projects);
        }
    }
}
