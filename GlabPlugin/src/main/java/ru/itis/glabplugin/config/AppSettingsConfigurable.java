package ru.itis.glabplugin.config;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;
import ru.itis.glabplugin.api.GitlabAPI;
import ru.itis.glabplugin.api.models.Pipeline;
import ru.itis.glabplugin.api.models.Project;
import ru.itis.glabplugin.components.projects.ProjectTableModel;
import ru.itis.glabplugin.utils.Utils;

import javax.swing.*;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 19.05.2022
 *
 * @author Azat Yamanaev
 */
public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent settingsComponent;

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "SDK: Application Settings Example";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return settingsComponent.getAccessToken();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        settingsComponent = new AppSettingsComponent();
        return settingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        return !settingsComponent.getAccessToken().getText().equals(settings.accessToken);
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.accessToken = settingsComponent.getAccessToken().getText();
        settings.classifierHost = settingsComponent.getClassifierHost().getText();
        settings.projects = (LinkedHashMap<Integer, Project>) Utils.map(GitlabAPI.getUserProjects());
        ProjectTableModel.updateTable(settingsComponent.getTableModel(), new ArrayList<>(settings.projects.values()));
    }

    @Override
    public void reset() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settingsComponent.getAccessToken().setText(settings.accessToken);
        settingsComponent.getClassifierHost().setText(settings.classifierHost);
        settings.projects = (LinkedHashMap<Integer, Project>) Utils.map(GitlabAPI.getUserProjects());
        ProjectTableModel.updateTable(settingsComponent.getTableModel(), new ArrayList<>(settings.projects.values()));
    }

    private void updatePipelines(AppSettingsState settings) {
        LinkedHashMap<Integer, LinkedHashMap<Integer, Pipeline>> map = new LinkedHashMap<>();

        settings.projects.keySet().forEach(id -> {
            map.put(id, (LinkedHashMap<Integer, Pipeline>) Utils.map(GitlabAPI.getProjectPipelines(id)));
        });
        settings.pipelines = map;

    }

    @Override
    public void disposeUIResources() {
        settingsComponent = null;
    }

}
