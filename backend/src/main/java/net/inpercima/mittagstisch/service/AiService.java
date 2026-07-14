package net.inpercima.mittagstisch.service;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.content.Media;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

  private final ChatClient chatClient;

  private final String PROMPT_TEXT = """
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

        Was gilt als Gericht:
        - Nur vollständige Mahlzeiten/Hauptgänge sind als eigenständige Gerichte zu erfassen.
        - Wenn ein Menü aus mehreren Komponenten besteht (z.B. Suppe + Hauptgang + Dessert), fasse diese zu EINEM Eintrag zusammen. Trenne die Komponenten mit " | " im "name"-Feld.
        - Dessert, Nachtisch, Nachspeise sind KEINE eigenständigen Gerichte, sondern Bestandteil eines Menüs.
        - Beilagen (z.B. "Salat", "Brot") sind ebenfalls KEINE eigenständigen Gerichte.
        - Getränke sind KEINE Gerichte.

        Ausgabeformat:
        - Antworte ausschließlich mit reinem JSON, ohne Erklärung, ohne Codeblock, ohne zusätzlichen Text.
        - Nutze exakt folgendes JSON-Format:
        {{
          "today": {{
            "content": [],
            "status": "SUCCESS"
          }},
          "tomorrow": {{
            "content": [],
            "status": "SUCCESS"
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
          {{ "name": "Gerichtname", "price": "5,90 €" }}
        ]
        - wenn kein Abschnitt gefunden wurde, gib im Feld "content" ein leeres Array [] zurück und setze den Status auf "NO_DATA"

        WICHTIG:
        - Gib ausschließlich reines JSON zurück, kein Markdown, kein Codeblock
        - "content" ist ein echtes JSON-Array
        - alle JSON-Keys müssen in doppelten Anführungszeichen stehen
        - Dessert/Nachtisch niemals als eigenständigen Eintrag in "content" aufnehmen
        - Jeder Eintrag in "content" repräsentiert genau eine vollständige Mahlzeit (Hauptgericht bzw. komplettes Menü)
      """;

  public String extractDishesFromText(String content, LocalDate weekStartDate, LocalDate weekEndDate, LocalDate today,
      LocalDate tomorrow) {
    Prompt prompt = buildFromText(content, weekStartDate, weekEndDate, today, tomorrow);
    return analyze(prompt);
  }

  private Prompt buildFromText(String content, LocalDate weekStartDate, LocalDate weekEndDate, LocalDate today,
      LocalDate tomorrow) {
    String promptText = PROMPT_TEXT + """
        Text:
        {content}
        """;

    PromptTemplate promptTemplate = new PromptTemplate(promptText);
    Prompt prompt = promptTemplate.create(Map.of(
        "weekStartDate", weekStartDate.toString(),
        "weekEndDate", weekEndDate.toString(),
        "today", today.toString(),
        "tomorrow", tomorrow.toString(),
        "content", content));
    return prompt;
  }

  public String extractDishesFromImages(List<String> imageUrls, LocalDate weekStartDate, LocalDate weekEndDate,
      LocalDate today, LocalDate tomorrow, MimeType mimeType) {
    Prompt prompt = buildFromImage(imageUrls, weekStartDate, weekEndDate, today, tomorrow, mimeType);
    return analyze(prompt);
  }

  private Prompt buildFromImage(List<String> images, LocalDate weekStartDate, LocalDate weekEndDate,
      LocalDate today, LocalDate tomorrow, MimeType mimeType) {
    String promptText = PROMPT_TEXT
        .formatted(today, tomorrow, weekStartDate, weekEndDate,
            today, today, today, today, tomorrow);

    List<Media> mediaList = images.stream()
        .map(value -> {
          try {
            final URI uri = URI.create(value);
            final MimeType resolvedMimeType = mimeType != null ? mimeType : resolveMimeType(uri);
            return new Media(resolvedMimeType, uri);
          } catch (IllegalArgumentException e) {
            log.warn("Skipping invalid image URL '{}': {}", value, e.getMessage());
            return null;
          }
        })
        .filter(java.util.Objects::nonNull)
        .toList();

    UserMessage userMessage = UserMessage.builder()
        .text(promptText)
        .media(mediaList)
        .build();
    return new Prompt(List.of(userMessage));
  }

  private String analyze(Prompt prompt) {
    return chatClient
        .prompt(prompt)
        .call().content();
  }

  private static MimeType resolveMimeType(URI uri) {
    String path = uri.getPath();
    if (path == null || !path.contains(".")) {
      return MimeTypeUtils.IMAGE_JPEG;
    }
    String ext = path.substring(path.lastIndexOf('.') + 1).toLowerCase();
    return switch (ext) {
      case "png" -> MimeTypeUtils.IMAGE_PNG;
      case "gif" -> MimeTypeUtils.IMAGE_GIF;
      case "webp" -> MimeType.valueOf("image/webp");
      default -> MimeTypeUtils.IMAGE_JPEG;
    };
  }
}
