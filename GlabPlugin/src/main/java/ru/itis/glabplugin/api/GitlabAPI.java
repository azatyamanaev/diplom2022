package ru.itis.glabplugin.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.Messages;
import org.springframework.web.client.ResourceAccessException;
import ru.itis.glabplugin.api.dto.ErrorDto;
import ru.itis.glabplugin.api.models.Commit;
import ru.itis.glabplugin.api.models.Pipeline;
import ru.itis.glabplugin.api.models.PipelineJob;
import ru.itis.glabplugin.api.models.Project;
import ru.itis.glabplugin.config.AppSettingsState;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 16.05.2022
 *
 * @author Azat Yamanaev
 */
public class GitlabAPI {

    private static final Logger logger = Logger.getInstance(GitlabAPI.class);

    private static final RestClient restClient = new RestClient();

    public static List<Project> getUserProjects() {
        List<Object> result = (List<Object>) restClient.get("https://gitlab.com/api/v4/projects", Object.class)
                .parameter("membership", true)
                .parameter("per_page", 50)
                .header("PRIVATE-TOKEN", AppSettingsState.getInstance().accessToken).send().orElse(null);
        return result == null ? null : result.stream().map(x -> toProject((LinkedHashMap<String, Object>) x)).collect(Collectors.toList());
    }

    private static Project toProject(LinkedHashMap<String, Object> map) {
        return Project.builder()
                .id((Integer) map.get("id"))
                .name((String) map.get("name"))
                .url((String) map.get("web_url"))
                .description((String) map.get("description"))
                .tracked(true)
                .build();
    }

    public static List<Pipeline> getProjectPipelines(Integer projectId) {
        List<Object> result = (List<Object>) restClient.get("https://gitlab.com/api/v4/projects/" + projectId + "/pipelines", Object.class)
                .parameter("per_page", 80)
                .header("PRIVATE-TOKEN", AppSettingsState.getInstance().accessToken).send().orElse(null);
        return result == null ? null : result.stream().map(x -> toPipeline((LinkedHashMap<String, Object>) x)).collect(Collectors.toList());
    }

    private static Pipeline toPipeline(LinkedHashMap<String, Object> map) {
        return Pipeline.builder()
                .id((Integer) map.get("id"))
                .projectId((Integer) map.get("project_id"))
                .status((String) map.get("status"))
                .source((String) map.get("source"))
                .branchName((String) map.get("ref"))
                .createdAt((String) map.get("created_at"))
                .updatedAt((String) map.get("updated_at"))
                .webUrl((String) map.get("web_url"))
                .duration(map.get("duration") == null ? null : Double.parseDouble(String.valueOf(map.get("duration"))))
                .commit((String) map.get("sha"))
                .build();
    }

    public static List<PipelineJob> getPipelineJobs(Integer projectId, Integer pipelineId) {
        List<Object> result = (List<Object>) restClient.get("https://gitlab.com/api/v4/projects/" + projectId + "/pipelines/" + pipelineId + "/jobs",
                        Object.class)
                .header("PRIVATE-TOKEN", AppSettingsState.getInstance().accessToken).send().orElse(null);
        return result == null ? null : result.stream().map(x -> toJob((LinkedHashMap<String, Object>) x)).collect(Collectors.toList());
    }

    private static PipelineJob toJob(LinkedHashMap<String, Object> map) {
        Pipeline pipeline = toPipeline((LinkedHashMap<String, Object>) map.get("pipeline"));
        return PipelineJob.builder()
                .id(Long.parseLong(String.valueOf(map.get("id"))))
                .name((String) map.get("name"))
                .stage((String) map.get("stage"))
                .status((String) map.get("status"))
                .duration((Double) map.get("duration"))
                .webUrl((String) map.get("web_url"))
                .pipelineId(pipeline.getId())
                .projectId(pipeline.getProjectId())
                .updatedAt((String) map.get("finished_at"))
                .commit(toCommit((LinkedHashMap<String, Object>) map.get("commit")))
                .build();
    }

    private static Commit toCommit(LinkedHashMap<String, Object> map) {
        return Commit.builder()
                .authorName((String) map.get("author_name"))
                .cid((String) map.get("id"))
                .message((String) map.get("message"))
                .shortId((String) map.get("short_id"))
                .title((String) map.get("title"))
                .build();
    }


