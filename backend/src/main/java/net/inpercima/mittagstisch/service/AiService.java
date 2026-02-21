package net.inpercima.mittagstisch.service;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiService {

  private final ChatClient chatClient;

  public String extractLunches(String content, LocalDate weekStartDate, LocalDate weekEndDate, String todayWeekday, String tomorrowWeekDay) {
    Prompt prompt = build(content, weekStartDate, weekEndDate, todayWeekday, tomorrowWeekDay);
    return analyze(prompt);
  }

  private String analyze(Prompt prompt) {
    return chatClient
        .prompt(prompt)
        .call().content();
  }

  private Prompt build(String content, LocalDate weekStartDate, LocalDate weekEndDate, String todayWeekday, String tomorrowWeekDay) {
    String template = """
        Rolle:
        Du bist ein Parser für Mittagsmenüs.

        Gegebene Parameter:
        weekStartDate: {weekStartDate}
        weekEndDate: {weekEndDate}
        todayWeekday: {todayWeekday}
        tomorrowWeekday: {tomorrowWeekday}

        Aufgabe:
        Extrahiere aus dem gegebenen Text die Mittagsgerichte für todayWeekday und tomorrowWeekday.

        Die Wocheninformationen werden unterschiedlich gegeben, z.B.:
        "Speiseplan vom 01.12.-05.12.2025"
        Mo 16.2., Di 17.2.
        Mittwoch, 25. Februar 2026
        Die Tage sind benannt als:
        Montag, Dienstag, Mittwoch, Donnerstag, Freitag oder abgekürzt Mo, Di, Mi, Do, Fr

        Ausgabeformat:
        - Nutze folgendes JSON-Format für die Ausgabe:
        {{
          today: {{
          "content": string,
          "status": string
        }},
        {{
          tomorrow: {{
          "content": string,
          "status": string
        }}

        Status-Regeln:
        - wenn Gerichte gefunden -> "SUCCESS"
        - wenn kein Abschnitt gefunden -> "NO_DATA"
        - wenn die Woche vor weekStartDate liegt -> "OUTDATED"
        - wenn die Woche nach weekEndDate liegt -> "NEXT_WEEK"

        - Prüfe, ob der Datumsbereich für weekStartDate bis weekEndDate passt.
        - Suche den Abschnitt für todayWeekday.
        - Suche den Abschnitt für tomorrowWeekday.
        - Wenn der jeweilige Abschnitt gefunden wurde, extrahiere die Gerichte für diesen Tag.
        - Gib im Feld "content" eine Liste der Gerichte im folgenden JSON-Format zurück:
        [
          {{ "name": string, "preis": string }}
        ]
        - Wenn kein Abschnitt gefdunden wurde, gib im Feld "content" ein leeres Array [] zurück.

        WICHTIG:
        - content ist ein echtes JSON-Array
        - keine Strings statt Array
        - eine Erklärung
        - kein zusätzlicher Text
      
        Text:
        {content}
        """;

    PromptTemplate promptTemplate = new PromptTemplate(template);
    Prompt prompt = promptTemplate.create(Map.of(
        "weekStartDate", weekStartDate.toString(),
        "weekEndDate", weekEndDate.toString(),
        "todayWeekday", todayWeekday,
        "tomorrowWeekDate", tomorrowWeekDay,
        "content", content));
    return prompt;
  }
}
