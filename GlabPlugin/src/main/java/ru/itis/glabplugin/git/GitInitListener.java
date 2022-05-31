package ru.itis.glabplugin.git;

import com.intellij.util.messages.Topic;
import git4idea.repo.GitRepository;

import java.util.List;

/**
 * 26.05.2022
 *
 * @author Azat Yamanaev
 */
public interface GitInitListener {

    Topic<GitInitListener> GIT_INITIALIZED = Topic.create(".git initialized", GitInitListener.class);

    void handle(List<GitRepository> gitRepositories);

}