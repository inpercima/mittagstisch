package net.inpercima.mittagstisch.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import net.inpercima.mittagstisch.entity.BistroEntity;

public interface BistroRepository
        extends JpaRepository<BistroEntity, Long> {

    List<BistroEntity> findByLockedFalse();
}
