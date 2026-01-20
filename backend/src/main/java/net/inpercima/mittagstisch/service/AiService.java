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
        Du bist ein Parser für Mittagsmenüs.
        Der Text enthält eine Wochentagsübersicht von Montag bis Freitag.
        Der Inhalt variiert je nach Bistro.
        Es ist immer eine Überschrift für die Woche enthalten.
        Sie kann folgendermaßen aussehen: "Speiseplan vom 01.12.-05.12.2025" oder "Mittagstisch KW 49/2025" oder "Wochenkarte".
        Die Tage sind in der Regel mit dem Wochentag benannt, z.B. "Montag", "Dienstag" usw. oder abgekürzt "Mo", "Di" usw.

        1. Nutze folgendes JSON-Format für die Ausgabe:
        {{
          "bistroName": "{bistroName}",
          "url": "{bistroUrl}",
          "content": string,
          "status": string
        }}
        2. Prüfe, ob die im Titel angegebene Woche die aktuelle Woche ist (heute ist {today}).
        3. Wenn nicht, gib im Feld "content" den Wert "Der Speiseplan ist nicht für die aktuelle Woche." zurück und im Feld "status" den Wert "error".
        4. Wenn ja, bestimme automatisch den heutigen Wochentag.
        5. Extrahiere nur die Gerichte für den heutigen Tag.
        6. Gib im Feld "content" eine Liste der Gerichte im folgenden JSON-Format zurück:
        [
          {{ "name": string, "preis": string }}
        ]
        7. Setze für das Feld "status" den Wert "success", wenn Gerichte gefunden wurden.
        Text:
        {content}
        """;

    PromptTemplate promptTemplate = new PromptTemplate(template);
    Prompt prompt = promptTemplate.create(Map.of(
        "today", date.toString(),
        "bistroName", bistro.getName(),
        "bistroUrl", bistro.getUrl(),
        "content", content));
    return prompt;
  }
}
