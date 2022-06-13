package ru.itis.glabplugin.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;
import ru.itis.glabplugin.BackgroundService;
import ru.itis.glabplugin.api.GitlabAPI;
import ru.itis.glabplugin.api.models.Pipeline;
import ru.itis.glabplugin.utils.Utils;

import javax.swing.*;
import java.util.LinkedHashMap;

/**
 * 19.05.2022
 *
 * @author Azat Yamanaev
 */
public class StartupInitialization implements StartupActivity {

    private static final Logger logger = Logger.getInstance(StartupInitialization.class);

    @Override
    public void runActivity(@NotNull Project project) {
        logger.debug("Running startup initialization");
        ApplicationManager.getApplication().getService(AppSettingsState.class);
        BackgroundService service = project.getService(BackgroundService.class);
        logger.debug("Finished startup initialization");

    }
}
