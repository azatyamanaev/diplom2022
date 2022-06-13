package ru.itis.glabplugin.config;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.FormBuilder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;
import ru.itis.glabplugin.api.GitlabAPI;
import ru.itis.glabplugin.api.models.Pipeline;
import ru.itis.glabplugin.components.projects.ProjectJBTable;
import ru.itis.glabplugin.components.projects.ProjectTableModel;
import ru.itis.glabplugin.api.models.Project;
import ru.itis.glabplugin.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 19.05.2022
 *
 * @author Azat Yamanaev
 */
@Getter
@Setter
public class AppSettingsComponent {

    private final JPanel panel;

    private JBTable table;
    private ProjectTableModel tableModel;
    private JBTextField accessToken;
    private JBTextField classifierHost;

    private final GitlabAPI api = new GitlabAPI();

    public AppSettingsComponent() {
        tableModel = new ProjectTableModel();
        accessToken = new JBTextField(AppSettingsState.getInstance().accessToken);
        classifierHost = new JBTextField(AppSettingsState.getInstance().classifierHost);

        JPanel tPanel = createTablePanel(new ArrayList<>(AppSettingsState.getInstance().projects.values()));
        panel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Access Token: "), accessToken, 1, false)
                .addLabeledComponent(new JBLabel("Classifier host: "), classifierHost, 1, false)
                .addComponent(tPanel)
                .addComponent(createActionPanel())
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel createTablePanel(@Nullable List<Project> projects) {
        JPanel tablePanel = new JPanel();
        table = new ProjectJBTable(tableModel);
        ProjectTableModel.updateTable(tableModel, projects);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setCellSelectionEnabled(true);
        table.setIntercellSpacing(new Dimension(5, 0));
        JBScrollPane scrollPane = new JBScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(695, 300));

        tablePanel.add(scrollPane, BorderLayout.CENTER, 0);
        return tablePanel;
    }

    public JComponent createActionPanel() {
        JPanel actionPanel = new JPanel();

        JBLabel label = new JBLabel(AllIcons.Actions.Refresh);
        label.setToolTipText("Check which projects have pipelines");
        label.setFocusable(true);
        label.setEnabled(true);
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int button = e.getButton();
                int count = e.getClickCount();
                if (button == MouseEvent.BUTTON1 && count == 1) {
                    AppSettingsState settings = AppSettingsState.getInstance();
                    LinkedHashMap<Integer, LinkedHashMap<Integer, Pipeline>> map = new LinkedHashMap<>();
                    Map<Integer, Project> projects = settings.projects;
                    projects.keySet().forEach(id -> {
                        List<Pipeline> pipelines = GitlabAPI.getProjectPipelines(id);
                        if (pipelines != null && pipelines.size() > 0) {
                            map.put(id, (LinkedHashMap<Integer, Pipeline>) Utils.map(pipelines));
                        }
                    });
                    settings.pipelines = map;
                    projects.keySet().stream().filter(id -> !map.containsKey(id) || map.get(id).size() == 0)
                            .forEach(id -> {
                                Project project = projects.get(id);
                                project.setTracked(false);
                                projects.put(id, project);
                            });
                    ProjectTableModel.updateTable(tableModel, new ArrayList<>(projects.values()));
                }
            }
        });
        actionPanel.add(label);
        return actionPanel;
    }

}
