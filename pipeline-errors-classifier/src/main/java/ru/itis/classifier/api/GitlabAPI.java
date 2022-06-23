package ru.itis.classifier.api;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.itis.classifier.models.*;
import ru.itis.classifier.utils.Settings;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 16.05.2022
 *
 * @author Azat Yamanaev
 */
@Service
@RequiredArgsConstructor
public class GitlabAPI {


    private final RestClient restClient;
    private final Mapper mapper;
    private final Settings settings;



    public List<Project> getUserProjects(int page, int perPage) {
        List<Object> result = (List<Object>) restClient.get("https://gitlab.com/api/v4/projects", Object.class)
                .parameter("membership", true)
                .parameter("page", page)
                .parameter("per_page", perPage)
                .header("PRIVATE-TOKEN", settings.getToken()).send().orElse(null);
        return result == null ? null : result.stream().map(x -> mapper.toProject((LinkedHashMap<String, Object>) x)).collect(Collectors.toList());
    }

    public Project getProject(Long projectId) {
        Object result = restClient.get("https://gitlab.com/api/v4/projects/" + projectId, Object.class)
                .header("PRIVATE-TOKEN", settings.getToken()).send().orElse(null);
        return result == null ? null : mapper.toProject((LinkedHashMap<String, Object>) result);
    }

    public List<Pipeline> getProjectPipelines(Long projectId, int page, int perPage) {
        List<Object> result = (List<Object>) restClient.get("https://gitlab.com/api/v4/projects/" + projectId + "/pipelines", Object.class)
                .parameter("page", page)
                .parameter("per_page", perPage)
                .header("PRIVATE-TOKEN", settings.getToken()).send().orElse(null);
        return result == null ? null : result.stream().map(x -> mapper.toPipeline((LinkedHashMap<String, Object>) x)).collect(Collectors.toList());
    }

    public List<PipelineJob> getPipelineJobs(Long projectId, Long pipelineId) {
        List<Object> result = (List<Object>) restClient.get("https://gitlab.com/api/v4/projects/" + projectId + "/pipelines/" + pipelineId + "/jobs",
                        Object.class)
                .parameter("per_page", 50)
                .header("PRIVATE-TOKEN", settings.getToken()).send().orElse(null);
        return result == null ? null : result.stream().map(x -> mapper.toJob((LinkedHashMap<String, Object>) x)).collect(Collectors.toList());
    }


    public String getJobLog(Long projectId, Long jobId) {
        String res = restClient.get("https://gitlab.com/api/v4/projects/" + projectId + "/jobs/" + jobId + "/trace", String.class)
                .header("PRIVATE-TOKEN", settings.getToken()).getString();
        if (res == null) return null;
        res = res.replace("\u001B", "")
                .replace("[0K", "")
                .replace("[36;1m", "")
                .replace("[32;1m", "")
                .replace("[31;1m", "")
                .replace("[0;m", "");
        return res;
    }

    public Commit getCommit(Long projectId, String commit) {
        Object result = restClient.get("https://gitlab.com/api/v4/projects/" + projectId + "/repository/commits/" + commit,
                Object.class).header("PRIVATE-TOKEN", settings.getToken()).send().orElse(null);
        return result == null ? null : mapper.toCommit((LinkedHashMap<String, Object>) result);
    }

    public List<Diff> getCommitDiff(Long projectId, String commit) {
        List<Object> result = (List<Object>) restClient.get("https://gitlab.com/api/v4/projects/" + projectId + "/repository/commits/" + commit + "/diff",
                Object.class).header("PRIVATE-TOKEN", settings.getToken()).send().orElse(null);
        return result == null ? null : result.stream().map(x -> mapper.toDiff((LinkedHashMap<String, Object>) x)).collect(Collectors.toList());
    }

    public List<MergeRequest> getCommitMergeRequests(Long projectId, String commit) {
        List<Object> result = (List<Object>) restClient.get("https://gitlab.com/api/v4/projects/" + projectId + "/repository/commits/" + commit + "/merge_requests",
                Object.class).header("PRIVATE-TOKEN", settings.getToken()).send().orElse(null);
        return result == null ? null : result.stream().map(x -> mapper.toMergeRequest((LinkedHashMap<String, Object>) x)).collect(Collectors.toList());
    }

    public String getFile(Long projectId, String path) {
        return restClient.get("https://gitlab.com/api/v4/projects/" + projectId + "/repository/files/" + path + "/raw",
                String.class).header("PRIVATE-TOKEN", settings.getToken()).getString();
    }

}
