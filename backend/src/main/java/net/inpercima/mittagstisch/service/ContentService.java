package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.inpercima.mittagstisch.entity.LunchEntity;
import net.inpercima.mittagstisch.model.Day;
import net.inpercima.mittagstisch.model.LunchAiItemDto;
import net.inpercima.mittagstisch.model.Status;

@Service
public class ContentService {

    public String extractText(String url, String selector) {
        try {
            Document doc = Jsoup.connect(url).get();
            Element content = doc.selectFirst(selector);
            if (content == null) {
                return "";
            }

            content.select("img, picture, source, script, style").remove();

            return content.wholeText().replaceAll("\\n{2,}", "\n").trim();
        } catch (IOException e) {
            return "";
        }
    }

    public List<LunchEntity> prepareLunchEntities(String lunchesJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root;
        try {
            root = mapper.readTree(lunchesJson);
        } catch (JsonProcessingException e) {
            throw new Exception("Invalid root JSON", e);
        }

        List<LunchEntity> result = new ArrayList<>();

        result.add(parseDayNode(mapper, root.path("today"), Day.TODAY));
        result.add(parseDayNode(mapper, root.path("tomorrow"), Day.TOMORROW));

        return result;
    }

    private LunchEntity parseDayNode(ObjectMapper mapper, JsonNode dayNode, Day day) throws Exception {
        JsonNode contentNode = dayNode.get("content");
        List<LunchAiItemDto> items = extractContentItems(mapper, contentNode);

        Status status;
        try {
            status = Status.valueOf(dayNode.path("status").asText());
        } catch (Exception e) {
            throw new Exception("Invalid status value for " + day + ": " + dayNode.path("status").asText(), e);
        }

        String lunches = joinNameAndPrice(items);
        LunchEntity entity = new LunchEntity();
        entity.setLunches(lunches);
        entity.setStatus(status);
        entity.setDay(day);

        return entity;
    }

    private static List<LunchAiItemDto> extractContentItems(ObjectMapper mapper, JsonNode contentNode)
            throws Exception {
        List<LunchAiItemDto> items;
        try {
            if (contentNode == null || contentNode.isNull()) {
                items = List.of();

            } else if (contentNode.isArray()) {
                // if content is real JSON array
                items = mapper.convertValue(contentNode, new TypeReference<List<LunchAiItemDto>>() {
                });

            } else if (contentNode.isTextual()) {
                // if content is JSON array as string
                String rawJson = contentNode.asText();
                if (rawJson.isBlank() || rawJson.equals("[]")) {
                    items = List.of();
                } else {
                    items = mapper.readValue(rawJson, new TypeReference<List<LunchAiItemDto>>() {
                    });
                }
            } else {
                throw new Exception("Unsupported content type: " + contentNode.getNodeType());
            }
        } catch (JsonProcessingException e) {
            throw new Exception("Failed to parse content", e);
        }
        return items;
    }

    private static String joinNameAndPrice(List<LunchAiItemDto> items) {
        String lunchesHtml = items.stream()
                .map(item -> {
                    return !item.preis().isBlank() ? item.name() + " – " + normalizePrice(item.preis()) : item.name();
                }).collect(Collectors.joining("<br><br>"));
        return lunchesHtml;
    }

    private static String normalizePrice(String input) {
        String cleaned = input.trim();

        cleaned = cleaned.replaceAll("[.–-]$", ",00 €");
        cleaned = cleaned.replaceAll("(\\d)\\s*€", "$1 €");

        return cleaned;
    }
}
