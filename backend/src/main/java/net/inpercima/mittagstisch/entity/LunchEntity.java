package net.inpercima.mittagstisch.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.inpercima.mittagstisch.model.Day;
import net.inpercima.mittagstisch.model.Status;

@Entity
@Table(name = "lunch")
@Getter
@Setter
@NoArgsConstructor
public class LunchEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Day day;

  @ManyToOne(fetch = FetchType.EAGER)
  private BistroEntity bistro;

  @Column(nullable = false)
  private String lunches;

  @Column(nullable = false)
  private LocalDate importDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Status status;
}
