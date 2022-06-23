package ru.itis.glabplugin.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.itis.glabplugin.api.models.Pipeline;
import ru.itis.glabplugin.api.models.PipelineJob;
import ru.itis.glabplugin.api.models.Project;

import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 19.05.2022
 *
 * @author Azat Yamanaev
 */
@State(
        name = "AppSettingsState",
        storages = @Storage("AppSettingsState.xml")
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    public String accessToken = "";
    public String classifierHost = "http://localhost:8080";
    public LinkedHashMap<Integer, Project> projects = new LinkedHashMap<>();
    public LinkedHashMap<Integer, LinkedHashMap<Integer, Pipeline>> pipelines = new LinkedHashMap<>();
    public LinkedHashMap<Integer, List<PipelineJob>> pipelineJobs = new LinkedHashMap<>();

    public static AppSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingsState.class);
    }

    @Nullable
    @Override
    public AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

}
