package com.example.demo.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "planets")
public class Planet {

  public Planet(String climate, String terrain) {
    this.climate = climate;
    this.terrain = terrain;
  }
  
    public Planet(String name, String climate, String terrain) {
      this.name = name;
      this.climate = climate;
      this.terrain = terrain;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    @NotEmpty
    private String name;
    @Column(nullable = false)
    @NotEmpty
    private String climate;
    @Column(nullable = false)
    @NotEmpty
    private String terrain;
    
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(obj, this);
    }
}
