package net.inpercima.mittagstisch.service;

import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import net.inpercima.mittagstisch.model.Bistro;

@UtilityClass
@Slf4j
public final class Utils {

    public static Bistro readBistroConfig(final String bistroJson, final String bistroId) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode;
        Bistro bistro = new Bistro();
        try {
            jsonNode = mapper.readTree(bistroJson).get(bistroId);
            bistro = mapper.convertValue(jsonNode, Bistro.class);
        } catch (IOException e) {
            log.error("Reading configuration file 'bistro.json' failed.");
        }
        return bistro;
    }
}
