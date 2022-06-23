package ru.itis.glabplugin.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import com.intellij.uiDesigner.core.GridConstraints;
import org.jetbrains.annotations.NotNull;
import ru.itis.glabplugin.BackgroundService;
import ru.itis.glabplugin.api.GitlabAPI;
import ru.itis.glabplugin.api.models.Pipeline;
import ru.itis.glabplugin.api.models.PipelineJob;
import ru.itis.glabplugin.api.models.Project;
import ru.itis.glabplugin.components.jobs.JobJBTable;
import ru.itis.glabplugin.components.jobs.JobTableModel;
import ru.itis.glabplugin.components.pipelines.PipelineJBTable;
import ru.itis.glabplugin.components.pipelines.PipelineTableModel;
import ru.itis.glabplugin.config.AppSettingsState;
import ru.itis.glabplugin.utils.UrlOpener;
import ru.itis.glabplugin.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * 16.05.2022
 *
 * @author Azat Yamanaev
 */
public class GToolWindow {

    private JPanel myToolWindowContent;
    private JPanel tablePanel;
    private JComboBox<String> pipelineStatus;
    private JComboBox<Project> repos;
    private JBTable pipelineTable;
    private JPanel actionPanel;
    private JScrollPane scrollPane;

    private PipelineTableModel tableModel;

    private final com.intellij.openapi.project.Project project;
    private final BackgroundService backgroundService;

    public GToolWindow(com.intellij.openapi.project.Project project, ToolWindow toolWindow) {
        this.project = project;
        backgroundService = project.getService(BackgroundService.class);
        createTablePanel(toolWindow);
    }

    public void createTablePanel(ToolWindow toolWindow) {
        AnActionButton refreshActionButton = new AnActionButton("Refresh", AllIcons.Actions.Refresh) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {

                Project proj = (Project) repos.getSelectedItem();
                int index = repos.getSelectedIndex();
                fillRepos();
                repos.setSelectedIndex(index);
                if (!proj.getName().equals("No project")) {
                    backgroundService.runAsync(project, "Update project pipelines", () -> {
                        AppSettingsState settings = AppSettingsState.getInstance();
                        List<Pipeline> pipelines = GitlabAPI.getProjectPipelines(proj.getId());
                        pipelines = pipelines == null ? new ArrayList<>() : pipelines;
                        settings.pipelines.put(proj.getId(), (LinkedHashMap<Integer, Pipeline>) Utils.map(pipelines));
                        tableModel.rows.clear();
                        tableModel.rows.addAll(pipelines);
                    });

                }
            }

            @Override
            public JComponent getContextComponent() {
                return pipelineTable;
            }

        };
        DefaultActionGroup actionGroup = new DefaultActionGroup(refreshActionButton);
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, true);


        repos = new ComboBox<>();
        fillRepos();
        repos.addItem(Project.builder().id(1).name("Test project").build());
        repos.setPreferredSize(new Dimension(200, 30));
        repos.addItemListener(e -> {
            Project value = (Project) e.getItem();
            Integer id = value.getId();
            int state = e.getStateChange();
            if (state == ItemEvent.SELECTED) {
                tableModel.rows.clear();
                if (!value.getName().equals("No project")) {
                    LinkedHashMap<Integer, LinkedHashMap<Integer, Pipeline>> map = AppSettingsState.getInstance().pipelines;
                    if (map.containsKey(id) && map.get(id) != null) {
                        tableModel.rows.clear();
                        tableModel.rows.addAll(map.get(id).values());
                    } else {
                        List<Pipeline> pipelines = GitlabAPI.getProjectPipelines(id);
                        if (pipelines != null && pipelines.size() > 0) {
                            map.put(id, (LinkedHashMap<Integer, Pipeline>) Utils.map(pipelines));
                            tableModel.rows.clear();
                            tableModel.rows.addAll(pipelines);
                        }

                    }
                }
            }
        });

        GridConstraints gridCon = new GridConstraints();
        pipelineStatus = new ComboBox<>();
        pipelineStatus.addItem("All");
        pipelineStatus.addItem("Success");
        pipelineStatus.addItem("Failed");
        pipelineStatus.addItem("Canceled");
        pipelineStatus.addItem("Skipped");
        pipelineStatus.addItemListener(e -> {
            String status = (String) e.getItem();
            Project proj = (Project) repos.getSelectedItem();
            List<Pipeline> pipelines;
            if (status.equals("All")) {
                pipelines = new ArrayList<>(AppSettingsState.getInstance().pipelines.get(proj.getId()).values());
            } else {
                pipelines = AppSettingsState.getInstance().pipelines.get(proj.getId()).values()
                        .stream().filter(pipe -> pipe.getStatus().equals(status.toLowerCase(Locale.ROOT)))
                        .collect(Collectors.toList());
            }
            tableModel.rows.clear();
            tableModel.rows.addAll(pipelines);
        });


        gridCon.setAnchor(GridConstraints.ANCHOR_WEST);
        actionPanel.add(repos, gridCon, 0);
        gridCon.setIndent(20);
        actionPanel.add(pipelineStatus, gridCon, 1);
        gridCon.setIndent(0);
        gridCon.setAnchor(GridConstraints.ANCHOR_EAST);
        actionPanel.add(actionToolbar.getComponent(), gridCon, 2);

        tableModel = new PipelineTableModel();

        pipelineTable = new PipelineJBTable(tableModel);

        pipelineTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        pipelineTable.addMouseListener(getActionsListener(toolWindow));
        pipelineTable.addMouseMotionListener(getActionsListener(toolWindow));

