package com.example.springboot_demo.helpers;

import java.util.Map;
import java.util.stream.Collectors;

public class FilterParameter {

    // Phân loại theo keyword
    public static String filterKeyword(Map<String, String[]> parameters) {
        return parameters.containsKey("keyword") ? parameters.get("keyword")[0] : null;
    }

    // Phân loại theo các filter mà đơn giản: page, perpage, keyword, sort
    public static Map<String, String> filterSimple(Map<String, String[]> parameters) {
        return parameters.entrySet().stream()
                .filter(entry -> (entry.getKey().contains("page") ||
                        entry.getKey().contains("perpage") ||
                        entry.getKey().contains("keyword") ||
                        entry.getKey().contains("publish")))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue()[0]));
    }

    // Phân loại theo các filter phức tạp
    public static Map<String, Map<String, String>> filterComplex(Map<String, String[]> parameters) {
        return parameters.entrySet().stream()
                .filter(entry -> entry.getKey().contains("["))
                .collect(Collectors.groupingBy(
                        entry -> entry.getKey().split("\\[")[0],
                        Collectors.toMap(
                                entry -> entry.getKey().split("\\[")[1].replace("]", ""),
                                entry -> entry.getValue()[0])));
    }

    // Phân loại theo filter kiểu ngày tháng { start_date: ..., end_date: ... }
    public static Map<String, String> filterDateRange(Map<String, String[]> parameters) {
        Map<String, String> dateRangeFilters = new java.util.HashMap<>();

        if (parameters.containsKey("start_date")) {
            dateRangeFilters.put("start_date", parameters.get("start_date")[0]);
        }

        if (parameters.containsKey("end_date")) {
            dateRangeFilters.put("end_date", parameters.get("end_date")[0]);
        }

        return dateRangeFilters;
    }
}
