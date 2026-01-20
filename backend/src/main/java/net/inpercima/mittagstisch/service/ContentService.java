package net.inpercima.mittagstisch.service;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

@Service
public class ContentService {

    public String extractText(String url, String selector) {
        try {
            final var doc = Jsoup.connect(url).get();
            return doc.select(selector).text();
        } catch (IOException e) {
            return "";
        }
    }
}
