package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.entity.LunchEntity;
import net.inpercima.mittagstisch.model.DayEnum;
import net.inpercima.mittagstisch.model.DishDto;
import net.inpercima.mittagstisch.model.StatusEnum;
import technology.tabula.ObjectExtractor;
import technology.tabula.Page;
import technology.tabula.RectangularTextContainer;
import technology.tabula.Table;
import technology.tabula.extractors.SpreadsheetExtractionAlgorithm;

@Slf4j
@Service
public class ContentService {

    /**
     * Extracts text content from a web page specified by the given URL and CSS
     * selector.
     *
     * @param url      the URL of the web page to extract text from
     * @param selector the CSS selector to locate the desired element
     * @return the extracted text content, or an empty string if the element is not
     *         found or an error occurs
     */
    public String extractLunchFromWebsite(String url, String selector) {
        try {
            Document doc = Jsoup.connect(url).get();
            Element content = doc.selectFirst(selector);
            if (content == null) {
                log.warn("No content found for selector '{}' on page '{}'.", selector, url);
                return "";
            }

            content.select("img, picture, source, script, style").remove();

            return content.wholeText().replaceAll("\\n{2,}", "\n").trim();
        } catch (IOException e) {
            log.error("Failed to extract content from '{}' with selector '{}': {}", url, selector, e.getMessage());
            return "";
        }
    }

    /**
     * Extracts the absolute URL of the first PDF found by the given CSS selector on
     * the specified page.
     *
     * @param url         the URL of the web page to search for a PDF
     * @param pdfSelector the CSS selector to locate the PDF element
     * @return the absolute URL of the PDF, or an empty string if not found or an
     *         error occurs
     */
    public String extractPdfUrlFromWebsite(String url, String pdfSelector) {
        try {
            Document doc = Jsoup.connect(url).get();
            Element pdf = doc.selectFirst(pdfSelector);
            if (pdf == null) {
                return "";
            }
            return pdf.attr("abs:href");
        } catch (IOException e) {
            log.error("Failed to extract PDF URL from '{}' with selector '{}': {}", url, pdfSelector,
                    e.getMessage());
            return "";
        }
    }

    /**
     * Extracts text from a PDF document by parsing its table structure.
     *
     * @param pdfUrl   the URL of the PDF document to extract text from
     * @param selector the CSS selector to locate the PDF element (not used in this
     *                 method, but kept for consistency)
     * @return the extracted text content with table data separated by pipes and
     *         newlines
     * @throws IOException if the PDF cannot be downloaded or processed
     */
    public String extractLunchFromPdf(String url, String selector) throws IOException {
        String pdfUrl = this.extractPdfUrlFromWebsite(url, selector);
        if (pdfUrl == null || pdfUrl.isBlank()) {
            throw new IllegalArgumentException("PDF URL cannot be null or blank");
        }

        RestTemplate restTemplate = new RestTemplate();
        byte[] pdfBytes = restTemplate.getForObject(pdfUrl, byte[].class);

        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new IOException("PDF content is empty or could not be downloaded from: " + pdfUrl);
        }

