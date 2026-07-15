package net.inpercima.mittagstisch.service;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.content.Media;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.CropBox;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

  private final ChatClient chatClient;

  // -----------------------------------------------------------------------
  // Prompt for TEXT-based bistros
  // -----------------------------------------------------------------------
  private final String PROMPT_TEXT = """
      Rolle:
        Du bist ein Parser für Mittagsmenüs.

      Aufgabe:
        Extrahiere aus dem gegebenen Dokument die Mittagsgerichte für {today} und {tomorrow}.

        Ermittle zunächst die gültige Woche. Diese kann unterschiedlich angegeben sein, beispielsweise:

        - Speiseplan vom 01.12. - 05.12.2025
        - 13.07. - 17.07.2026
        - Mo 16.2., Di 17.2.
        - Mittwoch, 25. Februar 2026

        Speichere den Beginn der Woche als "weekStartDate" und das Ende als "weekEndDate".

        Falls kein Wochenzeitraum eindeutig bestimmbar ist, verwende:

        weekStartDate = {weekStartDate}
        weekEndDate = {weekEndDate}

        Die Wochentage können ausgeschrieben oder abgekürzt sein:

        Montag, Dienstag, Mittwoch, Donnerstag, Freitag
        oder
        Mo, Di, Mi, Do, Fr

        Wenn lediglich ein Datum angegeben ist und anschließend Wochentage folgen, berechne den passenden Wochentag.

        --------------------------------------------------
        DOKUMENTANALYSE
        --------------------------------------------------

        Bevor Gerichte extrahiert werden, analysiere zuerst ausschließlich die Struktur des Dokuments.

        Bestimme zunächst, ob der Speiseplan aufgebaut ist als

        - Tabelle
        - Raster (Grid)
        - Karten-/Kachellayout
        - Zeilen
        - Spalten

        Ermittle anschließend die Bereiche der einzelnen Wochentage.

        Jeder Wochentag besitzt einen eigenen räumlichen Bereich.

        Erst nachdem sämtliche Tagesbereiche erkannt wurden, beginne mit der Extraktion.

        Die visuelle Position ist wichtiger als die OCR-Lesereihenfolge.

        Das Dokument darf NICHT als Fließtext interpretiert werden.

        Ordne Gerichte ausschließlich anhand ihrer Position zu.

        Dabei gelten folgende Regeln:

        - Ein Gericht gehört immer zu dem Wochentag, in dessen Bereich es sich befindet.
        - Bei Tabellen gehören alle Gerichte einer Spalte zu diesem Tag.
        - Bei Tabellen mit Zeilen und Spalten gehören alle Gerichte innerhalb der jeweiligen Zelle zu genau einem Tag.
        - Bei Grid- oder Kachellayouts gehören alle Kacheln derselben Zeile zum gleichen Wochentag.
        - Ein Zeilenwechsel im OCR-Text bedeutet NICHT automatisch einen Wechsel zum nächsten Tag.
        - Falls OCR-Reihenfolge und sichtbares Layout voneinander abweichen, ist ausschließlich das sichtbare Layout maßgeblich.
        - Lies immer zuerst den vollständigen Bereich eines Tages bis zum rechten oder unteren Rand und wechsle erst danach zum nächsten Tagesbereich.

        --------------------------------------------------
        EXTRAKTION
        --------------------------------------------------

        Führe die folgenden Schritte in genau dieser Reihenfolge aus:

        1. Ermittle den Wochenzeitraum.
        2. Analysiere das Layout.
        3. Ermittle sämtliche Tagesbereiche.
        4. Extrahiere ALLE Gerichte aller gefundenen Tage.
        5. Ordne jedes Gericht genau einem Tag zu.
        6. Extrahiere anschließend Name und Preis.
        7. Gib im JSON ausschließlich die Gerichte für {today} und {tomorrow} zurück.

        --------------------------------------------------
        WAS GILT ALS GERICHT
        --------------------------------------------------

        Ein Gericht ist genau eine vollständige Hauptmahlzeit.

        Dazu gehören beispielsweise:

        - Schnitzel mit Kartoffeln
        - Pasta mit Tomatensoße
        - Gulasch mit Knödeln

        Nicht als eigenständige Gerichte gelten:

        - Dessert
        - Nachtisch
        - Nachspeise
        - Salat
        - Brot
        - Getränke
        - Obst
        - Beilagen

        Wenn ein Menü aus mehreren Bestandteilen besteht (z.B. Hauptgericht + Dessert + Brot), fasse alles zu EINEM Eintrag zusammen.

        Verbinde die Bestandteile mit

        " | "

        Beispiel:

        "Sächsische Kartoffelsuppe mit Wurzelgemüse und Bockwurstscheiben | Brot | Dessert"

        Ein Tag kann beliebig viele Gerichte enthalten.

        Extrahiere alle Gerichte innerhalb des jeweiligen Tagesbereichs.

        --------------------------------------------------
        AUSGABEFORMAT
        --------------------------------------------------

        Antworte ausschließlich mit reinem JSON.

        Kein Markdown.
        Kein Codeblock.
        Keine Erklärung.

        Nutze exakt folgendes Format:

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
        - wenn weekStartDate ≤ {today} ≤ weekEndDate → Woche aktuell

        Suche anschließend den Bereich für {today} und {tomorrow}.

        Falls der Bereich gefunden wurde:

        [
          {{
            "name": "Gerichtname",
            "price": "5,90 €"
          }}
        ]

        Status = SUCCESS

        Falls kein Bereich gefunden wurde:

        content = []

        Status = NO_DATA

        --------------------------------------------------
        WICHTIG
        --------------------------------------------------

        - Gib ausschließlich gültiges JSON zurück.
        - content ist immer ein JSON-Array.
        - Alle JSON-Keys stehen in doppelten Anführungszeichen.
        - Jedes Gericht darf genau EINEM Wochentag zugeordnet werden.
        - Ein Gericht darf niemals mehreren Tagen zugeordnet werden.
        - Die visuelle Position ist wichtiger als die OCR-Reihenfolge.
        - Extrahiere niemals Dessert, Salat, Brot oder Getränke als eigenständige Gerichte.
      """;

  // -----------------------------------------------------------------------
  // Prompt for IMAGE-based bistros (receives a pre-cropped image showing
  // only today and tomorrow)
  // -----------------------------------------------------------------------
  private final String PROMPT_IMAGE = """
      Rolle:
        Du bist ein Parser für Mittagsmenüs.

      Aufgabe:
        Das Bild zeigt einen Ausschnitt eines Speiseplans mit den Gerichten für {today} und {tomorrow}.

        Ermittle zunächst den Wochenzeitraum. Dieser kann unterschiedlich angegeben sein, beispielsweise:

        - Speiseplan vom 01.12. - 05.12.2025
        - 13.07. - 17.07.2026
        - Mo 16.2., Di 17.2.
        - Mittwoch, 25. Februar 2026

        Speichere den Beginn der Woche als "weekStartDate" und das Ende als "weekEndDate".

        Falls kein Wochenzeitraum eindeutig bestimmbar ist, verwende:

        weekStartDate = {weekStartDate}
        weekEndDate = {weekEndDate}

        Die Wochentage können ausgeschrieben oder abgekürzt sein:

        Montag, Dienstag, Mittwoch, Donnerstag, Freitag
        oder
        Mo, Di, Mi, Do, Fr

        --------------------------------------------------
        BILDANALYSE
        --------------------------------------------------

        Analysiere die Struktur des Bildausschnitts:

        - Tabelle (Spalten pro Tag oder Zeilen pro Tag)
        - Raster (Grid)
        - Karten-/Kachellayout

        Ermittle die Bereiche für {today} und {tomorrow}.

        Die visuelle Position ist wichtiger als die OCR-Lesereihenfolge.

        Ordne Gerichte ausschließlich anhand ihrer Position zu:

        - Bei Tabellen mit Spalten pro Tag gehören alle Gerichte einer Spalte zu diesem Tag.
        - Bei Tabellen oder Grids mit Zeilen pro Tag gehören alle Gerichte einer Zeile zu diesem Tag.
        - Ein Zeilenwechsel im OCR-Text bedeutet NICHT automatisch einen Wechsel zum nächsten Tag.

        --------------------------------------------------
        EXTRAKTION
        --------------------------------------------------

        Führe die folgenden Schritte in genau dieser Reihenfolge aus:

        1. Ermittle den Wochenzeitraum.
        2. Analysiere das Layout.
        3. Ermittle die Bereiche für {today} und {tomorrow}.
        4. Extrahiere alle Gerichte beider Tage.
        5. Ordne jedes Gericht genau einem Tag zu.
        6. Extrahiere Name und Preis.

        --------------------------------------------------
        WAS GILT ALS GERICHT
        --------------------------------------------------

        Ein Gericht ist genau eine vollständige Hauptmahlzeit.

        Dazu gehören beispielsweise:

        - Schnitzel mit Kartoffeln
        - Pasta mit Tomatensoße
        - Gulasch mit Knödeln

        Nicht als eigenständige Gerichte gelten:

        - Dessert
        - Nachtisch
        - Nachspeise
        - Salat
        - Brot
        - Getränke
        - Obst
        - Beilagen

        Wenn ein Menü aus mehreren Bestandteilen besteht (z.B. Hauptgericht + Dessert + Brot), fasse alles zu EINEM Eintrag zusammen.

        Verbinde die Bestandteile mit

        " | "

        Beispiel:

        "Sächsische Kartoffelsuppe mit Wurzelgemüse und Bockwurstscheiben | Brot | Dessert"

        --------------------------------------------------
        AUSGABEFORMAT
        --------------------------------------------------

        Antworte ausschließlich mit reinem JSON.

        Kein Markdown.
        Kein Codeblock.
        Keine Erklärung.

        Nutze exakt folgendes Format:

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
        - wenn weekStartDate ≤ {today} ≤ weekEndDate → Woche aktuell

        Falls der Bereich für einen Tag gefunden wurde:

        [
          {{
            "name": "Gerichtname",
            "price": "5,90 €"
          }}
        ]

        Status = SUCCESS

        Falls kein Bereich gefunden wurde:

        content = []

        Status = NO_DATA

        --------------------------------------------------
        WICHTIG
        --------------------------------------------------

        - Gib ausschließlich gültiges JSON zurück.
        - content ist immer ein JSON-Array.
        - Alle JSON-Keys stehen in doppelten Anführungszeichen.
        - Jedes Gericht darf genau EINEM Wochentag zugeordnet werden.
        - Ein Gericht darf niemals mehreren Tagen zugeordnet werden.
        - Die visuelle Position ist wichtiger als die OCR-Reihenfolge.
        - Extrahiere niemals Dessert, Salat, Brot oder Getränke als eigenständige Gerichte.
      """;

  // -----------------------------------------------------------------------
  // Prompt for the crop-analysis pre-step (IMAGE type only)
  // -----------------------------------------------------------------------
  private final String PROMPT_CROP = """
      Analysiere den Speiseplan im Bild.
      Ermittle die Position des Bereichs, der die Gerichte für {today} und {tomorrow} enthält.
      Gib die Koordinaten als ganzzahlige Prozentsätze der Bildgröße zurück (0 = links/oben, 100 = rechts/unten).

      Antworte ausschließlich mit reinem JSON, kein Markdown, kein Codeblock:

      {{
        "xStart": 0,
        "yStart": 0,
        "xEnd": 100,
        "yEnd": 100
      }}
      """;

  // -----------------------------------------------------------------------
  // Public API
  // -----------------------------------------------------------------------

  public String extractDishesFromText(String content, LocalDate weekStartDate, LocalDate weekEndDate, LocalDate today,
      LocalDate tomorrow) {
    Prompt prompt = buildFromText(content, weekStartDate, weekEndDate, today, tomorrow);
    return analyze(prompt);
  }

  public String extractDishesFromImages(List<String> imageUrls, LocalDate weekStartDate, LocalDate weekEndDate,
      LocalDate today, LocalDate tomorrow, MimeType mimeType) {
    Prompt prompt = buildFromImage(imageUrls, weekStartDate, weekEndDate, today, tomorrow, mimeType);
    return analyze(prompt);
  }

  /**
   * Analyses the given image (data URI or URL) to determine the crop region that
   * contains only the menus for {@code today} and {@code tomorrow}.
   *
   * @return an {@link Optional} with a valid {@link CropBox}, or empty when the
   *         analysis fails or returns an invalid result
   */
  public Optional<CropBox> extractCropBox(String imageDataUri, LocalDate today, LocalDate tomorrow,
      MimeType mimeType) {
    PromptTemplate cropTemplate = new PromptTemplate(PROMPT_CROP);
    Prompt cropPrompt = cropTemplate.create(Map.of(
        "today", today.toString(),
        "tomorrow", tomorrow.toString()));

    MimeType resolvedMimeType = mimeType != null ? mimeType : resolveMimeTypeFromValue(imageDataUri);
    URI uri = URI.create(imageDataUri);
    Media media = new Media(resolvedMimeType, uri);

    UserMessage userMessage = UserMessage.builder()
        .text(cropPrompt.getContents())
        .media(List.of(media))
        .build();

    String response = analyze(new Prompt(List.of(userMessage)));

    try {
      ObjectMapper mapper = new ObjectMapper();
      CropBox cropBox = mapper.readValue(response, CropBox.class);
      if (cropBox.isValid()) {
        log.debug("Crop box determined: {}", cropBox);
        return Optional.of(cropBox);
      }
      log.warn("Received invalid crop box: {}", response);
    } catch (Exception e) {
      log.warn("Failed to parse crop box from response '{}': {}", response, e.getMessage());
    }
    return Optional.empty();
  }

  // -----------------------------------------------------------------------
  // Private helpers
  // -----------------------------------------------------------------------

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

  private Prompt buildFromImage(List<String> images, LocalDate weekStartDate, LocalDate weekEndDate,
      LocalDate today, LocalDate tomorrow, MimeType mimeType) {
    PromptTemplate promptTemplate = new PromptTemplate(PROMPT_IMAGE);
    Prompt textPrompt = promptTemplate.create(Map.of(
        "weekStartDate", weekStartDate.toString(),
        "weekEndDate", weekEndDate.toString(),
        "today", today.toString(),
        "tomorrow", tomorrow.toString()));

    List<Media> mediaList = images.stream()
        .map(value -> {
          try {
            final URI uri = URI.create(value);
            final MimeType resolvedMimeType = mimeType != null ? mimeType : resolveMimeTypeFromValue(value);
            return new Media(resolvedMimeType, uri);
          } catch (IllegalArgumentException e) {
            log.warn("Skipping invalid image URL '{}': {}", value, e.getMessage());
            return null;
          }
        })
        .filter(java.util.Objects::nonNull)
        .toList();

    UserMessage userMessage = UserMessage.builder()
        .text(textPrompt.getContents())
        .media(mediaList)
        .build();
    return new Prompt(List.of(userMessage));
  }

  private String analyze(Prompt prompt) {
    return chatClient
        .prompt(prompt)
        .call().content();
  }

  /**
   * Resolves the MIME type from either a data URI ({@code data:image/png;base64,…})
   * or a plain URL by inspecting the file extension.
   */
  private static MimeType resolveMimeTypeFromValue(String value) {
    if (value != null && value.startsWith("data:")) {
      int semicolon = value.indexOf(';');
      if (semicolon > 5) {
        try {
          return MimeType.valueOf(value.substring(5, semicolon));
        } catch (Exception e) {
          // fall through to default
        }
      }
      return MimeTypeUtils.IMAGE_JPEG;
    }
    try {
      return resolveMimeTypeFromUri(URI.create(value));
    } catch (Exception e) {
      return MimeTypeUtils.IMAGE_JPEG;
    }
  }

  private static MimeType resolveMimeTypeFromUri(URI uri) {
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
