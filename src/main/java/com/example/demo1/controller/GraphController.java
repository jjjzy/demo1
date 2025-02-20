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

    /**
     * 返回 HTML 页面，并提供 Dropdown 选项
     */
    @GetMapping("/graph")
    public String graphPage(Model model) {
        List<Map<String, Object>> dropdownOptions = loadOptionsData();
        model.addAttribute("dropdownOptions", dropdownOptions);
        return "graph"; // 渲染 graph.html
    }

    /**
     * 获取 Graph 数据，并转换为正确的格式
     */
    @GetMapping("/getData")
    @ResponseBody
    public Map<String, List<String>> getGraphData(@RequestParam String key) {
        Map<String, Map<String, List<String>>> jsonData = loadGraphData();

        // 获取 key 对应的数据
        Map<String, List<String>> rawEdges = jsonData.getOrDefault(key, new HashMap<>());

        // 转换为正确的 "source-target" 结构
        return transformEdges(rawEdges);
    }

    /**
     * 读取 Graph JSON 数据
     */
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

    /**
     * 读取 Dropdown 选项 JSON
     */
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

    /**
     * 转换 JSON 数据结构，使其符合 "source-target" 逻辑
     */
    private Map<String, List<String>> transformEdges(Map<String, List<String>> rawEdges) {
        List<String> nodes = new ArrayList<>();

        // 先收集所有节点的顺序
        for (List<String> values : rawEdges.values()) {
            nodes.addAll(values);
        }

        // 生成 source-target 连接
        Map<String, List<String>> transformedGraph = new HashMap<>();
        for (int i = 0; i < nodes.size() - 1; i++) {
            String source = nodes.get(i);
            String target = nodes.get(i + 1);
            transformedGraph.computeIfAbsent(source, k -> new ArrayList<>()).add(target);
        }

        return transformedGraph;
    }
}
