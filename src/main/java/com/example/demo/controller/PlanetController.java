package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Planet;
import com.example.demo.service.PlanetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/planets")
@RequiredArgsConstructor
public class PlanetController {

    private final PlanetService planetService;
    
    @PostMapping
    public ResponseEntity<Planet> create(@Valid @RequestBody Planet planet) {
        Planet planetDb = planetService.create(planet);
        return ResponseEntity.status(HttpStatus.CREATED).body(planetDb);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Planet> get(@PathVariable("id") Long id) {
        return planetService.get(id).map(planet -> ResponseEntity.ok(planet))
        .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Planet> getByName(@PathVariable("name") String name) {
        return planetService.getByName(name).map(planet -> ResponseEntity.ok(planet)).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Planet>> list(@RequestParam(required = false) String terrain, @RequestParam(required = false) String climate) {
        List<Planet> planets = planetService.list(terrain, climate);
        return ResponseEntity.ok(planets);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        planetService.remove(id);
        return ResponseEntity.noContent().build();
    }

}
