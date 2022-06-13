package ru.itis.glabplugin.components;

import com.intellij.ui.components.JBLabel;
import ru.itis.glabplugin.api.dto.JobDto;
import ru.itis.glabplugin.api.dto.PipelineDto;
import ru.itis.glabplugin.api.dto.StatusDto;
import ru.itis.glabplugin.utils.Utils;

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


    public StatusCell(StatusDto dto, boolean pipeline) {
        if (pipeline) {
            status.setText(dto.getStatus());
            if (dto.getDuration() != null) {
                int m = (int) (dto.getDuration() / 60);
                int s = (int) (dto.getDuration() % 60);
                String mm = m < 10 ? "0" + m : String.valueOf(m);
                String ss = s < 10 ? "0" + s : String.valueOf(s);
                duration.setText("00:" + mm + ":" + ss);
            }
            if (dto.getUpdatedAt() != null) {
                updatedAt.setText(Utils.timeDiff(dto.getUpdatedAt()));
            }
        } else {
            if (dto.getDuration() != null) {
                int m = (int) (dto.getDuration() / 60);
                int s = (int) (dto.getDuration() % 60);
                String mm = m < 10 ? "0" + m : String.valueOf(m);
                String ss = s < 10 ? "0" + s : String.valueOf(s);
                duration.setText("00:" + mm + ":" + ss);
            }
            if (dto.getUpdatedAt() != null) {
                updatedAt.setText(Utils.timeDiff(dto.getUpdatedAt()));
            }
        }


    }

    public StatusCell(PipelineDto dto) {
        status.setText("id: " + dto.getId());
        duration.setText("branch: " + dto.getBranch());
    }

    public StatusCell(JobDto dto) {
        status.setText("id: " + dto.getId());
        duration.setText("branch: " + dto.getBranch());
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
