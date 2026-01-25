package net.inpercima.mittagstisch.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import net.inpercima.mittagstisch.entity.BistroEntity;
import net.inpercima.mittagstisch.repository.BistroRepository;

@Service
@AllArgsConstructor
public class BistroService {

    private final BistroRepository bistroRepository;

    public List<BistroEntity> findAll() {
        return bistroRepository.findAll();
    }

    public long count() {
        return bistroRepository.count();
    }

}
