package net.inpercima.mittagstisch.service;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import net.inpercima.mittagstisch.entity.BistroEntity;

@Service
@RequiredArgsConstructor
public class AiService {

  private final ChatClient chatClient;

  public String extractLunches(String content, LocalDate date, final BistroEntity bistro) {
    Prompt prompt = build(content, date, bistro);
    return analyze(prompt);
  }

  private String analyze(Prompt prompt) {
    return chatClient
        .prompt(prompt)
        .call().content();
  }

  private Prompt build(String content, LocalDate date, final BistroEntity bistro) {
    String template = """
        Rolle:
        Du bist ein Parser für Mittagsmenüs.
        Aufgabe:
        Extrahiere aus dem gegebenen Text die Mittagsgerichte von Montag bis Freitag für den angegebenen Tag in einer Wochenübersicht.
        Informationen:
        Es ist immer eine Überschrift für die Woche enthalten.
        Sie kann folgendermaßen aussehen: "Speiseplan vom 01.12.-05.12.2025" oder "Mittagstisch KW 49/2025" oder "Wochenkarte".
        Die Tage sind in der Regel mit dem Wochentag benannt, z.B. "Montag", "Dienstag" usw. oder abgekürzt "Mo", "Di" usw.
        Anweisungen:
        - Nutze folgendes JSON-Format für die Ausgabe:
        {{
          "content": string,
          "status": string
        }}
        - Prüfe, ob der heutige Tag innerhalb der angegebenen Woche liegt (heute ist {today}).
        - Wenn der heutige Tag vor der angegebenen Woche liegt, gib im Feld "content" den Wert "[]" und im Feld "status" den Wert "OUTDATED" zurück.
        - Wenn der heutige Tag nach der angegebenen Woche liegt, gib im Feld "content" den Wert "[]" und im Feld "status" den Wert "NEXT_WEEK" zurück.
        - Wenn der heutige Tag innerhalb der angegebenen Woche liegt, extrahiere die Gerichte für diesen Tag.
        - Gib im Feld "content" eine Liste der Gerichte im folgenden JSON-Format zurück:
        [
          {{ "name": string, "preis": string }}
        ]
        - Setze für das Feld "status" den Wert "SUCCESS", wenn Gerichte gefunden wurden.
        - Wurden keine Gerichte gefunden, setzte für das Feld "status" den Wert "NO_DATA".
        WICHTIG:
        - Gib das Feld "content" als echtes JSON-Array zurück
        - Das Feld darf KEIN String sein
        - Verwende KEINE Escapes
        Text:
        {content}
        """;

    PromptTemplate promptTemplate = new PromptTemplate(template);
    Prompt prompt = promptTemplate.create(Map.of(
        "today", date.toString(),
        "content", content));
    return prompt;
  }
}