        List<String> resultRows = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(pdfBytes);
                ObjectExtractor extractor = new ObjectExtractor(document)) {

            Page page = extractor.extract(1);
            SpreadsheetExtractionAlgorithm sea = new SpreadsheetExtractionAlgorithm();
            List<Table> tables = sea.extract(page);

            for (Table table : tables) {
                for (List<RectangularTextContainer> row : table.getRows()) {
                    String rowText = row.stream()
                            .map(RectangularTextContainer::getText)
                            .map(String::trim)
                            .filter(text -> !text.isEmpty())
                            .collect(Collectors.joining(" | "));

                    if (!rowText.isEmpty()) {
                        log.debug("Extracted table row: {}", rowText);
                        resultRows.add(rowText);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to extract lunch from PDF '{}': {}", pdfUrl, e.getMessage());
            throw new IOException("PDF lunch extraction failed: " + e.getMessage(), e);
        }

        return String.join("\n", resultRows);
    }

    /**
     * Prepares a list of LunchEntity objects from the given JSON string containing
     * dish information.
     *
     * @param dishesJson the JSON string containing dish information
     * @return a list of LunchEntity objects
     * @throws JsonProcessingException  if the JSON is invalid or cannot be parsed
     * @throws IllegalArgumentException if dishesJson is null or blank
     */
    public List<LunchEntity> prepareLunchEntities(String dishesJson) throws JsonProcessingException {
        if (dishesJson == null || dishesJson.isBlank()) {
            throw new IllegalArgumentException("Dishes JSON cannot be null or blank");
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root;

        try {
            root = mapper.readTree(dishesJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse dishes JSON: {}", e.getMessage());
            throw new JsonProcessingException("Invalid root JSON") {
                @Override
                public String getMessage() {
                    return "Invalid root JSON: " + e.getMessage();
                }
            };
        }

        List<LunchEntity> result = new ArrayList<>();
        result.add(parseDayNode(mapper, root.path("today"), DayEnum.TODAY));
        result.add(parseDayNode(mapper, root.path("tomorrow"), DayEnum.TOMORROW));

        return result;
    }

    private LunchEntity parseDayNode(ObjectMapper mapper, JsonNode dayNode, DayEnum day)
            throws JsonProcessingException {
        StatusEnum status;
        String statusText = dayNode.path("status").asText();

        try {
            status = StatusEnum.valueOf(statusText);
        } catch (IllegalArgumentException e) {
            log.error("Invalid status value for {}: '{}'", day, statusText);
            throw new JsonProcessingException("Invalid status value for " + day + ": " + statusText) {
                @Override
                public String getMessage() {
                    return "Invalid status value for " + day + ": " + statusText;
                }
            };
        }

        JsonNode contentNode = dayNode.get("content");
        List<DishDto> dishes = extractDishes(mapper, contentNode);
        List<DishDto> normalizedDishes = normalizeDishesPrice(dishes);

        String dishesJson;
        try {
            dishesJson = mapper.writeValueAsString(normalizedDishes);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize content for {}: {}", day, e.getMessage());
            throw new JsonProcessingException("Failed to serialize content for " + day) {
                @Override
                public String getMessage() {
                    return "Failed to serialize content for " + day + ": " + e.getMessage();
                }
            };
        }

        LunchEntity entity = new LunchEntity();
        entity.setDishes(dishesJson);
        entity.setStatus(status);
        entity.setDay(day);

        return entity;
    }

    /**
     * Extracts a list of DishDto objects from the given JsonNode representing the
     * content.
     * The content can be either a JSON array or a JSON string containing an array.
     * If the content is null, empty, or cannot be parsed, an empty list is
     * returned.
     *
     * @param mapper      the ObjectMapper used for JSON parsing
     * @param contentNode the JsonNode representing the content
     * @return a list of DishDto objects
     * @throws JsonProcessingException if the content cannot be parsed
     */
    private static List<DishDto> extractDishes(ObjectMapper mapper, JsonNode contentNode)
            throws JsonProcessingException {
        if (contentNode == null || contentNode.isNull()) {
            return List.of();
        }

        try {
            if (contentNode.isArray()) {
                // if content is real JSON array
                return mapper.convertValue(contentNode, new TypeReference<List<DishDto>>() {
                });

            } else if (contentNode.isTextual()) {
                // if content is JSON array as string
                String rawJson = contentNode.asText();
                if (rawJson.isBlank() || rawJson.equals("[]")) {
                    return List.of();
                }
                return mapper.readValue(rawJson, new TypeReference<List<DishDto>>() {
                });

            } else {
                throw new JsonProcessingException("Unsupported content type: " + contentNode.getNodeType()) {
                    @Override
                    public String getMessage() {
                        return "Unsupported content type: " + contentNode.getNodeType();
                    }
                };
            }
        } catch (JsonProcessingException e) {
            log.error("Failed to parse dishes content: {}", e.getMessage());
            throw new JsonProcessingException("Failed to parse content") {
                @Override
                public String getMessage() {
                    return "Failed to parse content: " + e.getMessage();
                }
            };
        }
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
     * Normalizes the given price string by trimming whitespace, replacing certain
     * patterns, and ensuring a consistent format.
     * The following transformations are applied:
     * - If the price is null or blank, it returns an empty string.
     * - Trims leading and trailing whitespace.
     * - Replaces any occurrence of a dot, dash, or similar character at the end of
     * the string with ",00 €".
     * - Ensures that if a number is followed by a euro symbol, there is a space in
     * between (e.g., "5€" becomes "5 €").
     *
     * @param price the price string to normalize
     * @return the normalized price string
     */
    private static String normalizePrice(String price) {
        return Optional.ofNullable(price)
                .filter(p -> !p.isBlank())
                .map(String::trim)
                .map(p -> p.replaceAll("\\s*[\\.,]?\\s*[.–-]\\s*(€)?\\s*$", ",00 €"))
                .map(p -> p.replaceAll("(\\d)\\s*€", "$1 €"))
                .orElse("");
    }
}
