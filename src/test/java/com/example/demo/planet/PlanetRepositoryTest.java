package com.example.demo.planet;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.test.context.jdbc.Sql;

import com.example.demo.common.PlanetConstants;
import com.example.demo.domain.Planet;
import com.example.demo.domain.QueryBuilder;
import com.example.demo.repository.PlanetRepository;

@DataJpaTest
public class PlanetRepositoryTest {

    @Autowired
    private PlanetRepository planetRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    public void afterEach() {
        PlanetConstants.PLANET.setId(null);
    }

    @Test
    public void createPlanetWithValidData() {
       Planet planet = planetRepository.save(PlanetConstants.PLANET);
       Planet sut = testEntityManager.find(Planet.class, planet.getId());
       Assertions.assertThat(sut).isNotNull();
       Assertions.assertThat(sut.getName()).isEqualTo(PlanetConstants.PLANET.getName());
       Assertions.assertThat(sut.getClimate()).isEqualTo(PlanetConstants.PLANET.getClimate());
       Assertions.assertThat(sut.getTerrain()).isEqualTo(PlanetConstants.PLANET.getTerrain());
    }

    @Test
    public void createPlanetWithInvalidData() {
        Planet emptyPlanet = new Planet();
        Planet invalidPlanet = new Planet("", "", "");

        Assertions.assertThatThrownBy(() -> planetRepository.save(emptyPlanet)).isInstanceOf(RuntimeException.class);
        Assertions.assertThatThrownBy(() -> planetRepository.save(invalidPlanet)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void createPlanetWithExistingName() {
        Planet planet = testEntityManager.persistFlushFind(PlanetConstants.PLANET);
        testEntityManager.detach(planet);
        planet.setId(null);

        Assertions.assertThatThrownBy(() -> planetRepository.save(planet)).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void getPlanetByExistingId() {
       Planet planet = testEntityManager.persistFlushFind(PlanetConstants.PLANET);

       Optional<Planet> planetOpt = planetRepository.findById(planet.getId());

       Assertions.assertThat(planetOpt).isNotEmpty();
       Assertions.assertThat(planetOpt.get()).isEqualTo(planet);
    }

    @Test
    public void getPlanetByUnexistingId() {
        Optional<Planet> planetOpt = planetRepository.findById(1L);
        Assertions.assertThat(planetOpt).isEmpty();
    }

    @Test
    public void getPlanetByExistingName() throws Exception {
        Planet planet = testEntityManager.persistFlushFind(PlanetConstants.PLANET);

        Optional<Planet> planetOpt = planetRepository.getByName(planet.getName());

        Assertions.assertThat(planetOpt).isNotEmpty();
        Assertions.assertThat(planetOpt.get()).isEqualTo(planet);
    }

    @Test
    public void getPlanetByUnexistingName() throws Exception {
        Optional<Planet> planetOpt = planetRepository.getByName("");
        Assertions.assertThat(planetOpt).isEmpty();
    }

    @Sql(scripts = "/import_planets.sql")
    @Test
    public void listPlanetsFilteredPlanets() throws Exception {
        Example<Planet> queryWithoutFilters = QueryBuilder.makeQuery(new Planet());
        Example<Planet> queryWithFilters = QueryBuilder.makeQuery(new Planet(PlanetConstants.TATOOINE.getClimate(), PlanetConstants.TATOOINE.getTerrain()));

        List<Planet> responseWithoutFilters = planetRepository.findAll(queryWithoutFilters);
        List<Planet> responseWithFilters = planetRepository.findAll(queryWithFilters);

        Assertions.assertThat(responseWithoutFilters).isNotEmpty();
        Assertions.assertThat(responseWithoutFilters).hasSize(3);
        Assertions.assertThat(responseWithFilters).isNotEmpty();
        Assertions.assertThat(responseWithFilters).hasSize(1);
        Assertions.assertThat(responseWithFilters.get(0)).isEqualTo(PlanetConstants.TATOOINE);
    }

    @Test
    public void listPlanetsNoPlanets() throws Exception {
        Example<Planet> query = QueryBuilder.makeQuery(new Planet());
        List<Planet> response = planetRepository.findAll(query);

        Assertions.assertThat(response).isEmpty();
    }

    @Test
    public void removePlanetWithExistingId() {
        Planet planet = testEntityManager.persistFlushFind(PlanetConstants.PLANET);

        planetRepository.deleteById(planet.getId());

        Planet removedPlanet = testEntityManager.find(Planet.class, planet.getId());
        Assertions.assertThat(removedPlanet).isNull();
    }

    @Test
    public void removePlanetWithUnexistingId() {
        Assertions.assertThatThrownBy(() -> planetRepository.deleteById(1L)).isInstanceOf(EmptyResultDataAccessException.class);
    }

}
