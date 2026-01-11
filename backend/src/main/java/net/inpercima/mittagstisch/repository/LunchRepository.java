package net.inpercima.mittagstisch.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import net.inpercima.mittagstisch.entity.LunchEntity;

public interface LunchRepository extends JpaRepository<LunchEntity, Long> {
}
