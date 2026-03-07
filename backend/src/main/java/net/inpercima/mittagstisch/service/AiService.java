package net.inpercima.mittagstisch.service;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
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

  public String extractDishes(String content, LocalDate weekStartDate, LocalDate weekEndDate, LocalDate today,
      LocalDate tomorrow) {
    Prompt prompt = build(content, weekStartDate, weekEndDate, today, tomorrow);
    return analyze(prompt);
  }

  public String extractDishesFromDocument(String imageUrl, LocalDate weekStartDate, LocalDate weekEndDate,
      LocalDate today,
      LocalDate tomorrow) {
    String promptText = buildPromptText(weekStartDate, weekEndDate, today, tomorrow);
    try {
      URL url = URI.create(imageUrl).toURL();
      MimeType mimeType = detectMimeType(imageUrl);
      InputStream in = url.openStream();
      byte[] pdfBytes = in.readAllBytes();
      String base64 = Base64.getEncoder().encodeToString(pdfBytes);
      String dataUrl = "data:" + mimeType.toString() + ";base64," + base64;
      URL data = URI.create(dataUrl).toURL();
      return chatClient.prompt()
          .user(u -> u.text(promptText).media(mimeType, data))
          .call().content();
    } catch (MalformedURLException e) {
      log.error("Invalid image URL '{}': {}", imageUrl, e.getMessage());
      return null;
    } catch (Exception e) {
      log.error("Failed to extract dishes from image '{}': {}", imageUrl, e.getMessage());
      return null;
    }
  }

  private MimeType detectMimeType(String fileUrl) {
    int queryStart = fileUrl.indexOf('?');
    String path = queryStart >= 0 ? fileUrl.substring(0, queryStart) : fileUrl;
    int dotIndex = path.lastIndexOf('.');
    if (dotIndex >= 0) {
      String ext = path.substring(dotIndex + 1).toLowerCase();
      if (ext.equals("png")) {
        return MimeTypeUtils.IMAGE_PNG;
      }
      if (ext.equals("gif")) {
        return MimeTypeUtils.IMAGE_GIF;
      }
      if (ext.equals("pdf")) {
        return MimeType.valueOf("application/pdf");
      }
    }
    return MimeTypeUtils.IMAGE_JPEG;
  }

  private String analyze(Prompt prompt) {
    return chatClient
        .prompt(prompt)
        .call().content();
  }

  private Prompt build(String content, LocalDate weekStartDate, LocalDate weekEndDate, LocalDate today,
      LocalDate tomorrow) {
    String template = buildPromptText(weekStartDate, weekEndDate, today, tomorrow) + """

        Text:
        {content}
        """;

    PromptTemplate promptTemplate = new PromptTemplate(template);
    Prompt prompt = promptTemplate.create(Map.of(
        "content", content));
    return prompt;
  }

  private String buildPromptText(LocalDate weekStartDate, LocalDate weekEndDate, LocalDate today,
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
          {{ "name": string, "price": string }}
        ]
        - wenn kein Abschnitt gefunden wurde, gib im Feld "content" ein leeres Array [] zurück und setze den Status auf "NO_DATA"

        WICHTIG:
        - content ist ein echtes JSON-Array
        - keine Strings statt Array
        - eine Erklärung
        - kein zusätzlicher Text
        """;

    PromptTemplate promptTemplate = new PromptTemplate(template);
    return promptTemplate.create(Map.of(
        "weekStartDate", weekStartDate.toString(),
        "weekEndDate", weekEndDate.toString(),
        "today", today.toString(),
        "tomorrow", tomorrow.toString())).getContents();
  }
}
