package com.example.demo.planet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;

import com.example.demo.common.PlanetConstants;
import com.example.demo.domain.Planet;
import com.example.demo.domain.QueryBuilder;
import com.example.demo.repository.PlanetRepository;
import com.example.demo.service.PlanetService;

@ExtendWith(MockitoExtension.class)
public class PlanetServiceTest {

    @Mock
    private PlanetRepository planetRepository;
    @InjectMocks
    private PlanetService planetService;

    @Test
    public void createPlanetWithValidData() {
        when(planetRepository.save(PlanetConstants.PLANET)).thenReturn(PlanetConstants.PLANET);
        Planet sut = planetService.create(PlanetConstants.PLANET);
        Assertions.assertThat(sut).isEqualTo(PlanetConstants.PLANET);
    }

    @Test
    public void createPlanetWithInvalidData() {
        when(planetRepository.save(PlanetConstants.INVALID_PLANET)).thenThrow(RuntimeException.class);
        Assertions.assertThatThrownBy(() -> planetService.create(PlanetConstants.INVALID_PLANET)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getPlanetByExistingIdReturnPlanet() {
        when(planetRepository.findById(anyLong())).thenReturn(Optional.of(PlanetConstants.PLANET));
        Optional<Planet> sut = planetService.get(1L);
        Assertions.assertThat(sut).isNotEmpty();
        Assertions.assertThat(sut.get()).isEqualTo(PlanetConstants.PLANET);
    }

    @Test
    public void getPlanetByUnexistingIdReturnEmpty() {
        when(planetRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Planet> sut = planetService.get(1L);
        Assertions.assertThat(sut).isEmpty();
    }

    @Test
    public void getPlanetByExistingNameReturnPlanet() {
        when(planetRepository.getByName(PlanetConstants.PLANET.getName())).thenReturn(Optional.of(PlanetConstants.PLANET));
        Optional<Planet> sut = planetService.getByName(PlanetConstants.PLANET.getName());
        Assertions.assertThat(sut).isNotEmpty();
        Assertions.assertThat(sut.get()).isEqualTo(PlanetConstants.PLANET);
    }

    @Test
    public void getPlanetByUnexistingNameReturnsEmpty() {
        final String name = "Unexisting name";
        when(planetRepository.getByName(name)).thenReturn(Optional.empty());
        Optional<Planet> sut = planetService.getByName(name);
        Assertions.assertThat(sut).isEmpty();
    }

    @Test
    public void listPlanetsReturnsAllPlanets() {
        List<Planet> planets = new ArrayList<>() {{
            add(PlanetConstants.PLANET);
        }};

        Example<Planet> query = QueryBuilder.makeQuery(new Planet(PlanetConstants.PLANET.getClimate(), PlanetConstants.PLANET.getTerrain()));
        when(planetRepository.findAll(query)).thenReturn(planets);

        List<Planet> sut = planetService.list(PlanetConstants.PLANET.getTerrain(), PlanetConstants.PLANET.getClimate());

        Assertions.assertThat(sut).isNotEmpty();
        Assertions.assertThat(sut).hasSize(1);
        Assertions.assertThat(sut.get(0)).isEqualTo(PlanetConstants.PLANET);
    }

    @Test
    public void listPlanetsReturnsNoPlanets() {
        when(planetRepository.findAll(any())).thenReturn(Collections.emptyList());
        List<Planet> sut = planetService.list(PlanetConstants.PLANET.getTerrain(), PlanetConstants.PLANET.getClimate());
        Assertions.assertThat(sut).isEmpty();
    }

    @Test
    public void removePlanetWithExistingId() {
        Assertions.assertThatCode(() -> planetService.remove(1L)).doesNotThrowAnyException();
    }

    @Test
    public void removePlanetWithUnexistingId() {
        doThrow(new RuntimeException()).when(planetRepository).deleteById(99L);
        Assertions.assertThatThrownBy(() -> planetService.remove(99L)).isInstanceOf(RuntimeException.class);
    }

    
}
