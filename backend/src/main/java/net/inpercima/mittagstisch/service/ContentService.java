package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.time.LocalDate;

import org.jsoup.Jsoup;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import net.inpercima.mittagstisch.model.Bistro;
import net.inpercima.mittagstisch.model.Lunch;

@Service
@AllArgsConstructor
public class ContentService {

    private final AiService aiService;

    private String loadAndParse(String url, String selector) {
        try {
            final var doc = Jsoup.connect(url).get();
            return doc.select(selector).text();
        } catch (IOException e) {
            return "";
        }
    }

    public Lunch generateContent(final Bistro bistro, final int days) {
        String content = loadAndParse(bistro.getUrl(), bistro.getSelector());
        Prompt prompt = aiService.build(
                content, LocalDate.now().plusDays(days), bistro);
        return aiService.analyze(prompt);
    }
}
