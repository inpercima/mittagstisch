package net.inpercima.mittagstisch.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import net.inpercima.mittagstisch.entity.LunchEntity;
import net.inpercima.mittagstisch.model.Day;

public interface LunchRepository extends JpaRepository<LunchEntity, Long> {

    List<LunchEntity> findByImportDateAndDay(
            LocalDate importDate,
            Day day,
            Pageable pageable);
}
