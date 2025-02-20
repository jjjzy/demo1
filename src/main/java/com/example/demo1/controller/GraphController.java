package com.example.demo1.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/api")
public class GraphController {

    private static final String GRAPH_JSON_PATH = "graphData.json";
    private static final String OPTIONS_JSON_PATH = "options.json";

    @GetMapping("/graph")
    public String graphPage(Model model) {
        List<Map<String, Object>> dropdownOptions = loadOptionsData();
        model.addAttribute("dropdownOptions", dropdownOptions);
        return "index";
    }

    @GetMapping("/getData")
    @ResponseBody
    public Map<String, List<String>> getGraphData(@RequestParam String key) {
        Map<String, Map<String, List<String>>> jsonData = loadGraphData();
        return jsonData.getOrDefault(key, new HashMap<>()); // 直接返回数据，不转换
    }

    private Map<String, Map<String, List<String>>> loadGraphData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource(GRAPH_JSON_PATH);
            return objectMapper.readValue(resource.getFile(), new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private List<Map<String, Object>> loadOptionsData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ClassPathResource resource = new ClassPathResource(OPTIONS_JSON_PATH);
            return objectMapper.readValue(resource.getFile(), new TypeReference<>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    @GetMapping("/getAllNodes")
    @ResponseBody
    public Set<String> getAllNodes() {
        Map<String, Map<String, List<String>>> jsonData = loadGraphData();
        Set<String> allNodes = new HashSet<>();

        jsonData.values().forEach(edges -> edges.forEach((source, targets) -> {
//            allNodes.add(source);
            allNodes.addAll(targets);
        }));



        return allNodes;
    }
}