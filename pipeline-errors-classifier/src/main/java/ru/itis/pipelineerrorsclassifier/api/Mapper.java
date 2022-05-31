package ru.itis.pipelineerrorsclassifier.api;

import org.springframework.stereotype.Service;
import ru.itis.pipelineerrorsclassifier.models.*;

import java.util.LinkedHashMap;

/**
 * 30.05.2022
 *
 * @author Azat Yamanaev
 */
@Service
public class Mapper {

    public Project toProject(LinkedHashMap<String, Object> map) {
        return Project.builder()
                .id(Long.parseLong(String.valueOf(map.get("id"))))
                .name((String) map.get("name"))
                .webUrl((String) map.get("web_url"))
                .description((String) map.get("description"))
                .createdAt((String) map.get("created_at"))
                .lastActivityAt((String) map.get("last_activity_at"))
                .build();
    }

    public Pipeline toPipeline(LinkedHashMap<String, Object> map) {
        return Pipeline.builder()
                .id(Long.parseLong(String.valueOf(map.get("id"))))
                .iid((Integer) map.get("iid"))
                .project(Project.builder().id(Long.parseLong(String.valueOf(map.get("project_id")))).build())
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

    public PipelineJob toJob(LinkedHashMap<String, Object> map) {
        Pipeline pipeline = toPipeline((LinkedHashMap<String, Object>) map.get("pipeline"));
        return PipelineJob.builder()
                .id(Long.parseLong(String.valueOf(map.get("id"))))
                .name((String) map.get("name"))
                .stage((String) map.get("stage"))
                .status((String) map.get("status"))
                .duration((Double) map.get("duration"))
                .webUrl((String) map.get("web_url"))
                .pipeline(Pipeline.builder().id(pipeline.getId()).build())
                .projectId(pipeline.getProject().getId())
                .build();
    }


    public MergeRequest toMergeRequest(LinkedHashMap<String, Object> map) {
        return MergeRequest.builder()
                .id(Long.parseLong(String.valueOf(map.get("id"))))
                .iid((Integer) map.get("iid"))
                .project(Project.builder().id(Long.parseLong(String.valueOf(map.get("project_id")))).build())
                .title((String) map.get("title"))
                .webUrl((String) map.get("web_url"))
                .description((String) map.get("description"))
                .state((String) map.get("state"))
                .targetBranch((String) map.get("target_branch"))
                .sourceBranch((String) map.get("source_branch"))
                .draft((Boolean) map.get("draft"))
                .mergeStatus((String) map.get("merge_status"))
                .commit((String) map.get("sha"))
                .mergeCommit((String) map.get("merge_commit_sha"))
                .build();
    }

    
    public Commit toCommit(LinkedHashMap<String, Object> map) {
        return Commit.builder()
                .pid((String) map.get("id"))
                .shortId((String) map.get("short_id"))
                .title((String) map.get("title"))
                .authorName((String) map.get("author_name"))
                .authorEmail((String) map.get("author_email"))
                .createdAt((String) map.get("created_at"))
                .committedAt((String) map.get("committed_date"))
                .message((String) map.get("message"))
                .webUrl((String) map.get("web_url"))
                .build();
    }

    public Diff toDiff(LinkedHashMap<String, Object> map) {
        return Diff.builder()
                .diff((String) map.get("diff"))
                .newPath((String) map.get("new_path"))
                .oldPath((String) map.get("old_path"))
                .aMode((String) map.get("a_mode"))
                .bMode((String) map.get("b_mode"))
                .newFile((Boolean) map.get("new_file"))
                .renamedFile((Boolean) map.get("renamed_file"))
                .deletedFile((Boolean) map.get("deleted_file"))
                .build();
    }
}
