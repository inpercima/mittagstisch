package net.inpercima.mittagstisch.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import net.inpercima.mittagstisch.model.DayType;

@Entity
@Table(name = "lunch")
public class LunchEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DayType day;

  @Column(nullable = false)
  private String bistro;

  @Column(nullable = false)
  private String meal;

  @Column(nullable = false)
  private LocalDateTime importDate;
}
