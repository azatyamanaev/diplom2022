package ru.itis.glabplugin.components.jobs;

import ru.itis.glabplugin.api.dto.JobActionDto;
import ru.itis.glabplugin.api.dto.JobDto;
import ru.itis.glabplugin.api.dto.PipelineDto;
import ru.itis.glabplugin.api.dto.StatusDto;
import ru.itis.glabplugin.api.models.Pipeline;
import ru.itis.glabplugin.api.models.PipelineJob;
import ru.itis.glabplugin.components.TableRowDefinition;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * 22.05.2022
 *
 * @author Azat Yamanaev
 */
public class JobTableModel extends DefaultTableModel {

    public List<PipelineJob> rows;

    public List<TableRowDefinition<PipelineJob>> definitions;

    public JobTableModel() {
        this.rows = new ArrayList<>();
        this.definitions = List.of(
                new TableRowDefinition<>("Status", PipelineJob::getStatus),
                new TableRowDefinition<>("Job", x -> new JobDto(String.valueOf(x.getId()), x.getName())),
                new TableRowDefinition<>("Stage", PipelineJob::getStage),
                new TableRowDefinition<>("Duration", x -> new StatusDto(x.getWebUrl(), x.getDuration(), x.getUpdatedAt())),
                new TableRowDefinition<>("Action", PipelineJob::getStatus));
//                new TableRowDefinition<>("Delete", PipelineJob::isRetried));
//                new TableRowDefinition<>("Id", PipelineJob::getId));
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
        PipelineJob row = rows.get(rowIndex);
        return definitions.get(columnIndex).tableModelRowFunction.apply(row);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return definitions.get(columnIndex).title;
    }

}
