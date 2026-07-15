package net.inpercima.mittagstisch.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
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
import net.inpercima.mittagstisch.model.CropBox;
import net.inpercima.mittagstisch.model.DayEnum;
import net.inpercima.mittagstisch.model.DishDto;
import net.inpercima.mittagstisch.model.StatusEnum;

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
    public String extractContentFromWebsite(String url, String selector) {
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
     * Extracts the absolute URL of the first image found by the given CSS selector
     * on the specified page.
     *
     * @param url      the URL of the web page to search for an image
     * @param selector the CSS selector to locate the image element
     * @return an optional containing the first absolute image URL found, or empty
     *         if none are found or an error occurs
     */
    public Optional<String> extractImageUrlFromWebsite(String url, String selector) {
        try {
            Document doc = Jsoup.connect(url).get();
            return doc.select(selector).stream()
                    .map(img -> img.attr("abs:src"))
                    .filter(src -> !src.isBlank())
                    .findFirst();
        } catch (IOException e) {
            log.error("Failed to extract image URL from '{}' with selector '{}': {}", url, selector,
                    e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Extracts the absolute URL of the first PDF found by the given CSS selector on
     * the specified page.
     *
     * @param url      the URL of the web page to search for a PDF
     * @param selector the CSS selector to locate the PDF element
     * @return the absolute URL of the PDF, or an empty string if not found or an
     *         error occurs
     */
    public Optional<String> extractPdfUrlFromWebsite(String url, String selector) {
        try {
            Document doc = Jsoup.connect(url).get();
            Element pdf = doc.selectFirst(replaceKwPlaceholder(selector));
            if (pdf == null) {
                return Optional.empty();
            }
            return Optional.of(pdf.attr("abs:href"));
        } catch (IOException e) {
            log.error("Failed to extract PDF URL from '{}' with selector '{}': {}", url, selector,
                    e.getMessage());
            return Optional.empty();
        }
    }

    private static String getCurrentWeekRange() {
        int currentWeek = LocalDate.now().get(WeekFields.of(Locale.GERMANY).weekOfWeekBasedYear());

        int fromWeek = (currentWeek % 2 == 0) ? currentWeek : currentWeek - 1;
        int toWeek = fromWeek + 1;

        return "KW " + fromWeek + " bis " + toWeek;
    }

    private static String replaceKwPlaceholder(String selector) {
        return selector.replace("{KW}", getCurrentWeekRange());
    }

    /**
     * Downloads an image from the given URL and returns it as a base64-encoded
     * data URI (e.g. {@code data:image/jpeg;base64,…}).
     *
     * @param url the absolute URL of the image to download
     * @return the data URI string
     * @throws IOException if the image cannot be downloaded or is empty
     */
    public String downloadImageAsDataUri(String url) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        byte[] imageBytes = restTemplate.getForObject(url, byte[].class);

        if (imageBytes == null || imageBytes.length == 0) {
            throw new IOException("Image content is empty or could not be downloaded from: " + url);
        }

        String mimeType = resolveMimeTypeFromUrl(url);
        String base64 = Base64.getEncoder().encodeToString(imageBytes);
        return "data:" + mimeType + ";base64," + base64;
    }

    /**
     * Crops a base64-encoded data URI image according to the given {@link CropBox}
     * (coordinates expressed as percentages 0–100 of the original image dimensions)
     * and returns the result as a new data URI.
     *
     * @param dataUri the source image data URI (data:image/…;base64,…)
     * @param cropBox the crop region as percentages
     * @return the cropped image as a data URI
     * @throws IOException if the image cannot be decoded or encoded
     */
    public String cropDataUri(String dataUri, CropBox cropBox) throws IOException {
        int commaIndex = dataUri.indexOf(',');
        String header = dataUri.substring(0, commaIndex);
        byte[] imageBytes = Base64.getDecoder().decode(dataUri.substring(commaIndex + 1));

        BufferedImage original = ImageIO.read(new ByteArrayInputStream(imageBytes));
        if (original == null) {
            throw new IOException("Could not decode image from data URI");
        }

        int x = (int) (original.getWidth() * cropBox.xStart() / 100.0);
        int y = (int) (original.getHeight() * cropBox.yStart() / 100.0);
        int w = (int) (original.getWidth() * (cropBox.xEnd() - cropBox.xStart()) / 100.0);
        int h = (int) (original.getHeight() * (cropBox.yEnd() - cropBox.yStart()) / 100.0);

        // Clamp to image bounds
        w = Math.max(1, Math.min(w, original.getWidth() - x));
        h = Math.max(1, Math.min(h, original.getHeight() - y));

        BufferedImage cropped = original.getSubimage(x, y, w, h);

        String format = header.contains("png") ? "png" : "jpeg";
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(cropped, format, baos);

        String base64Cropped = Base64.getEncoder().encodeToString(baos.toByteArray());
        log.debug("Cropped image from {}x{} to {}x{} (crop box: {})",
                original.getWidth(), original.getHeight(), w, h, cropBox);
        return header + "," + base64Cropped;
    }

    private static String resolveMimeTypeFromUrl(String url) {
        String lower = url.toLowerCase();
        if (lower.contains(".png")) return "image/png";
        if (lower.contains(".gif")) return "image/gif";
        if (lower.contains(".webp")) return "image/webp";
        return "image/jpeg";
    }

    /**
     * Extracts PDF pages as PNG images and returns them as data URIs.
     *
     * @param url      the URL of the web page to search for a PDF
     * @param selector the CSS selector to locate the PDF element
     * @return an optional containing a list of data URIs
     *         (data:image/png;base64,...)
     *         for each PDF page
     * @throws IOException if the PDF cannot be downloaded or processed
     */
    public Optional<List<String>> extractPdfPagesAsImages(String url, String selector) throws IOException {
        Optional<String> pdfUrlOptional = this.extractPdfUrlFromWebsite(url, selector);
        if (pdfUrlOptional.isEmpty()) {
            log.warn("PDF URL is blank for url '{}' with selector '{}'", url, selector);
            return Optional.empty();
        } else {
            log.info("Found PDF URL: {}", pdfUrlOptional.get());
        }

        RestTemplate restTemplate = new RestTemplate();
        byte[] pdfBytes = restTemplate.getForObject(pdfUrlOptional.get(), byte[].class);

        if (pdfBytes == null || pdfBytes.length == 0) {
            log.error("PDF content is empty or could not be downloaded from: {}", pdfUrlOptional.get());
            return Optional.empty();
        }

        List<String> imageDataUris = new ArrayList<>();

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            int pageCount = document.getNumberOfPages();

            for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                // Render PDF page to image at 300 DPI for good quality
                BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 300);

                // Convert BufferedImage to PNG bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                byte[] imageBytes = baos.toByteArray();

                // Convert to base64 data URI
                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                String dataUri = "data:image/png;base64," + base64Image;
                imageDataUris.add(dataUri);

                log.debug("Converted PDF page {} to PNG image (size: {} bytes)", pageIndex + 1, imageBytes.length);
            }
        } catch (Exception e) {
            log.error("Failed to convert PDF '{}' to images: {}", pdfUrlOptional.get(), e.getMessage());
            throw new IOException("PDF to image conversion failed: " + e.getMessage(), e);
        }

        return Optional.of(imageDataUris);
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