//        scrollPane.setViewportView(pipelineTable);
        JButton button = new JButton("Load pipelines");
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setEnabled(false);
        button.addActionListener(e -> {
            Project project = (Project) repos.getSelectedItem();
            int index = repos.getSelectedIndex();
            fillRepos();
            repos.setSelectedIndex(index);
            if (!project.getName().equals("No project")) {
                AppSettingsState settings = AppSettingsState.getInstance();
                List<Pipeline> pipelines = GitlabAPI.getProjectPipelines(project.getId());
                pipelines = pipelines == null ? new ArrayList<>() : pipelines;
                settings.pipelines.put(project.getId(), (LinkedHashMap<Integer, Pipeline>) Utils.map(pipelines));
                tableModel.rows.clear();
                tableModel.rows.addAll(pipelines);
            }
            scrollPane.setViewportView(pipelineTable);
        });
        backgroundService.runAsync(project, "Load projects and pipelines", () ->
        {
            updateData();
            button.setEnabled(true);
        });
        scrollPane.setViewportView(button);
    }


    public void updateJobs(JobTableModel jobTableModel, Pipeline pipeline) {
        backgroundService.runAsync(project, "Update pipeline jobs", () -> {
            AppSettingsState settings = AppSettingsState.getInstance();
            List<PipelineJob> jobs = GitlabAPI.getPipelineJobs(pipeline.getProjectId(), pipeline.getId());
            jobs = jobs == null ? new ArrayList<>() : jobs;
            settings.pipelineJobs.put(pipeline.getId(), jobs);
            jobTableModel.rows.clear();
            jobTableModel.rows.addAll(jobs);
        });
    }

    public JPanel createPipelineJobsPanel(Pipeline pipeline) {

        JPanel panel = new JPanel(new BorderLayout());

        JobTableModel jobTableModel = new JobTableModel();
        JBTable jobTable = new JobJBTable(jobTableModel);
        jobTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);


        AnActionButton refreshActionButton = new AnActionButton("Refresh", AllIcons.Actions.Refresh) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                updateJobs(jobTableModel, pipeline);

            }

            @Override
            public JComponent getContextComponent() {
                return jobTable;
            }

        };
        DefaultActionGroup actionGroup = new DefaultActionGroup(refreshActionButton);
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, actionGroup, true);


        panel.add(actionToolbar.getComponent(), BorderLayout.WEST);
        panel.add(new JBScrollPane(jobTable), BorderLayout.CENTER);

        jobTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = jobTable.columnAtPoint(e.getPoint());
                int row = jobTable.rowAtPoint(e.getPoint());
                PipelineJob job = jobTableModel.rows.get(row);

                int button = e.getButton();
                int count = e.getClickCount();
                if (button == MouseEvent.BUTTON1 && count == 1) {
                    switch (column) {
                        case 1:
                            String path = GitlabAPI.writeLog(job.getProjectId(), job.getPipelineId(), job.getId(), job.getStage());

//                            openEditor(project, path);
                            break;
                        case 4:
                            String s = (String) jobTable.getValueAt(row, column);
                            if (s.equals("running") || s.equals("pending")) {
                                backgroundService.runAsync(project, "Cancel job",
                                        () -> {
                                            GitlabAPI.retryCancelJob(job.getProjectId(), job.getId(), "cancel");
                                            updateJobs(jobTableModel, pipeline);
                                        });
                            } else {
                                backgroundService.runAsync(project, "Retry job",
                                        () -> {
                                            GitlabAPI.retryCancelJob(job.getProjectId(), job.getId(), "retry");
                                            updateJobs(jobTableModel, pipeline);
                                        });
                            }
                            break;
                        case 5:
                            backgroundService.runAsync(project, "Delete job",
                                    () -> {
                                        GitlabAPI.eraseJob(job.getProjectId(), job.getId());
                                        jobTableModel.rows.remove(row);
                                    });
                            break;
                    }
                }
            }
        });

        jobTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int columnIndex = jobTable.columnAtPoint(e.getPoint());
                int rowIndex = jobTable.rowAtPoint(e.getPoint());
                if (columnIndex > 3 && rowIndex > -1 && rowIndex < jobTableModel.getRowCount()) {
                    pipelineTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    pipelineTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        });

        AppSettingsState state = AppSettingsState.getInstance();
        List<PipelineJob> pipelineJobs;
        if (state.pipelineJobs.containsKey(pipeline.getId())) {
            pipelineJobs = state.pipelineJobs.get(pipeline.getId());
            if (pipelineJobs != null && pipelineJobs.size() > 0) {
                jobTableModel.rows.addAll(pipelineJobs);
            }
        } else {
            pipelineJobs = GitlabAPI.getPipelineJobs(pipeline.getProjectId(), pipeline.getId());
            state.pipelineJobs.put(pipeline.getId(), pipelineJobs);
            jobTableModel.rows.addAll(pipelineJobs);
        }
        return panel;
    }

    public JPanel getContent() {
        return myToolWindowContent;
    }

    public void openEditor(com.intellij.openapi.project.Project project, String path) {

        FileEditorManagerEx manager = FileEditorManagerEx.getInstanceEx(project);
        FileDocumentManager documentManager = FileDocumentManager.getInstance();
        manager.openFile(LocalFileSystem.getInstance().findFileByPath("/home/monitor/Desktop/Studies/diplom/idea projects/GlabPlugin/src/main/" +
                "java/ru/itis/glabplugin/BackgroundService.java"), true);

    }

    private void fillRepos() {
        repos.removeAllItems();
        repos.addItem(Project.builder().name("No project").build());
//        repos.addItem(Project.builder().tracked(true).id(33346042).name("Neome").url("https://gitlab.com/technaxis/mkusa-backend").build());
        List<Project> projects = new ArrayList<>(AppSettingsState.getInstance().projects.values());
        if (projects.size() > 0) {
            projects.stream().filter(Project::isTracked).forEach(project -> repos.addItem(project));
        }
    }

    private MouseAdapter getActionsListener(ToolWindow toolWindow) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = pipelineTable.columnAtPoint(e.getPoint());

                int button = e.getButton();
                int count = e.getClickCount();
                int row = pipelineTable.rowAtPoint(e.getPoint());
                Pipeline pipeline = tableModel.rows.get(row);
                if (button == MouseEvent.BUTTON1 && count == 1) {
                    switch (column) {
                        case 1:
                            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                            Content content = contentFactory.createContent(createPipelineJobsPanel(pipeline), String.valueOf(pipeline.getId()), false);
                            toolWindow.getContentManager().addContent(content);
                            break;
                        case 2:
                            String s = (String) pipelineTable.getValueAt(row, column);
                            if (s.equals("running")) {
                                backgroundService.runAsync(project, "Cancel pipeline",
                                        () -> {
                                            Pipeline pline = GitlabAPI.retryCancelPipeline(pipeline.getProjectId(), pipeline.getId(), "cancel");
                                            pline.setStatus("canceled");
                                            tableModel.rows.set(row, pline);
                                            tableModel.fireTableRowsUpdated(row, row);
                                        });
                            } else {
                                backgroundService.runAsync(project, "Retry pipeline",
                                        () -> {
                                            Pipeline pline = GitlabAPI.retryCancelPipeline(pipeline.getProjectId(), pipeline.getId(), "retry");
                                            tableModel.rows.set(row, pline);
                                            tableModel.fireTableRowsUpdated(row, row);
                                        });
                            }
                            break;
                        case 3:
                            backgroundService.runAsync(project, "Delete pipeline",
                                    () -> {
                                        GitlabAPI.deletePipeline(pipeline.getProjectId(), pipeline.getId());
                                        tableModel.rows.remove(row);
                                    });
                            break;
                        case 4:
                            UrlOpener.openUrl(pipeline.getWebUrl());
                            break;
                        case 5:
                            String message = GitlabAPI.getPipelineError(pipeline.getId());
                            if (message == null) {
                                message = "There is no error info about this pipeline";
                            }
                            JTextField field = new JTextField(message);
                            field.setPreferredSize(new Dimension(800, 800));

                            ErrorDialog dialog = new ErrorDialog(pipeline.getId(), message);
                            dialog.show();
                            break;
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                int columnIndex = pipelineTable.columnAtPoint(e.getPoint());
                int rowIndex = pipelineTable.rowAtPoint(e.getPoint());
                if (columnIndex > 1 && rowIndex > -1 && rowIndex < tableModel.getRowCount()) {
                    pipelineTable.setCursor(new Cursor(Cursor.HAND_CURSOR));
                } else {
                    pipelineTable.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }
        };
    }

    public void updateData() {
        AppSettingsState state = AppSettingsState.getInstance();
        state.projects = (LinkedHashMap<Integer, ru.itis.glabplugin.api.models.Project>) Utils.map(GitlabAPI.getUserProjects());
        LinkedHashMap<Integer, LinkedHashMap<Integer, Pipeline>> map = new LinkedHashMap<>();

        state.projects.keySet().forEach(id -> {
            map.put(id, (LinkedHashMap<Integer, Pipeline>) Utils.map(GitlabAPI.getProjectPipelines(id)));
        });
        state.pipelines = map;
        state.projects.keySet().stream().filter(id -> !map.containsKey(id) || map.get(id).size() == 0)
                .forEach(id -> {
                    ru.itis.glabplugin.api.models.Project project = state.projects.get(id);
                    project.setTracked(false);
                    state.projects.put(id, project);
                });

        if (state.pipelineJobs == null) {
            state.pipelineJobs = new LinkedHashMap<>();
        }
    }

}