    public static Pipeline retryCancelPipeline(Integer projectId, Integer pipelineId, String method) {
        Object result = restClient.post("https://gitlab.com/api/v4/projects/" + projectId + "/pipelines/" + pipelineId + "/" + method,
                Object.class).header("PRIVATE-TOKEN", AppSettingsState.getInstance().accessToken).send().orElse(null);

        return toPipeline((LinkedHashMap<String, Object>) result);
    }


    public static void deletePipeline(Integer projectId, Integer pipelineId) {
        restClient.delete("https://gitlab.com/api/v4/projects/" + projectId + "/pipelines/" + pipelineId, Object.class)
                .header("PRIVATE-TOKEN", AppSettingsState.getInstance().accessToken).send();
    }

    public static void retryCancelJob(Integer projectId, Long jobId, String method) {
        Object result = restClient.post("https://gitlab.com/api/v4/projects/" + projectId + "/jobs/" + jobId + "/" + method, Object.class)
                .header("PRIVATE-TOKEN", AppSettingsState.getInstance().accessToken).send().orElse(null);
    }

    public static void eraseJob(Integer projectId, Long jobId) {
        restClient.delete("https://gitlab.com/api/v4/projects/" + projectId + "/jobs/" + jobId + "/erase", Object.class)
                .header("PRIVATE-TOKEN", AppSettingsState.getInstance().accessToken).send();
    }

    public static String getJobLog(Integer projectId, Long jobId) {
        String res = restClient.get("https://gitlab.com/api/v4/projects/" + projectId + "/jobs/" + jobId + "/trace", String.class)
                .header("PRIVATE-TOKEN", AppSettingsState.getInstance().accessToken).getString();
        if (res == null) return null;
        res = res.replace("\u001B", "")
                .replace("[0K", "")
                .replace("[36;1m", "")
                .replace("[32;1m", "")
                .replace("[31;1m", "")
                .replace("[0;m", "");
        return res;
    }

    public static String writeLog(Integer projectId, Integer pipelineId, Long jobId, String stage) {
        try {
            String logData = getJobLog(projectId, jobId);
            if (logData != null) {
                String path = "tmp/logs" + File.separator + projectId + File.separator + pipelineId;

                File file = new File(path);
                file.mkdirs();
                FileWriter writer = new FileWriter(path + File.separator + stage + ".log");
                int length = logData.length();
                for (int i = 0; i < length / 100000 + 1; i++) {
                    String part = logData.substring(i * 100000, Math.min(i * 100000 + 100000, length));
                    writer.write(part);
                }
                writer.close();
                return path;
            } else {
                return null;
            }
        } catch (IOException e) {
            logger.error("Error while saving log to file");
        }
        return null;
    }

    public static String getPipelineError(Integer pipelineId) {
        try {
            String s = restClient.get("http://localhost:8080/pipelines/errors/" + pipelineId, String.class).getString();
            return errorToString(toError(s));
        } catch (ResourceAccessException e) {
            Messages.showErrorDialog("Could not connect to host " + AppSettingsState.getInstance().classifierHost, "Error");
            return null;
        }
    }

    private static ErrorDto toError(String data) {
        if (data != null && !data.isBlank()) {
            List<String> list = List.of(data.split("#\\$%"));

            ErrorDto dto = ErrorDto.builder()
                    .stage(list.get(0).split("\\$@;")[1])
                    .log(list.get(1).split("\\$@;")[1])
                    .command(list.get(2).split("\\$@;")[1])
                    .type(list.get(3).split("\\$@;")[1])
                    .build();
            return dto;
        } else {
            return null;
        }
    }

    private static String errorToString(ErrorDto dto) {
        if (dto == null) return null;
        StringBuilder builder = new StringBuilder();


        builder.append("\"stage\":")
                .append(dto.getStage()).append("\n\n\n")
                .append("\"log\":")
                .append(dto.getLog()).append("\n\n\n")
                .append("\"command\":")
                .append(dto.getCommand()).append("\n")
                .append("\"type\":")
                .append(dto.getType());
        return builder.toString();
    }

}
