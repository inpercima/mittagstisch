package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import net.inpercima.mittagstisch.model.DayEnum;
import net.inpercima.mittagstisch.model.StatusEnum;
import net.inpercima.mittagstisch.repository.LunchRepository;

class LunchServiceTest {

    private final LunchService lunchService = new LunchService(
            mock(LunchRepository.class),
            mock(BistroService.class),
            mock(ContentService.class),
            mock(AiService.class));

    @ParameterizedTest(name = "buildStatusMessage({0}, {1}) on {2} -> \"{3}\"")
    @MethodSource("statusMessageCases")
    void buildStatusMessage_shouldReturnExpectedMessage(StatusEnum status, DayEnum day, LocalDate today, String expected) {
        LocalDate tomorrow = today.plusDays(1);
        assertThat(lunchService.buildStatusMessage(status, day, today, tomorrow)).isEqualTo(expected);
    }

    private static Stream<Arguments> statusMessageCases() {
        // A Friday in a known week so next/prev week dates are predictable
        LocalDate friday = LocalDate.of(2026, 2, 27);

        return Stream.of(
                Arguments.of(StatusEnum.NO_DATA, DayEnum.TODAY, friday,
                        "Für den 27.02.2026 liegen leider keine Informationen vor."),
                Arguments.of(StatusEnum.NO_DATA, DayEnum.TOMORROW, friday,
                        "Für den 28.02.2026 liegen leider keine Informationen vor."),
                Arguments.of(StatusEnum.NEXT_WEEK, DayEnum.TODAY, friday,
                        "Die Speisekarte ist bereits für die nächste Woche verfügbar (02.03.2026 bis 06.03.2026)."),
                Arguments.of(StatusEnum.OUTDATED, DayEnum.TODAY, friday,
                        "Die Speisekarte ist noch von letzter Woche (16.02.2026 bis 20.02.2026)."),
                Arguments.of(StatusEnum.SUCCESS, DayEnum.TODAY, friday, null));
    }
}
