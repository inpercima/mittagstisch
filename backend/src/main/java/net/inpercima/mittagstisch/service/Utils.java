package net.inpercima.mittagstisch.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Bistro;

@UtilityClass
@Slf4j
public final class Utils {

    public static Bistro readBistroConfigById(final String bistroJson, final String bistroId) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode;
        Bistro bistro = new Bistro();
        try {
            jsonNode = mapper.readTree(bistroJson).get(bistroId);
            bistro = mapper.convertValue(jsonNode, Bistro.class);
        } catch (IOException e) {
            log.error("Reading configuration file 'bistro.json' failed.", e);
        }
        return bistro;
    }

    public static List<Bistro> readBistroConfig(final String bistroJson) {
        ObjectMapper mapper = new ObjectMapper();
        List<Bistro> bistros = new ArrayList<>();
        try {
            JsonNode root = mapper.readTree(bistroJson);
            for (JsonNode node : root) {
                Bistro bistro = mapper.convertValue(node, Bistro.class);
                bistros.add(bistro);
            }
        } catch (IOException e) {
            log.error("Reading configuration file 'bistro.json' failed.", e);
        }
        return bistros;
    }
}
