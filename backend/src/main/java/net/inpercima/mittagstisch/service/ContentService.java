package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.inpercima.mittagstisch.entity.LunchEntity;
import net.inpercima.mittagstisch.model.DayEnum;
import net.inpercima.mittagstisch.model.DishDto;
import net.inpercima.mittagstisch.model.StatusEnum;

@Service
public class ContentService {

    /**
     * Extracts text content from a web page specified by the given URL and CSS selector.
     *
     * @param url the URL of the web page to extract text from
     * @param selector the CSS selector to locate the desired element
     * @return the extracted text content, or an empty string if the element is not found or an error occurs
     */
    public String extractLunchFromWebsite(String url, String selector) {
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

    /**
     * Prepares a list of LunchEntity objects from the given JSON string containing dish information.
     *
     * @param dishesJson the JSON string containing dish information
     * @return a list of LunchEntity objects
     * @throws Exception if the JSON is invalid or cannot be parsed
     */
    public List<LunchEntity> prepareLunchEntities(String dishesJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode root;
        try {
            root = mapper.readTree(dishesJson);
        } catch (JsonProcessingException e) {
            throw new Exception("Invalid root JSON", e);
        }

        List<LunchEntity> result = new ArrayList<>();

        result.add(parseDayNode(mapper, root.path("today"), DayEnum.TODAY));
        result.add(parseDayNode(mapper, root.path("tomorrow"), DayEnum.TOMORROW));

        return result;
    }

    private LunchEntity parseDayNode(ObjectMapper mapper, JsonNode dayNode, DayEnum day) throws Exception {
        StatusEnum status;
        try {
            status = StatusEnum.valueOf(dayNode.path("status").asText());
        } catch (Exception e) {
            throw new Exception("Invalid status value for " + day + ": " + dayNode.path("status").asText(), e);
        }

        JsonNode contentNode = dayNode.get("content");
        List<DishDto> dishes = extractDishes(mapper, contentNode);
        List<DishDto> normalizedDishes = normalizeDishesPrice(dishes);

        String dishesJson;
        try {
            dishesJson = mapper.writeValueAsString(normalizedDishes);
        } catch (JsonProcessingException e) {
            throw new Exception("Failed to serialize content for " + day, e);
        }

        LunchEntity entity = new LunchEntity();
        entity.setDishes(dishesJson);
        entity.setStatus(status);
        entity.setDay(day);

        return entity;
    }

    /**
    * Extracts a list of DishDto objects from the given JsonNode representing the content.
     * The content can be either a JSON array or a JSON string containing an array.
     * If the content is null, empty, or cannot be parsed, an empty list is returned.
     *
     * @param mapper the ObjectMapper used for JSON parsing
     * @param contentNode the JsonNode representing the content
    * @return a list of DishDto objects
     * @throws Exception if the content cannot be parsed
     */
    private static List<DishDto> extractDishes(ObjectMapper mapper, JsonNode contentNode)
            throws Exception {
        List<DishDto> dishes;
        try {
            if (contentNode == null || contentNode.isNull()) {
                dishes = List.of();

            } else if (contentNode.isArray()) {
                // if content is real JSON array
                dishes = mapper.convertValue(contentNode, new TypeReference<List<DishDto>>() {
                });

            } else if (contentNode.isTextual()) {
                // if content is JSON array as string
                String rawJson = contentNode.asText();
                if (rawJson.isBlank() || rawJson.equals("[]")) {
                    dishes = List.of();
                } else {
                    dishes = mapper.readValue(rawJson, new TypeReference<List<DishDto>>() {
                    });
                }
            } else {
                throw new Exception("Unsupported content type: " + contentNode.getNodeType());
            }
        } catch (JsonProcessingException e) {
            throw new Exception("Failed to parse content", e);
        }
        return dishes;
    }

    /**
    * Normalizes the prices of the given list of DishDto objects.
     *
     * @param dishes the list of DishDto objects
     * @return a list of DishDto objects with normalized prices
     */
    private static List<DishDto> normalizeDishesPrice(List<DishDto> dishes) {
        return dishes.stream()
                .map(dish -> new DishDto(dish.name(), normalizePrice(dish.price())))
                .toList();
    }

    /**
     * Normalizes the given price string by trimming whitespace, replacing certain patterns, and ensuring a consistent format.
     * The following transformations are applied:
     * - If the price is null or blank, it returns an empty string.
     * - Trims leading and trailing whitespace.
     * - Replaces any occurrence of a dot, dash, or similar character at the end of the string with ",00 €".
     * - Ensures that if a number is followed by a euro symbol, there is a space in between (e.g., "5€" becomes "5 €").
     *
     * @param price the price string to normalize
     * @return the normalized price string
     */
    private static String normalizePrice(String price) {
        if (price == null || price.isBlank()) {
            return "";
        }

        String cleaned = price.trim();
        cleaned = cleaned.replaceAll("\\s*[\\.,]?\\s*[.–-]\\s*(€)?\\s*$", ",00 €");
        cleaned = cleaned.replaceAll("(\\d)\\s*€", "$1 €");

        return cleaned;
    }
}
