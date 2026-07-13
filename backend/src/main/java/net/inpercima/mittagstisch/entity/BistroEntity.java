package net.inpercima.mittagstisch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.inpercima.mittagstisch.model.TypeEnum;

@Entity
@Table(name = "bistro")
@Getter
@Setter
@NoArgsConstructor
public class BistroEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @Column
    private String selector;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeEnum type;

    @Column
    private boolean locked;
}
