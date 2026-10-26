package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpServer;

import net.inpercima.mittagstisch.entity.LunchEntity;
import net.inpercima.mittagstisch.model.DayEnum;
import net.inpercima.mittagstisch.model.DishDto;

class ContentServiceTest {

    private final ContentService contentService = new ContentService();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void extractContentFromWebsite_shouldWaitForJavaScriptRenderedContent() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            byte[] response = """
                    <!doctype html>
                    <html>
                      <body>
                        <main>
                          <script>
                            window.setTimeout(() => {
                              const section = document.createElement('section');
                              section.id = 'mittagstisch';
                              section.innerHTML = '<p>Gericht aus JavaScript</p><img src="/image.png">';
                              document.querySelector('main').append(section);
                            }, 200);
                          </script>
                        </main>
                      </body>
                    </html>
                    """.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream responseBody = exchange.getResponseBody()) {
                responseBody.write(response);
            }
        });
        server.start();

        try {
            String content = contentService.extractContentFromWebsite(
                    "http://127.0.0.1:" + server.getAddress().getPort() + "/",
                    "main section#mittagstisch");
            assertThat(content).isEqualTo("Gericht aus JavaScript");
        } finally {
            server.stop(0);
        }
    }

    @ParameterizedTest(name = "normalizes \"{0}\" to \"{1}\" (stringifiedContent={2})")
    @MethodSource("normalizePriceCases")
    void normalizePrice_shouldNormalizeDifferentFormats(String rawPrice, String expected, boolean stringifiedContent)
            throws Exception {
        String contentArray = objectMapper.writeValueAsString(List.of(new DishDto("Test", rawPrice)));
        String contentValue = stringifiedContent
                ? objectMapper.writeValueAsString(contentArray)
                : contentArray;

        String input = """
                {
                  "today": {
                    "status": "SUCCESS",
                    "content": %s
                  },
                  "tomorrow": {
                    "status": "SUCCESS",
                    "content": []
                  }
                }
                """.formatted(contentValue);

        List<LunchEntity> entities = contentService.prepareLunchEntities(input);
        LunchEntity todayEntity = entities.stream()
                .filter(entity -> entity.getDay() == DayEnum.TODAY)
                .findFirst()
                .orElseThrow();
        List<DishDto> todayItems = objectMapper.readValue(
                todayEntity.getDishes(),
                new TypeReference<List<DishDto>>() {
                });

        assertThat(todayItems.getFirst().price()).isEqualTo(expected);
    }

    private static Stream<Arguments> normalizePriceCases() {
        return Stream.of(
                Arguments.of("14.-", "14,00 €", false),
                Arguments.of("14€", "14 €", false),
                Arguments.of("14,50€", "14,50 €", true));
    }

    @Test
    void extractPdfPagesAsImages_shouldHandleInvalidPdf() throws IOException {
        // This test verifies that the PDF to PNG conversion handles invalid PDFs
        // gracefully
        ContentService service = new ContentService();

        // Test with invalid URL - should handle errors gracefully
        // Note: In a real test environment, you would mock RestTemplate and provide
        // test data
        try {
            Optional<List<String>> result = service.extractPdfPagesAsImages("http://example.com/notfound", "a");
            // If it returns empty, that's expected behavior for invalid PDFs
            assertThat(result).isEmpty();
        } catch (IOException e) {
            // IOException is also acceptable for network/download failures
            assertThat(e).isInstanceOf(IOException.class);
        }
    }
}
