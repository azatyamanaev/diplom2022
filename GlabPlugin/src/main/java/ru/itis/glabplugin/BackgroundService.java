package ru.itis.glabplugin;

import com.google.common.base.Stopwatch;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.BackgroundableProcessIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.messages.MessageBus;
import org.jetbrains.annotations.NotNull;
import ru.itis.glabplugin.api.GitlabAPI;
import ru.itis.glabplugin.git.GitInitListener;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 25.05.2022
 *
 * @author Azat Yamanaev
 */
public class BackgroundService {

    private static final Logger logger = Logger.getInstance(BackgroundService.class);

    private static final int INITIAL_DELAY = 0;
    private static final int UPDATE_DELAY = 30;

    private boolean isActive = false;
    private boolean isRunning = false;
    private final Runnable backgroundTask;
    private ScheduledFuture<?> scheduledFuture;
    private final Project project;
    private final MessageBus messageBus;

    public BackgroundService(Project project) {
        this.project = project;

        messageBus = project.getMessageBus();

        backgroundTask = () -> {
            update(project, false);
        };

        project.getMessageBus().connect().subscribe(GitInitListener.GIT_INITIALIZED, gitRepository -> {
            logger.debug("Retrieved GIT_INITIALIZED event. Starting background task if needed");
            //If the background task is started at once for some reason the progress indicator is never closed
            if (isActive) {
                logger.debug("Background task already running");
                return;
            }
            logger.debug("Starting background task");
            scheduledFuture = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(backgroundTask, 5, UPDATE_DELAY, TimeUnit.SECONDS);
            isActive = true;
        });
    }

    public synchronized void update(Project project, boolean triggeredByUser) {
        if (isRunning) {
            return;
        }
        Task.Backgroundable updateTask = new Task.Backgroundable(project, "Loading gitLab pipelines", false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                Stopwatch stopwatch = Stopwatch.createStarted();
                try {
                    getUpdateRunnable(triggeredByUser).run();

                    if (stopwatch.elapsed(TimeUnit.MILLISECONDS) < 1000) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ignored) {
                        }
                    }
                } finally {
                    indicator.stop();
                }
            }
        };

        final BackgroundableProcessIndicator updateProgressIndicator = new BackgroundableProcessIndicator(updateTask);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(updateTask, updateProgressIndicator);
    }

    public void runAsync(Project project, String name, Runnable runnable) {
        Task.Backgroundable updateTask = new Task.Backgroundable(project, name, false) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                Stopwatch stopwatch = Stopwatch.createStarted();
                try {
                    runnable.run();

                    if (stopwatch.elapsed(TimeUnit.MILLISECONDS) < 1000) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException ignored) {
                        }
                    }
                } finally {
                    indicator.stop();
                }
            }
        };

        final BackgroundableProcessIndicator updateProgressIndicator = new BackgroundableProcessIndicator(updateTask);
        ProgressManager.getInstance().runProcessWithProgressAsynchronously(updateTask, updateProgressIndicator);
    }

    private Runnable getUpdateRunnable(boolean triggeredByUser) {
        return () -> {
            if (isRunning || project.isDisposed()) {
                return;
            }
            isRunning = true;
            try {
                logger.debug("Starting IntelliJ background task", (triggeredByUser ? " triggered by user" : ""));
                GitlabAPI.getProjectPipelines(33346042);
                logger.debug("Finished IntelliJ background task");
            }
            finally {
                isRunning = false;
            }
        };
    }

    public synchronized boolean startBackgroundTask() {
        if (isActive) {
            logger.debug("Background task already running");
            return false;
        }
        logger.debug("Starting background task");
        scheduledFuture = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(backgroundTask, INITIAL_DELAY, UPDATE_DELAY, TimeUnit.SECONDS);
        isActive = true;
        return true;
    }

    public synchronized void stopBackgroundTask() {
        if (!isActive) {
            logger.debug("Background task already stopped");
        }
        if (scheduledFuture == null) {
            return;
        }
        logger.debug("Stopping background task");
        boolean cancelled = scheduledFuture.cancel(false);
        isActive = !cancelled;
        logger.debug("Background task cancelled: ", cancelled);
    }

    public synchronized boolean isActive() {
        return isActive;
    }

    public synchronized void restartBackgroundTask() {
        logger.debug("Restarting background task");
        if (isActive) {
            boolean cancelled = scheduledFuture.cancel(false);
            isActive = !cancelled;
            logger.debug("Background task cancelled: ", cancelled);
        }
        scheduledFuture = AppExecutorUtil.getAppScheduledExecutorService().scheduleWithFixedDelay(backgroundTask, INITIAL_DELAY, UPDATE_DELAY, TimeUnit.SECONDS);
        isActive = true;
    }
}
