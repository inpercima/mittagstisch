package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.inpercima.mittagstisch.entity.LunchEntity;
import net.inpercima.mittagstisch.model.DayEnum;
import net.inpercima.mittagstisch.model.DishDto;

class ContentServiceTest {

    private final ContentService contentService = new ContentService();
    private final ObjectMapper objectMapper = new ObjectMapper();

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
}
