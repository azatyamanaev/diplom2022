package ru.itis.glabplugin.components.pipelines;

import ru.itis.glabplugin.api.dto.PipelineDto;
import ru.itis.glabplugin.api.dto.StatusDto;
import ru.itis.glabplugin.api.models.Pipeline;
import ru.itis.glabplugin.components.TableRowDefinition;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * 20.05.2022
 *
 * @author Azat Yamanaev
 */
public class PipelineTableModel extends DefaultTableModel {


    public List<Pipeline> rows;

    public List<TableRowDefinition<Pipeline>> definitions;

    public PipelineTableModel() {
        this.rows = new ArrayList<>();
        this.definitions = List.of(
                new TableRowDefinition<>("Status", x -> new StatusDto(x.getStatus(), x.getDuration(), x.getUpdatedAt())),
                new TableRowDefinition<>("Pipeline", x -> new PipelineDto(String.valueOf(x.getId()), String.valueOf(x.getProjectId()),
                        x.getBranchName(), x.getCommit())),
                new TableRowDefinition<>("Action", Pipeline::getStatus),
                new TableRowDefinition<>("Delete", x -> PipelineDto.builder()
                        .id(String.valueOf(x.getId()))
                        .projectId(String.valueOf(x.getProjectId()))
                        .build()),
                new TableRowDefinition<>("Url", Pipeline::getWebUrl),
                new TableRowDefinition<>("Error", Pipeline::getStatus));
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
        Pipeline row = rows.get(rowIndex);
        return definitions.get(columnIndex).tableModelRowFunction.apply(row);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return definitions.get(columnIndex).title;
    }
}
