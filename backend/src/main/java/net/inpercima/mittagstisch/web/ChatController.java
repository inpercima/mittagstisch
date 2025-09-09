package net.inpercima.mittagstisch.web;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    private final ChatClient chatClient;

    @Autowired
    public ChatController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @GetMapping("/today")
    public String getTodayMenu() {

        String text = """

                Speiseplan vom08.09-12.09.2025


                                                Montag

                        Köttbullar  Pommes frites, Champignonrahm, Preiselbeeren       8,90 €

                        veg. Maultaschen mit Zwiebelschmelz und Käsesauce                7,80 €


                        Dienstag

                        Burrito mit Rosmarinkartoffeln und Dip                                              8,90 €

                        veg. Wrap mit Rosmarinkartoffeln und Dip                                        8,20 €

                        Soljanka mit Schmand und Brötchen                                                  7,00 €



                        Mittwoch

                        Leberkäse mit Sauerkraut und Kartoffeln                                          8,40 €

                        Pasta-Gemüsepfanne mit Thaisauce                                                 7,80 €

















                        Donnerstag

                        Schweineschnitzel, Kartoffelspalten mit Gemüse

                        und Metaxasauce                                                                                    8,90 €

                        veg. Tortelloni mit Tomatensauce                                                        7,80 €


                        Freitag

                        Knusperfisch mit Kartoffelpüree und Rahmerbsen                           8,90 €

                        Polenta mit Ratatouille                                                                           7,80 €



                                                """;

        String template = """
                Du bist ein Parser für Mittagsmenüs.
                Der Text enthält eine Wochenüberschrift, z.B. "Speiseplan vom08.09-12.09.2025", und darunter die Tage Montag bis Freitag mit Gerichten.

                1. Prüfe, ob die im Titel angegebene Woche die aktuelle Woche ist (heute ist {heute}).
                   - Wenn nicht, gib eine Fehlermeldung zurück.
                2. Wenn ja, bestimme automatisch den heutigen Wochentag.
                3. Extrahiere nur die Gerichte für den heutigen Tag.
                4. Gib die Daten als JSON zurück:
                {{
                  "datum": "{heute}",
                  "tag": "...",
                  "gerichte": [
                    {{ "name": "...", "preis": "..." }}
                  ]
                }}
                Text:
                {text}
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        Prompt prompt = promptTemplate.create(Map.of(
                "heute", LocalDate.now().toString(),
                "text", text));

        OpenAiChatOptions options = OpenAiChatOptions.builder()
        // warum wird model nicht korrekt gelesen und muss hier gesetzt werden?
                .model("gpt-5-nano")
                .build();

        String result = chatClient.prompt(prompt).options(options).call().content();
        return result;
    }
}
