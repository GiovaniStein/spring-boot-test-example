package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import com.example.demo.domain.Planet;
import com.example.demo.domain.QueryBuilder;
import com.example.demo.repository.PlanetRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanetService {

    private final PlanetRepository planetRepository;

    public Optional<Planet> get(Long id) {
        return planetRepository.findById(id);
    }

    public Planet create(Planet planet) {
       return planetRepository.save(planet);
    }

    public Optional<Planet> getByName(String name) {
        return planetRepository.getByName(name);
    }

    public List<Planet> list(String terrain, String climate) {
        Example<Planet> query = QueryBuilder.makeQuery(new Planet(climate, terrain));
        return planetRepository.findAll(query);
    }

    public void remove(Long id) {
        planetRepository.deleteById(id);
    }

}
