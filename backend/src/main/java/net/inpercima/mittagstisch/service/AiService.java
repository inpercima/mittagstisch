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

  public String extractLunches(String content, LocalDate weekStartDate, LocalDate weekEndDate, LocalDate today,
      LocalDate tomorrow) {
    Prompt prompt = build(content, weekStartDate, weekEndDate, today, tomorrow);
    return analyze(prompt);
  }

  private String analyze(Prompt prompt) {
    return chatClient
        .prompt(prompt)
        .call().content();
  }

  private Prompt build(String content, LocalDate weekStartDate, LocalDate weekEndDate, LocalDate today,
      LocalDate tomorrow) {
    String template = """
        Rolle:
        Du bist ein Parser für Mittagsmenüs.

        Aufgabe:
        Extrahiere aus dem gegebenen Text die Mittagsgerichte für {today} und {tomorrow}.
        Ermittle dazu die gültige Wocheninformation, die unterschiedlich auf den Webseiten stehen kann, z.B.:
        - "Speiseplan vom 01.12.-05.12.2025"
        - Mo 16.2., Di 17.2.
        - Mittwoch, 25. Februar 2026
        Speichere den Anfang der Woche als weekStartDate und das Ende der Woche als weekEndDate ab, um zu prüfen, ob die Woche aktuell, veraltet oder in der Zukunft liegt.
        Ist das nicht ermittelbar, setze weekStartDate auf {weekStartDate} und weekEndDate auf {weekEndDate}.

        Die Tage sind benannt als:
        Montag, Dienstag, Mittwoch, Donnerstag, Freitag oder abgekürzt Mo, Di, Mi, Do, Fr

        Ausgabeformat:
        - Nutze folgendes JSON-Format für die Ausgabe:
        {{
          today:
          {{
            "content": string,
            "status": string
          }}
        }},
        {{
          tomorrow:
          {{
            "content": string,
            "status": string
          }}
        }}

        Status-Regeln:
        - wenn {today} > weekEndDate → OUTDATED
        - wenn {today} < weekStartDate → NEXT_WEEK
        - wenn weekStartDate ≤ {today} ≤ weekEndDate → Woche ist aktuell → weiter prüfen
        - suche den Abschnitt für {today}
        - suche den Abschnitt für {tomorrow}
        - wenn der jeweilige Abschnitt gefunden wurde, extrahiere die Gerichte für diesen Tag.
        - gib im Feld "content" eine Liste der Gerichte im folgenden JSON-Format zurück und setze den Status auf "SUCCESS"
        [
          {{ "name": string, "preis": string }}
        ]
        - wenn kein Abschnitt gefunden wurde, gib im Feld "content" ein leeres Array [] zurück und setze den Status auf "NO_DATA"

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
        "today", today.toString(),
        "tomorrow", tomorrow.toString(),
        "content", content));
    return prompt;
  }
}
