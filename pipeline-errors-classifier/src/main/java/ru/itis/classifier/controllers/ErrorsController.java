package ru.itis.classifier.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.classifier.api.Service;
import ru.itis.classifier.dto.ErrorDto;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * 22.06.2022
 *
 * @author Azat Yamanaev
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(ErrorsController.ROOT_URL)
public class ErrorsController {

    public static final String ROOT_URL = "/pipelines";
    private static final String ERRORS_URL = "/errors";
    private static final String TEMPLATES_URL = "/templates";
    private static final String ONE_URL = "/{id}";

    private final Service service;


    @GetMapping(TEMPLATES_URL)
    public ResponseEntity<List<String>> getTemplate(@RequestParam String stage) {

        return ResponseEntity.ok(service.getTemplate(stage));
    }

    @GetMapping(ERRORS_URL)
    public ResponseEntity<ErrorDto> generateError(@RequestParam Long id) {

        return ResponseEntity.ok(service.determineError(id));
    }

    @GetMapping(ERRORS_URL + ONE_URL)
    public String getError(@PathVariable("id") Long id) {
        return result(service.getError(id));
    }

    private String result(ErrorDto dto) {
        if (dto == null) return "";
        StringBuilder builder = new StringBuilder();
        builder.append("{")
                .append("\"stage\"$@;")
                .append(dto.getStage()).append("#$%")
                .append("\"log\"$@;")
                .append(dto.getLog()).append("#$%")
                .append("\"command\"$@;")
                .append(dto.getCommand()).append("#$%")
                .append("\"type\"$@;")
                .append(dto.getType())
                .append("}");
        return builder.toString();
    }
}
