package net.inpercima.mittagstisch.web;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.inpercima.mittagstisch.model.Lunch;

@RestController
public class TomorrowController extends BaseController {

    @GetMapping(value = "/api/tomorrow")
    public List<Lunch> listToday() throws IOException {
        return list(1);
    }
}
