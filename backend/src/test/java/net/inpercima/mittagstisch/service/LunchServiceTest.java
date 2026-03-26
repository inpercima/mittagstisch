package net.inpercima.mittagstisch.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.data.domain.Pageable;

import net.inpercima.mittagstisch.entity.BistroEntity;
import net.inpercima.mittagstisch.entity.LunchEntity;
import net.inpercima.mittagstisch.model.DayEnum;
import net.inpercima.mittagstisch.model.StatusEnum;
import net.inpercima.mittagstisch.repository.LunchRepository;

class LunchServiceTest {

    private final LunchRepository lunchRepository = mock(LunchRepository.class);
    private final BistroService bistroService = mock(BistroService.class);

    private final LunchService lunchService = new LunchService(
            lunchRepository,
            bistroService,
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

    @Test
    void getDataByDay_returnsToday_whenTodayHasData() {
        LocalDate thursday = LocalDate.of(2026, 3, 26); // Thursday
        LunchEntity entity = buildLunchEntity(thursday);

        when(bistroService.count()).thenReturn(1L);
        when(lunchRepository.findByImportDateAndDay(any(LocalDate.class), any(DayEnum.class), any(Pageable.class)))
                .thenReturn(List.of(entity));

        try (MockedStatic<LocalDate> mockedDate = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
            mockedDate.when(LocalDate::now).thenReturn(thursday);

            var result = lunchService.getDataByDay(DayEnum.TODAY);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).importDate()).isEqualTo(thursday);
            // yesterday should not be queried since today returned data
            verify(lunchRepository, never()).findByImportDateAndDay(eq(thursday.minusDays(1)), any(DayEnum.class), any(Pageable.class));
        }
    }

    @Test
    void getDataByDay_fallsBackToYesterday_onWeekdayWhenTodayEmpty() {
        LocalDate thursday = LocalDate.of(2026, 3, 26); // Thursday
        LocalDate wednesday = thursday.minusDays(1);
        LunchEntity entity = buildLunchEntity(wednesday);

        when(bistroService.count()).thenReturn(1L);
        when(lunchRepository.findByImportDateAndDay(any(LocalDate.class), any(DayEnum.class), any(Pageable.class)))
                .thenAnswer(invocation -> {
                    LocalDate date = invocation.getArgument(0);
                    return thursday.equals(date) ? List.of() : List.of(entity);
                });

        try (MockedStatic<LocalDate> mockedDate = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
            mockedDate.when(LocalDate::now).thenReturn(thursday);

            var result = lunchService.getDataByDay(DayEnum.TODAY);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).importDate()).isEqualTo(wednesday);
        }
    }

    @ParameterizedTest(name = "getDataByDay does NOT fall back on {0}")
    @MethodSource("noFallbackDays")
    void getDataByDay_doesNotFallBack_onMondayOrWeekend(LocalDate date) {
        when(bistroService.count()).thenReturn(1L);
        when(lunchRepository.findByImportDateAndDay(any(LocalDate.class), any(DayEnum.class), any(Pageable.class)))
                .thenReturn(List.of());

        try (MockedStatic<LocalDate> mockedDate = Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
            mockedDate.when(LocalDate::now).thenReturn(date);

            var result = lunchService.getDataByDay(DayEnum.TODAY);

            assertThat(result).isEmpty();
            verify(lunchRepository, times(1))
                    .findByImportDateAndDay(any(LocalDate.class), any(DayEnum.class), any(Pageable.class));
        }
    }

    private static Stream<Arguments> noFallbackDays() {
        return Stream.of(
                Arguments.of(LocalDate.of(2026, 3, 30)), // Monday
                Arguments.of(LocalDate.of(2026, 3, 28)), // Saturday
                Arguments.of(LocalDate.of(2026, 3, 29))  // Sunday
        );
    }

    private static LunchEntity buildLunchEntity(LocalDate importDate) {
        BistroEntity bistro = new BistroEntity();
        bistro.setName("TestBistro");
        bistro.setUrl("http://example.com");

        LunchEntity entity = new LunchEntity();
        entity.setBistro(bistro);
        entity.setImportDate(importDate);
        entity.setStatus(StatusEnum.SUCCESS);
        entity.setDishes("[]");
        entity.setDay(DayEnum.TODAY);
        return entity;
    }
}
