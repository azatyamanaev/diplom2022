package ru.itis.glabplugin.components;

import com.intellij.ui.components.JBLabel;
import ru.itis.glabplugin.api.dto.JobDto;
import ru.itis.glabplugin.api.dto.PipelineDto;
import ru.itis.glabplugin.api.dto.StatusDto;

import javax.swing.*;

/**
 * 22.05.2022
 *
 * @author Azat Yamanaev
 */
public class StatusCell {

    private JPanel mainPanel;
    private JLabel status;
    private JLabel duration;
    private JLabel updatedAt;


    public StatusCell(StatusDto dto) {
        status.setText(dto.getStatus());
//        duration.setText(String.valueOf(dto.getDuration()));
        if (dto.getDuration() != null) {
            int m = (int) (dto.getDuration() / 60);
            int s = (int) (dto.getDuration() % 60);
            duration.setText("00:0" + m + ":" + s);
        }
        if (dto.getUpdatedAt() != null) {
            updatedAt.setText(dto.getUpdatedAt());
        }

    }

    public StatusCell(PipelineDto dto) {
        status.setText(dto.getCommit());
        duration.setText(dto.getId() + " " + dto.getBranch());
    }

    public StatusCell(JobDto dto) {
        status.setText(dto.getId());
        duration.setText(dto.getBranch());
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
